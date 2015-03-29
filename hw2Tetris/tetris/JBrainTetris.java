package tetris;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JBrainTetris extends JTetris {

    protected Brain brain = new DefaultBrain();

    protected JCheckBox brainMode;
    protected JCheckBox animateMode;
    protected JSlider adversary;
    protected JButton loadBrain;
    protected JTextField brainText;
    protected JLabel randomLabel;
    protected Brain.Move bestMove;
    protected int lastCount = 0;
    protected Random random = new Random();

    JBrainTetris(int pixels) {
        super(pixels);
        // TODO Auto-generated constructor stub
    }

    @Override
    public JComponent createControlPanel() {
        JComponent panel = super.createControlPanel();

        panel.add(Box.createVerticalStrut(12));
        panel.add(new JLabel("Brain:"));

        brainMode = new JCheckBox("Brain active");
        brainMode.setSelected(false);
        panel.add(brainMode);

        animateMode = new JCheckBox("Animate fall");
        animateMode.setSelected(true);
        panel.add(animateMode);

        panel.add(Box.createVerticalStrut(12));
        JPanel row = new JPanel();
        row.add(new JLabel("Adversary:"));
        adversary = new JSlider(0, 100, 0);
        adversary.setPreferredSize(new Dimension(100, 15));
        row.add(adversary);
        panel.add(row);

        panel.add(Box.createVerticalStrut(12));
        row = new JPanel();
        loadBrain = new JButton("Load brain");
        row.add(loadBrain);
        brainText = new JTextField();
        brainText.setPreferredSize(new Dimension(100, 20));
        row.add(brainText);
        panel.add(row);
        
        panel.add(Box.createVerticalStrut(12));
        randomLabel = new JLabel();
        panel.add(randomLabel);
        
        return panel;
    }

    @Override
    public void tick(int verb) {

        switch (verb) {
        case JTetris.LEFT:
            ;
        case JTetris.RIGHT:
            ;
        case JTetris.DROP:
            ;
        case JTetris.ROTATE:
            super.tick(verb);
            return;
        case JTetris.DOWN:
            break;
        }

        if (brainMode.isSelected()) {
            if (count != lastCount) {
                board.undo();
                lastCount = count;
                bestMove = brain
                        .bestMove(board, currentPiece, HEIGHT, bestMove);
            }

            if (bestMove == null) {
                return;
            }

            if (!bestMove.piece.equals(currentPiece)) {
                super.tick(JTetris.ROTATE);
            }

            if (bestMove.x > currentX) {
                super.tick(JTetris.RIGHT);
            } else if (bestMove.x < currentX) {
                super.tick(JTetris.LEFT);
            } else if (!animateMode.isSelected()
                    && bestMove.piece.equals(currentPiece)) {
                super.tick(JTetris.DROP);
                super.tick(JTetris.DOWN);
            }

        }

        super.tick(JTetris.DOWN);
    }

    @Override
    public Piece pickNextPiece() {
        int rnd = random.nextInt(adversary.getMaximum() - 1) + 1;
        if (rnd >= adversary.getValue()) {
            randomLabel.setText("ok");
            return super.pickNextPiece();
        } else {
            randomLabel.setText("*ok*");
            Piece worstPiece = null;
            double highestScore = -1;
            for (Piece piece : pieces) {
                Brain.Move move = new Brain.Move();
                brain.bestMove(board, piece, HEIGHT, move);
                if (move.score > highestScore) {
                    highestScore = move.score;
                    worstPiece = piece;
                }
            }
            return worstPiece;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JBrainTetris tetris = new JBrainTetris(16);
        JFrame frame = JBrainTetris.createFrame(tetris);
        frame.setVisible(true);
    }

}
