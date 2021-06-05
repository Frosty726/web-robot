import java.io.*;
import java.net.*;

public class RoboTask extends Thread {

    private String url;
    private int depth;
    private URLPool checked;
    private URLPool unchecked;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static final String URL_PREFIX = "http://";
    private static final String A_HREF = "<a href=\"";
    private static final String A_HREF_END = "\"";

    public RoboTask(String url, int depth, URLPool checked, URLPool unchecked) {
        this.checked = checked;
        this.unchecked = unchecked;
        this.url = url;
        this.depth = depth;
    }

    public void run() {
        while (true) {
            try {
                URLDepthPair pair = unchecked.get();
                if (pair != null && pair.isRightURL() && !isChecked(pair))
                    checkURL(pair);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void checkURL(URLDepthPair pair) {
        if (pair.depth >= depth) {
            checked.put(pair);
            return;
        }

        try {
            try {
                socket = new Socket(url.substring(URL_PREFIX.length()), 80);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                socket.setSoTimeout(1000);

                /** Request creation **/
                String request = "GET " + pair.getResource() + " HTTP/1.1" + "\n" +
                        "Host: " + pair.getHost() + "\n" +
                        "Connection: close\n";
                out.println(request);

                /** Receiving offer **/
                String line = new String();
                int hrefIndex = 0;
                line = in.readLine();
                while (line != null) {
                    hrefIndex = line.indexOf(A_HREF) + A_HREF.length();
                    if (hrefIndex != A_HREF.length() - 1) {
                        String resource = line.substring(hrefIndex, line.indexOf(A_HREF_END, hrefIndex));
                        URLDepthPair found = new URLDepthPair(resource, pair.depth + 1);
                        if (!isChecked(found)) unchecked.put(found);
                    }
                    line = in.readLine();
                }

                checked.put(pair);

            } catch (UnknownHostException | SecurityException e) {
                System.out.println("Unknown host: " + e.getMessage());
            } catch (StringIndexOutOfBoundsException e) {
                // Invalid line
            } finally {
                out.close();
                in.close();
                socket.close();
            }
        } catch (IOException | NullPointerException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private boolean isChecked(URLDepthPair pair) {
        if (checked.contains(pair))
            return true;
        return false;
    }
}
