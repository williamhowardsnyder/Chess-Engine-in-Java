package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.AI.*;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.*;

// Table manages the visual component for the JChess program and extends the Observable class
public class Table extends Observable {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private Move computerMove;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(900, 850);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(600, 500);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(15,15);
    private final static String pieceImagePath = "art/standard/";

    private final Color lightTileColor = (new Color(255,250,205)).brighter();
    private final Color darkTileColor = (new Color(89,62,26)).brighter();
    private final Color highlightTileColor = new Color(220, 203, 71, 163);

    // Turns Table into a singleton (only one table in our implementation)
    private static final Table INSTANCE = new Table();

    // Constructs a Table object
    private Table() {
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;

        // this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST); // TODO: Temporarily removed until fixed
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        center(this.gameFrame);
        this.gameFrame.setVisible(true);
    }

    // Returns the only instance of Table
    public static Table get() {
        return INSTANCE;
    }

    // Initializes the visual component of the program
    public void show() {
        invokeLater(new Runnable() {
            public void run() {
                Table.get().getMoveLog().clear();
                Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
            }
        });
    }

    // Returns the game setup
    private GameSetup getGameSetup() {
        return this.gameSetup;
    }
    // Returns the chess board
    private Board getGameBoard() {
        return this.chessBoard;
    }

    // Creates and returns a JMenuBar for the table
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    // Centers the given JFrame
    private static void center(final JFrame frame) {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;
        frame.setLocation(x, y);
    }

    // Creates and returns the file menu
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        // Menu option for loading a PGN file
        final JMenuItem openPGN = new JMenuItem("Load PGN File"); // TODO: Add functionality
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that PGN file!");
            }
        });
        fileMenu.add(openPGN);
        // Menu option for exiting the program
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    // Creates and returns a preference menu
    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlightCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves");
        legalMoveHighlightCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlightCheckbox.isSelected();
            }
        });
        preferencesMenu.add(legalMoveHighlightCheckbox);

        return preferencesMenu;
    }

    //
    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());

            }
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    // This chunk of code handles getting the AI player's move, and updating the board with it
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Notifies AI player to make its move after human makes their move
    private void setupUpdate(GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup); // Tells the AI player to make their move
    }

    // TableGameAIWatcher implements Observer and notifies the AI player to make a move on their turn.
    private static class TableGameAIWatcher implements Observer {
        @Override
        public void update(final Observable o, final Object arg) {
            Player currentPlayer = Table.get().getGameBoard().getCurrentPlayer();
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getCurrentPlayer()) &&
               !currentPlayer.isInCheckmate() && !currentPlayer.isInStalemate()) { // If AI player and the game isn't over
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if(currentPlayer.isInCheckmate()) {
                // TODO: Make this be a popup dialog or something
                System.out.println(currentPlayer.toString() + " is in checkmate!");
            } else if(currentPlayer.isInStalemate()) {
                System.out.println(currentPlayer.toString() + " is in stalemate!");
            } else if (currentPlayer.getActivePieces().size() == 1 &&
                       currentPlayer.getOpponent().getActivePieces().size() == 1) {
                System.out.print("Draw!");
                System.exit(0);
            }
        }
    }

    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }
    public void updateComputerMove(final Move move) {
        this.computerMove = move;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }
    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }
    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }
    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    // Manages getting the AI player's move
    private static class AIThinkTank extends SwingWorker<Move, String> {
        private AIThinkTank() { }

        @Override
        protected Move doInBackground() throws Exception {
            MoveStrategy AI;
            if(Table.get().getGameSetup().difficulty().equals(Difficulty.LIKE_ACTUALLY_REALLY_EASY)) {
                AI = new Rando();
            } else if (Table.get().getGameSetup().difficulty().equals(Difficulty.EASY)) {
                AI = new Minimax(4);
            } else if (Table.get().getGameSetup().difficulty().equals(Difficulty.MEDIUM)) {
                AI = new Minimax(6);
            } else { // Difficulty.HARD
                AI = new Minimax(10);
            }
            final Move bestMove = AI.execute(Table.get().getGameBoard());
            return bestMove;
        }
        @Override
        public void done() {
            try {
                final Move bestMove = get();

                // Update all the gui components
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                // Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Represents the two different directions the board might face, and manages the visual changes that occur
    // due to flipping the board
    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                ArrayList<TilePanel> reversed = new ArrayList<>();
                for (int i = boardTiles.size() - 1; i >= 0; i--) {
                    reversed.add(boardTiles.get(i));
                }
                return reversed/*List.reverse(boardTiles)*/;
            }
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    // Represents the visual component for the Board
    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        // Constructs a BoardPanel object
        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) { // adds 64 tiles to the list of board tiles
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        // Draws all the tiles on the BoardPanel object
        public void drawBoard(final Board board) {
            removeAll();
            // for loop executed over the traverse() list which depends on the board direction
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    // Represents the game history
    public static class MoveLog {
        // Wraps the list class to make it more intuitive
        private final List<Move> moves;
        public MoveLog() {
            this.moves = new ArrayList<>();
        }
        public List<Move> getMoves() {
            return this.moves;
        }
        public void addMove(Move move) {
            this.moves.add(move);
        }
        public int size() {
            return this.moves.size();
        }
        public void clear() {
            this.moves.clear();
        }
        public Move removeMove(int index) {
            return this.moves.remove(index);
        }
        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    public enum PlayerType {
        HUMAN,
        COMPUTER
    }

    public enum Difficulty {
        LIKE_ACTUALLY_REALLY_EASY,
        EASY,
        MEDIUM,
        HARD
    }

    // Represents the visual component for a Tile object
    private class TilePanel extends JPanel {
        private final int tileID;

        TilePanel (final BoardPanel boardPanel, final int tileID) {
            super(new GridBagLayout());
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor(lightTileColor, darkTileColor);
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(!gameSetup.isAIPlayer(chessBoard.getCurrentPlayer())) { // Can only click on human's turn
                        if (isLeftMouseButton(e)) {
                            if (sourceTile == null) {                            // If first of two clicks
                                sourceTile = chessBoard.getTile(tileID);           // get Tile from board
                                humanMovedPiece = sourceTile.getPiece();           // get piece on Tile
                                if (humanMovedPiece == null) {                     // If no piece on tile
                                    sourceTile = null;                               // restart
                                }
                            } else {                                            // Second of two clicks
                                destinationTile = chessBoard.getTile(tileID);     // set destination
                                final Move move = Move.MoveFactory.createMove(chessBoard,
                                        sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                                final MoveTransition moveTransition = chessBoard.getCurrentPlayer().makeMove(move);
                                if (moveTransition.getMoveStatus().isDone()) {
                                    chessBoard = moveTransition.getTransitionBoard();
                                    moveLog.addMove(move);
                                }
                                sourceTile = null;
                                destinationTile = null;
                                humanMovedPiece = null;
                            }
                            invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    gameHistoryPanel.redo(chessBoard, moveLog);
                                    takenPiecesPanel.redo(moveLog);
                                    if (gameSetup.isAIPlayer(chessBoard.getCurrentPlayer())) { // AI Player is playing
                                        Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                    }
                                    boardPanel.drawBoard(chessBoard);
                                }
                            });

                        } else if (isRightMouseButton(e)) {
                            // Cancels any previous piece selections
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    }
                }
                // TODO: Implement drag-and-drop piece moving.
                @Override
                public void mousePressed(final MouseEvent e) { }
                @Override
                public void mouseReleased(final MouseEvent e) { }
                @Override
                public void mouseEntered(final MouseEvent e) { }
                @Override
                public void mouseExited(final MouseEvent e) { }
            });
            validate();
        }

        // Draws a Tile on the given board.
        public void drawTile(Board board) {
            assignTilePieceIcon(board);
            // highlightLegalMoves(board);
            Piece pieceOnTile = board.getTile(this.tileID).getPiece();
            if(pieceOnTile != null && pieceOnTile.equals(humanMovedPiece)) { // Highlights the selected piece
                assignTileColor(highlightTileColor, highlightTileColor);
            } else {
                assignTileColor(lightTileColor, darkTileColor);
            }
            highlightLegalSquares(board);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll(); // removes previous drawings
            if (board.getTile(tileID).isTileOccupied()) {
                try {
                    // Example: white bishop is represented as "WB.gif"
                    final BufferedImage image = ImageIO.read(new File(pieceImagePath +
                            board.getTile(this.tileID).getPiece().getPieceAlliance().toString().substring(0, 1) +
                            board.getTile(tileID).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Highlights the legal moves for the humanMovedPiece on a given board using a green dot
        private void highlightLegalMoves(final Board board) {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    MoveTransition transition = board.getCurrentPlayer().makeMove(move);
                    if(!transition.getMoveStatus().isDone()){ // Move does something illegal
                        continue;
                    }
                    if (move.getDestinationCoordinate() == this.tileID) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // Highlights the legal moves for the humanMovedPiece on a given board by making squares lighter or darker
        private void highlightLegalSquares(final Board board) {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    MoveTransition transition = board.getCurrentPlayer().makeMove(move);
                    if(!transition.getMoveStatus().isDone()){ // Move does something illegal
                        continue;
                    }
                    if (move.getDestinationCoordinate() == this.tileID) {
                        try {
                            this.assignTileColor(highlightTileColor.brighter(), highlightTileColor.darker());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // Returns a collection of moves that the selected piece can execute on a given board.
        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null &&
                    humanMovedPiece.getPieceAlliance() == board.getCurrentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        // Assigns a tile one of the two given colors.
        private void assignTileColor(Color light, Color dark) {
            if (BoardUtils.EIGHTH_RANK[tileID] || BoardUtils.SIXTH_RANK[tileID] ||
                BoardUtils.FOURTH_RANK[tileID] || BoardUtils.SECOND_RANK[tileID]) { // Checks to see if in even row
                setBackground(this.tileID % 2 == 0 ? light : dark);
            } else { // In an odd row
                setBackground(this.tileID % 2 == 1 ? light : dark);
            }
        }
    }

}
