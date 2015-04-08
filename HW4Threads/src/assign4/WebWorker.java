package assign4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Semaphore;
import java.util.Date;
import java.text.DateFormat;

import javax.swing.SwingUtilities;

public class WebWorker implements Runnable {
    final Semaphore semaphore;
    final WebFrame webFrame;
    final int tableIndex;
    final String urlString;

    public WebWorker(Semaphore semaphore, int tableIndex, WebFrame webFrame,
            String urlString) {
        this.semaphore = semaphore;
        this.tableIndex = tableIndex;
        this.webFrame = webFrame;
        this.urlString = urlString;
    }

    /**
     * Try to download the content of the url.
     * 
     * @param url
     */
    private void download(String urlString) {
        String status = null;
        InputStream input = null;
        StringBuilder contents = null;

        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);

            long startTime = System.currentTimeMillis();
            connection.connect();
            input = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    input));

            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);

            if (Thread.currentThread().isInterrupted()) {
                status = "interrupted";
            } else {
                while ((len = reader.read(array, 0, array.length)) > 0) {
                    if (Thread.currentThread().isInterrupted()) {
                        status = "interrupted";
                        break;
                    }
                    contents.append(array, 0, len);
                    Thread.sleep(100);
                }
            }

            if (status == null) {
                long completedTime = System.currentTimeMillis();
                Date completedDate = new Date(completedTime);
                String completionTime = DateFormat.getTimeInstance().format(
                        completedDate);
                long elapsedTime = completedTime - startTime;

                status = completionTime + "  " + elapsedTime + "ms  "
                        + contents.length() + " bytes";
            }
        } catch (IOException e) {
            status = "err";
        } catch (InterruptedException e) {
            status = "interrupted";
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {
            }
        }

        final String statusString = status;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                webFrame.table.setValueAt(statusString, tableIndex, 1);
            }
        });
    }

    @Override
    public void run() {
        webFrame.incRunningThreads();
        download(urlString);
        webFrame.decRunningThreads();
        webFrame.incCompletedThreads();
        webFrame.incProgressBar();
        semaphore.release();
    }
}
