package assignment;

import java.util.*;
import java.io.File;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses the input;
 * responsible for building the actual web index.
 *
 * TODO: Implement this!
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {
    String path;
    String absolutePath;
    String currentPathString;
    ArrayList<String> allPaths;
    int allPathsIndex;
    String pageString;
    WebIndex wIndex;
    int [] scriptStyleTracker;


    public CrawlingMarkupHandler() {
        absolutePath = "";
        currentPathString = "";
        allPaths = new ArrayList<String>();
        allPathsIndex = 0;
        wIndex = new WebIndex();
        scriptStyleTracker = new int[2];
    }

    public String setCurrentPathString(String currPathInput){
        currentPathString = currPathInput;


        for(int i = currPathInput.length() - 1; i >= 0; i--){
            if(currPathInput.substring(i, i + 1).equals("/")){
                break;
            }
            
            currentPathString = currentPathString.substring(0,i);
        }

        if(currentPathString.contains("..")){

           currentPathString = removeRelative(currentPathString);
        }
       
        return currentPathString;
    }

    public String removeRelative(String current){
        int dotsCounter = 0;
        int startIndex = 0;

        for(int i = 0; i < current.length(); ){
            int index = current.indexOf("..", i);
            if(index > -1){
                if(dotsCounter == 0){
                    startIndex = index;
                }
                dotsCounter++;
                i = index + 2;
            }
            else{
                break;
            }
        }

        String tempString = current.substring(0, startIndex);
        int tempIndex = tempString.length()-1;

        while(dotsCounter >= 0){
            if(tempString.charAt(tempIndex) == '/'){
                dotsCounter--;
            }

            tempString = tempString.substring(0, tempIndex);
            tempIndex = tempString.length() - 1;
        }

        current = tempString + current.substring(startIndex - 1);

        current = current.replace("/..", "");

        return current;
    }

    public void getTheURL(URL url){
        absolutePath = url.toString();
    }

    public String getCurrentPathString(){
        return currentPathString;
    }

    /**
    * This method returns the complete index that has been crawled thus far when called.
    */
    public Index getIndex() {
        
        return wIndex;
    }

    /**
    * This method returns any new URLs found to the Crawler; upon being called, the set of new URLs
    * should be cleared.
    */
    public List<URL> newURLs() {

        try{
            LinkedList<URL> list = new LinkedList<URL>();
            for(;allPathsIndex < allPaths.size(); allPathsIndex++){
                list.add(new URL(allPaths.get(allPathsIndex)));
            }
            return list;
        }

        catch(MalformedURLException e){
            System.err.print("There is a malformed URL exception in the newURLs method and allPaths is: " + allPaths.get(allPathsIndex));
        }
       
        //no new urls added
        return null;
    }

    /**
    * These are some of the methods from AbstractSimpleMarkupHandler.
    * All of its method implementations are NoOps, so we've added some things
    * to do; please remove all the extra printing before you turn in your code.
    *
    * Note: each of these methods defines a line and col param, but you probably
    * don't need those values. You can look at the documentation for the
    * superclass to see all of the handler methods.
    */

    /**
    * Called when the parser first starts reading a document.
    * @param startTimeNanos  the current time (in nanoseconds) when parsing starts
    * @param line            the line of the document where parsing starts
    * @param col             the column of the document where parsing starts
    */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        pageString = absolutePath + " ";
    }

    /**
    * Called when the parser finishes reading a document.
    * @param endTimeNanos    the current time (in nanoseconds) when parsing ends
    * @param totalTimeNanos  the difference between current times at the start
    *                        and end of parsing
    * @param line            the line of the document where parsing ends
    * @param col             the column of the document where the parsing ends
    */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) {
        pageString = pageString.replaceAll("[ ]+", " ");
        wIndex.addURl(pageString);
    }

    /**
    * Called at the start of any tag.
    * @param elementName the element name (such as "div")
    * @param attributes  the element attributes map, or null if it has no attributes
    * @param line        the line in the document where this element appears
    * @param col         the column in the document where this element appears
    */


    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {

        if(elementName.equals("script")){
            int temp = scriptStyleTracker[0];
            scriptStyleTracker[0] = temp + 1;
        }
        else if(elementName.equals("style")){
            int temp = scriptStyleTracker[1];
            scriptStyleTracker[1] = temp + 1;
        }
        
        if(attributes != null && elementName.equals("a")){
            if(attributes.get("href") != null){
                path = currentPathString + attributes.get("href");

                if(path.contains("..")){
                    path = removeRelative(path);
                }

                File file = new File(path.substring(5));
                
                if(file.isFile()){
                    if(!allPaths.contains(path)){
                        allPaths.add(path);
                    }
                }
               
    
            }
        }
       
    }

    /**
    * Called at the end of any tag.
    * @param elementName the element name (such as "div").
    * @param line        the line in the document where this element appears.
    * @param col         the column in the document where this element appears.
    */
    public void handleCloseElement(String elementName, int line, int col) {
        if(elementName.equals("script")){
            int temp = scriptStyleTracker[0];
            scriptStyleTracker[0] = temp - 1;
        }

        else if(elementName.equals("style")){
            int temp = scriptStyleTracker[1];
            scriptStyleTracker[1] = temp - 1;
        }
    }

    /**
    * Called whenever characters are found inside a tag. Note that the parser is not
    * required to return all characters in the tag in a single chunk. Whitespace is
    * also returned as characters.
    * @param ch      buffer containing characters; do not modify this buffer
    * @param start   location of 1st character in ch
    * @param length  number of characters in ch
    */
    public void handleText(char[] ch, int start, int length, int line, int col) {

        if((scriptStyleTracker[0] > 0) || (scriptStyleTracker[1] > 0)){
            return;
        }
        for(int i = start; i < start + length; i++) {
            // Instead of printing raw whitespace, we're escaping it
            switch(ch[i]) {
                case '\n':
                    pageString = pageString + " ";
                case '\r':
                    pageString = pageString + " ";
                case '\t':
                    pageString = pageString + " ";
                case '.':
                    pageString = pageString + " ";
                    break;
                case '?':
                    pageString = pageString + " ";
                    break;
                case '!':
                    pageString = pageString + " ";
                    break;
                case '-':
                    pageString = pageString + " ";
                    break;
                case ',':
                    pageString = pageString + " ";
                    break;
                case '\'':
                    pageString = pageString + " ";
                    break;
                case  '"':
                    pageString = pageString + " ";
                    break;
                case '\\':
                    pageString = pageString + " ";
                    break;
                case '/':
                    pageString = pageString + " ";
                    break;
                case '(':
                    pageString = pageString + " ";
                    break;
                case ')':
                    pageString = pageString + " ";
                    break;
                case ':':
                    pageString = pageString + " ";
                    break;
                case ';':
                    pageString = pageString + " ";
                    break;
                case '[':
                    pageString = pageString + " ";
                    break;
                case ']':
                    pageString = pageString + " ";
                    break;
                case '{':
                    pageString = pageString + " ";
                    break;
                case '}':
                    pageString = pageString + " ";
                    break;
                case '&':
                    pageString = pageString + " ";
                    break;
                default:
                    pageString = pageString + Character.toString(ch[i]);
                    break;
            }
        }

    }
}
