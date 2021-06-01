import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;

public class WebRobot {
    private static class URLDepthPair {

        public String url;
        public int depth;
        private int hostLength;

        public URLDepthPair(String url, int depth) {
            this.url = url;
            this.depth = depth;

            int endIndex = URL_PREFIX.length();
            while (endIndex < url.length() && url.charAt(endIndex) !='/')
                ++endIndex;
            hostLength = endIndex;
        }

        @Override
        public String toString() {
            return depth + " " + url;
        }

        private static boolean isRightURL(String url) {
            return url.startsWith(URL_PREFIX);
        }

        public boolean isRightURL() {
            return url.startsWith(URL_PREFIX);
        }

        public String getHost() {
            return url.substring(URL_PREFIX.length(), hostLength);
        }

        public String getResource() {
            if (hostLength == url.length())
                return "/";
            return url.substring(hostLength);
        }

        @Override
        public boolean equals(Object other) {
            URLDepthPair toCompare = (URLDepthPair) other;
            if (url.equals(toCompare.url) || url.equals(toCompare.url + "/"))
                return true;
            return false;
        }
    }

    /** Data containing variables **/
    private static String url;
    private static int depth;
    private static LinkedList<URLDepthPair> checked;
    private static LinkedList<URLDepthPair> unchecked;

    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    private static final String URL_PREFIX = "http://";
    private static final String A_HREF = "<a href=\"";
    private static final String A_HREF_END = "\"";

    /**
     * Main method
     * **/
    public static void main(String[] args) {

        /** Input processing and arguments validation **/
        try {
            if (args.length == 2) {
                url = args[0];
                depth = Integer.parseInt(args[1]);
            }
            else
                throw new IllegalArgumentException();

            if (!URLDepthPair.isRightURL(url))
                throw new MalformedURLException();

        } catch (MalformedURLException e) {
            System.out.println("Wrong URL format: must starts with http://");
            return;
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage() + " - depth value must be integer");
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Must get 2 arguments:\nURL, search depth");
            return;
        }

        /** Program start **/
        try {
            checked = new LinkedList<>();
            unchecked = new LinkedList<>();
            unchecked.add(new URLDepthPair(url, 0));

            /** Main loop **/
            while (!unchecked.isEmpty()) {
                URLDepthPair pair = unchecked.pop();
                if (pair.isRightURL() && pair.depth < depth && !isChecked(pair))
                    checkURL(pair);
            }

            /** Result printing **/
            for (URLDepthPair i : checked)
                System.out.println(i);

        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private static void checkURL(URLDepthPair pair) {
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
                        unchecked.add(new URLDepthPair(resource, pair.depth + 1));
                    }
                    line = in.readLine();
                }

                checked.add(pair);

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

    private static boolean isChecked(URLDepthPair pair) {
        if (checked.contains(pair))
            return true;
        return false;
    }
}
