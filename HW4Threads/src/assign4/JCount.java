package assign4;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class JCount extends JPanel {
    private JTextField textField;
    private JLabel label;
    private JButton startButton, stopButton;
    private Thread worker;
    
    private class WorkerThread implements Runnable {
        private int maxValue;
        int currentValue;
        
        WorkerThread(int maxValue) {
            this.maxValue = maxValue;
            currentValue = 0;
        }
        
        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    label.setText(String.valueOf(currentValue));
                }});
            
            while (currentValue < maxValue) {
                for (int i = 0; i < 10000; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    currentValue++;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        label.setText(String.valueOf(currentValue));
                    }});
            }
        }
    }
    
    public JCount() {
        textField = new JTextField();
        label = new JLabel(" ");
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(textField);
        add(label);
        add(startButton);
        add(stopButton);
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (worker != null && !worker.isInterrupted()) {
                        worker.interrupt();
                    }
                    
                    String text = textField.getText();
                    int maxValue = Integer.parseInt(text);
                    worker = new Thread(new WorkerThread(maxValue));
                    worker.start();
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (worker != null && !worker.isInterrupted()) {
                    worker.interrupt();
                }
            }
        });
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Coutner");
        
        JPanel panel = new JPanel();
        frame.setContentPane(panel);
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < 4; i++) {
            panel.add(new JCount());
            panel.add(Box.createRigidArea(new Dimension(0, 40)));
        }
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
