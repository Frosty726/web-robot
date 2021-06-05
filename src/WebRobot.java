import java.net.*;
import java.util.*;

public class WebRobot {

    /** Data containing variables **/
    private static int numThreads;
    private static String url;
    private static int depth;
    private static URLPool checked;
    private static URLPool unchecked;

    private static RoboTask[] threads;

    /**
     * Main method
     * **/
    public static void main(String[] args) {

        /** Input processing and arguments validation **/
        try {
            if (args.length == 3) {
                url = args[0];
                depth = Integer.parseInt(args[1]);
                numThreads = Integer.parseInt(args[2]);
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
            System.out.println("Must get 3 arguments:\nURL, search depth, number of threads");
            return;
        }

        /** Program start **/
        try {
            checked = new URLPool();
            unchecked = new URLPool();
            unchecked.put(new URLDepthPair(url, 0));

            threads = new RoboTask[numThreads];

            /** Threads running **/
            for (int i = 0; i < numThreads; ++i) {
                threads[i] = new RoboTask(url, depth, checked, unchecked);
                threads[i].start();
            }

            while (true) {
                try {
                    if (unchecked.getWaitCount() != numThreads) {
                        Thread.sleep(100);
                    }
                    else
                        break;
                }
                catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }

            /** Result printing **/
            checked.printContent();

            System.exit(0);

        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
