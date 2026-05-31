package tm.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * A simple, static class to display a URL in the system browser.
 *
 * Examples:
 *
 * BrowserControl.displayURL("http://www.javaworld.com")
 *
 * BrowserControl.displayURL("file://c:\\docs\\index.html")
 *
 * BrowserControl.displayURL("file:///user/joe/index.html");
 *
 * Note - you must include the url type -- either "http://" or
 * "file://".
 */
public class BrowserControl
{
    /**
     * Display a URL in the system default browser.
     *
     * @param url the file's url (the url must start with either "http://"
     * or "file://").
     */
    public static void displayURL(String url)
    {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(URI.create(url));
                return;
            }
        }
        catch (IOException | IllegalArgumentException | UnsupportedOperationException x) {
            TMLog.logException("Could not invoke browser, url=" + url, x);
        }
    }

    /**
     * Try to determine whether this application is running under Windows
     * by examining the "os.name" property.
     *
     * @return true if this application is running under a Windows OS
     */
    public static boolean isWindowsPlatform()
    {
        String os = System.getProperty("os.name");
        return os != null && os.startsWith(WIN_ID);
    }

    /**
     * Simple example.
     */
    public static void main(String[] args)
    {
        displayURL("http://www.javaworld.com");
    }

    private static final String WIN_ID = "Windows";
}
