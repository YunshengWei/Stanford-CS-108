package assign4;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class WebFrame extends JFrame {

    JTable table;
    JButton singleButton;
    JButton concurButton;
    JTextField text;
    JLabel runningLabel;
    JLabel completedLabel;
    JLabel elapsedLabel;
    JProgressBar bar;
    JButton stopButton;

    private Thread launcher;
    private int numRunning;
    private int numCompleted;

    protected synchronized void incRunningThreads() {
        numRunning++;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runningLabel.setText("Running:" + numRunning);
            }
        });
    }

    protected synchronized void decRunningThreads() {
        numRunning--;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runningLabel.setText("Running:" + numRunning);
            }
        });
    }

    protected synchronized void incCompletedThreads() {
        numCompleted++;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                completedLabel.setText("Completed:" + numCompleted);
            }
        });
    }

    private class Fetch implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setValueAt(null, i, 1);
            }
            
            numCompleted = 0;
            runningLabel.setText("Running:" + numRunning);
            completedLabel.setText("Completed:" + numCompleted);
            elapsedLabel.setText("Elapsed:");
            bar.setMaximum(table.getRowCount());

            int numThreads;
            if (e.getSource() == singleButton) {
                numThreads = 1;
            } else {
                try {
                    numThreads = Integer.parseInt(text.getText());
                } catch (NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                    return;
                }
            }

            launcher = new Thread(new Launcher(numThreads));
            launcher.start();
            singleButton.setEnabled(false);
            concurButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    private class Stop implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            launcher.interrupt();
        }
    }

    private class Launcher implements Runnable {
        private Semaphore semaphore;
        private Thread[] threadArray;

        Launcher(int numThreads) {
            semaphore = new Semaphore(numThreads);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            incRunningThreads();
            
            try {
                // Note: the thread-safe way to use get() method
                final Integer[] rowCount = new Integer[1];
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        rowCount[0] = table.getRowCount();
                    }
                });

                threadArray = new Thread[rowCount[0]];
                
                for (int i = 0; i < rowCount[0]; i++) {
                    final String[] url = new String[1];
                    final int rowIndex = i;
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            url[0] = (String) table.getValueAt(rowIndex, 0);
                        }
                    });
                    String urlString = url[0];
                    semaphore.acquire();
                    Thread workerThread = new Thread(new WebWorker(semaphore,
                            i, WebFrame.this, urlString));
                    threadArray[i] = workerThread;
                    workerThread.start();
                }

                for (Thread thread : threadArray) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                for (Thread thread : threadArray) {
                    if (thread != null) {
                        thread.interrupt();
                    }
                }
            } catch (InvocationTargetException ignored) {
            }
            
            decRunningThreads();
            final long elapsedTime = System.currentTimeMillis() - startTime;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    singleButton.setEnabled(true);
                    concurButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    elapsedLabel.setText("Elasped:" + elapsedTime / 1000.0);
                    bar.setValue(0);
                }
            });
        }
    }

    public WebFrame(List<String> urls) {
        super("WebLoader");

        numRunning = 0;
        numCompleted = 0;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        setContentPane(panel);

        // table
        table = new JTable();
        TableModel model = new DefaultTableModel(
                new String[] { "url", "status" }, urls.size());
        for (int i = 0; i < urls.size(); i++) {
            model.setValueAt(urls.get(i), i, 0);
        }
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        panel.add(scrollPane);

        // "Single Thread Fetch" button
        singleButton = new JButton("Single Thread Fetch");
        singleButton.addActionListener(new Fetch());
        panel.add(singleButton);

        // "Concurrent Fetch" button
        concurButton = new JButton("Concurrent Fetch");
        concurButton.addActionListener(new Fetch());
        panel.add(concurButton);

        // text field
        text = new JTextField();
        text.setMaximumSize(new Dimension(50, 20));
        ;
        panel.add(text);

        // "Running" label
        runningLabel = new JLabel("Running:" + numRunning);
        panel.add(runningLabel);

        // "Completed" label
        completedLabel = new JLabel("Completed:" + numCompleted);
        panel.add(completedLabel);

        // "Elapsed" label
        elapsedLabel = new JLabel("Elapsed:");
        panel.add(elapsedLabel);

        // progress bar
        bar = new JProgressBar();
        panel.add(bar);

        // "Stop" button
        stopButton = new JButton("Stop");
        stopButton.addActionListener(new Stop());
        panel.add(stopButton);

        // pack() and setDefaultCloseOperation() can be think of as property of
        // WebFrame, but setVisible() can't.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    private static void createAndShowGUI(String urlFile) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        List<String> urls = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new FileReader(new File(
                urlFile)))) {
            String url;
            while ((url = in.readLine()) != null) {
                urls.add(url);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        WebFrame webLoader = new WebFrame(urls);
        webLoader.setVisible(true);
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI(args[0]);
            }
        });
    }

}
