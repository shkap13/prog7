package assignment;

import java.io.*;
import java.net.*;
import java.util.*;

import org.attoparser.simple.*;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;

/**
 * The entry-point for WebCrawler; takes in a list of URLs to start crawling from and saves an index
 * to index.db.
 */
public class WebCrawler {
    static WebIndex index;

   
    /**
    * The WebCrawler's main method starts crawling a set of pages.  You can change this method as
    * you see fit, as long as it takes URLs as inputs and saves an Index at "index.db".
    */
    public static void main(String[] args) {

        // Basic usage information
        if (args.length == 0) {
            System.err.println("Error: No URLs specified.");
            return;
        }

        // We'll throw all the args into a queue for processing.
        Queue<URL> remaining = new LinkedList<>();
        for (String url : args) {
            try {
                remaining.add(new URL(url));
            } catch (MalformedURLException e) {
                // Throw this one out!
                System.err.printf("Error: URL '%s' was malformed and will be ignored!%n", url);
            }
        }

        // Create a parser from the attoparser library, and our handler for markup.
        ISimpleMarkupParser parser = new SimpleMarkupParser(ParseConfiguration.htmlConfiguration());
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

       
        while (!remaining.isEmpty()) {
            String print = remaining.peek().toString();
            //set current path string in crawling markup handler so that the entire path can be constructed
            handler.setCurrentPathString(remaining.peek().toString());
            handler.getTheURL(remaining.peek());

            try{
                // Parse the next URL's page
                parser.parse(new InputStreamReader(remaining.poll().openStream()), handler);
                // Add any new URLs
                remaining.addAll(handler.newURLs());
            }
            catch(IOException e){
                System.err.println("this file is causing an IO Exception: " + print);
                continue;
            }
            catch(ParseException e){
                System.err.println("this file is causing a Parse Exception: " + print);
                continue;
            }
        }

        try{
            index = (WebIndex) handler.getIndex();
            index.save("index.db");
        }
        catch(IOException e){
            System.err.println("index.db does not exist???");
            return;
        }

    }
}
