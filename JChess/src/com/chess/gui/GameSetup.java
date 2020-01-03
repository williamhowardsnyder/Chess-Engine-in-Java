// TODO: COPIED

package com.chess.gui;

import com.chess.engine.Alliance;
import com.chess.engine.player.Player;
import com.chess.gui.Table.Difficulty;
import com.chess.gui.Table.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// GameSetup represents and manages the interface by which the user can set up their chess game
class GameSetup extends JDialog {

    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private Difficulty difficulty;
    // private JSpinner searchDepthSpinner;

    private static final String HUMAN_TEXT = "Human";
    private static final String COMPUTER_TEXT = "Computer";

    GameSetup(final JFrame frame,
              final boolean modal) {
        super(frame, modal);
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        final JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
        final JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton blackComputerButton = new JRadioButton(COMPUTER_TEXT);
        final JRadioButton likeActuallyReallyEasyButton = new JRadioButton("Like Actually Really Easy");
        final JRadioButton easyButton = new JRadioButton("Easy");
        final JRadioButton mediumButton = new JRadioButton("Medium");
        final JRadioButton hardButton = new JRadioButton("Hard");
        whiteHumanButton.setActionCommand(HUMAN_TEXT);
        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteComputerButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackComputerButton.setSelected(true);

        getContentPane().add(myPanel);
        myPanel.add(new JLabel("White"));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerButton);
        myPanel.add(new JLabel("Black"));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerButton);

        // Allows the user to adjust the difficulty of their AI opponent (//TODO: Implement search depth changing based on difficulty selected)
        final ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(likeActuallyReallyEasyButton);
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);
        likeActuallyReallyEasyButton.setSelected(true);
        myPanel.add(new JLabel("Difficulty"));
        myPanel.add(likeActuallyReallyEasyButton);
        myPanel.add(easyButton);
        myPanel.add(mediumButton);
        myPanel.add(hardButton);

        final JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                whitePlayerType = whiteComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                blackPlayerType = blackComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                boolean AIActiveWhite = whitePlayerType.equals(PlayerType.COMPUTER);
                boolean AIActiveBlack = blackPlayerType.equals(PlayerType.COMPUTER);
                // TODO: Allow for the AIs to have different difficulties
                if(AIActiveWhite) {
                    System.out.println("White AI is active");
                }
                if(AIActiveBlack) {
                    System.out.println("Black AI is active");
                    // TODO: Make this correspond to a particular AI
                }

                if(likeActuallyReallyEasyButton.isSelected()) {
                    difficulty = Difficulty.LIKE_ACTUALLY_REALLY_EASY;
                } else if(easyButton.isSelected()) {
                    difficulty = Difficulty.EASY;
                } else if(mediumButton.isSelected()) {
                    difficulty = Difficulty.MEDIUM;
                } else {
                    difficulty = Difficulty.HARD;
                }

                GameSetup.this.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                GameSetup.this.setVisible(false);
            }
        });

        myPanel.add(cancelButton);
        myPanel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    void promptUser() {
        setVisible(true);
        repaint();
    }

    public Difficulty difficulty() {
        return this.difficulty;
    }

    boolean isAIPlayer(final Player player) {
        if(player.getAlliance() == Alliance.WHITE) {
            return getWhitePlayerType() == PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == PlayerType.COMPUTER;
    }

    PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }

    private static JSpinner addLabeledSpinner(final Container c,
                                              final String label,
                                              final SpinnerModel model) {
        final JLabel l = new JLabel(label);
        c.add(l);
        final JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);
        return spinner;
    }

    /* int getSearchDepth() {
        return (Integer)this.searchDepthSpinner.getValue();
    }*/
}