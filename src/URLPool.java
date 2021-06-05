import java.util.LinkedList;

public class URLPool {

    LinkedList<URLDepthPair> items;

    public URLPool() {
        items = new LinkedList<>();
    }

    public boolean put(URLDepthPair pair) {
        boolean added = false;
        synchronized (items) {
            items.add(pair);
            added = true;
            items.notify();
        }
        return added;
    }

    public URLDepthPair get() throws java.lang.InterruptedException {
        URLDepthPair item = null;
        synchronized (items) {
            while (items.size() == 0)
                items.wait();
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
}
