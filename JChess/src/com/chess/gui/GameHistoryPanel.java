package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.gui.Table.MoveLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// GameHistoryPanel represents the moves that have taken place in the current game. Also, handles the visual
// representation of those moves.
public class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(150, 600);

    // Constructs a GameHistoryPanel
    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final MoveLog moveHistory) {
        int currentRow = 0;
        this.model.clear();

        // TODO: Doesn't stay as check for minimax
        // Writes each move from the moveHistory in algebraic notation to the panel
        for(final Move move : moveHistory.getMoves()) {
            final String moveText = move.toString() + calculateCheckMateHash(move.getBoard());
            if(move.getMovedPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            } else if(move.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        // Notation for Checkmate on final move
        if(moveHistory.getMoves().size() > 0) {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.getMoves().size() - 1);
            final String moveText = lastMove.toString();
            if(lastMove.getMovedPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText + calculateCheckMateHash(board), currentRow, 0);
            } else if(lastMove.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText + calculateCheckMateHash(board), currentRow - 1, 1);
            }
        }

        // Allows for scrolling if the number of moves exceeds the size of the panel
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    // Calculates and returns the additional String notation for check and checkmate on a given board.
    private String calculateCheckMateHash(Board board) {
        if (board.getCurrentPlayer().getOpponent().isInCheckmate()) {
            return "#";
        } else if(board.getCurrentPlayer().getOpponent().isInCheckmate()) {
            return "+";
        }
        return "";
    }

    // Represents DataModel for holding the game history. Extends the DefaultTableModel.
    private static class DataModel extends DefaultTableModel {
        private final List<Row> values;
        private static final String[] NAMES = {"WHITE", "BLACK"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if(this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            final Row currentRow = this.values.get(row);
            if (col == 0) {
                return currentRow.getWhiteMove();
            } else if (col == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col) {
            final Row currentRow;
            if (this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if (col == 0) {
                currentRow.setWhiteMove(value.toString());
                fireTableRowsInserted(row, row); // adds a new row if the column is 0
            } else if (col == 1) {
                currentRow.setBlackMove(value.toString());
                fireTableCellUpdated(row, col);
            }
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int col) {
            return NAMES[col];
        }
    }

    // Represents a Row in in the game history panel that stores a white and black move.
    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row() { }
        // getters
        public String getWhiteMove() {
            return this.whiteMove;
        }
        public String getBlackMove() {
            return this.blackMove;
        }
        // setters
        public void setWhiteMove(final String move) {
            this.whiteMove = move;
        }
        public void setBlackMove(final String move) {
            this.blackMove = move;
        }

    }

}
