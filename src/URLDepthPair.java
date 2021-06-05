public class URLDepthPair {

    private static final String URL_PREFIX = "http://";
    private static final String A_HREF = "<a href=\"";
    private static final String A_HREF_END = "\"";

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

    public static boolean isRightURL(String url) {
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