package assign3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class SudokuFrame extends JFrame {
    
    JTextArea puzzleArea;
    JTextArea solutionArea;
    JButton checkButton;
    JCheckBox autoCheckBox;
    
    public SudokuFrame() {
        super("Sudoku Solver");

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        
        puzzleArea = new JTextArea(15, 40);
        puzzleArea.setBorder(new TitledBorder("Puzzle"));
        Document document = puzzleArea.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent arg0) {
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                if (autoCheckBox.isSelected()) {
                    for (ActionListener al: checkButton.getActionListeners()) {
                        al.actionPerformed(null);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                if (autoCheckBox.isSelected()) {
                    for (ActionListener al: checkButton.getActionListeners()) {
                        al.actionPerformed(null);
                    }
                }
            }
        });
        panel.add(puzzleArea, BorderLayout.CENTER);

        solutionArea = new JTextArea(15, 40);
        solutionArea.setBorder(new TitledBorder("Solution"));
        solutionArea.setEditable(false);
        panel.add(solutionArea, BorderLayout.EAST);

        JPanel little = new JPanel();

        little.setLayout(new BoxLayout(little, BoxLayout.X_AXIS));
        checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String text = puzzleArea.getText();
                solutionArea.setText(null);
                
                try {
                    Sudoku sudoku = new Sudoku(text);
                    
                    int count = sudoku.solve();
                    if (count > 0) {
                        solutionArea.append(sudoku.getSolutionText());
                        solutionArea.append(System.lineSeparator());
                        solutionArea.append("solutions:" + count);
                        solutionArea.append(System.lineSeparator());
                    }
                    
                    solutionArea.append("elapsed:" + sudoku.getElapsed() + "ms");
                } catch (Exception e) {
                    solutionArea.setText("Parsing problem");
                }
            }
        });

        autoCheckBox = new JCheckBox("Auto Check");

        little.add(checkButton);
        little.add(autoCheckBox);
        panel.add(little, BorderLayout.SOUTH);

        setContentPane(panel);

        setLocationByPlatform(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        // GUI Look And Feel
        // Do this incantation at the start of main() to tell Swing
        // to use the GUI LookAndFeel of the native platform. It's ok
        // to ignore the exception.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SudokuFrame frame = new SudokuFrame();
    }

}
