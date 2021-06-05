import java.util.LinkedList;

public class URLPool {

    private LinkedList<URLDepthPair> items;
    private static int waitingThreadsNum = 0;

    public URLPool() {
        items = new LinkedList<>();
    }

    public void put(URLDepthPair pair) {
        synchronized (this) {
            items.add(pair);
            notify();
        }
    }

    public URLDepthPair get() throws java.lang.InterruptedException {
        URLDepthPair item = null;
        synchronized (this) {
            while (items.size() == 0) {
                ++waitingThreadsNum;
                wait();
                --waitingThreadsNum;
            }

            item = items.removeFirst();
        }
        return item;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void printContent() {
        for (URLDepthPair i : items)
            System.out.println(i);
    }

    public boolean contains(URLDepthPair pair) {
        return items.contains(pair);
    }

    public int getWaitCount() {
        synchronized (this) {
            return waitingThreadsNum;
        }
    }
}
