package assignment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 *
 * TODO: Implement this!
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;
    HashMap<Page, HashMap<String, HashSet<Integer>>> urlMap;

    public WebIndex(){
        urlMap = new HashMap<Page, HashMap<String, HashSet<Integer>>>();
        //first index will be the url
        // List<String> wordStrings = new ArrayList<String> (Arrays.asList(pageString.split(" ")));
    }

    public void addURl(String pageString){

        try{
            String [] wordStrings = pageString.split(" ");
            Page tempPage = new Page(new URL(wordStrings[0]));
    
            if(urlMap.get(tempPage) == null){
                urlMap.put(tempPage, setWordMap(wordStrings));
                System.out.println("urls are getting added to the map");
                System.out.println("is the urlMap empty?: " + urlMap.isEmpty());
            }

        }

        catch(MalformedURLException e){
            System.err.println("Malformed URL while creating WebIndex");
        }
       
    }

    public HashMap<String, HashSet<Integer>> setWordMap(String [] wordStrings){
        HashMap<String, HashSet<Integer>> wordMap = new HashMap<String, HashSet<Integer>>();
        
       for(int i = 1; i < wordStrings.length; i++){
            
            if(wordMap.get(wordStrings[i]) == null){
                HashSet<Integer> temp = new HashSet<Integer>();
                temp.add(i);
                wordMap.put(wordStrings[i], temp);
            }
            else{
                wordMap.get(wordStrings[i]).add(i);
            }
       }

       return wordMap;
    }

    public ArrayList<Page> getURLForWord(String word){
        ArrayList<Page> urlList = new ArrayList<Page>();
        if(urlMap == null){
            System.out.println("im fucking sobbing");
        }
        if(urlMap.keySet() == null){
            System.out.println("im crying");
        }
        Iterator iter = urlMap.keySet().iterator();
        
        while(iter.hasNext()){
            Page page = (Page) iter.next();
            HashMap<String, HashSet<Integer>> map = urlMap.get(page);

            if(map.get(word) != null){
                urlList.add(page);
            }
        }    
        
        return urlList;   
    }

    public void printWebIndex(){
        System.out.println(urlMap);
    }

    public void printWordMap(String url){

        try{
            Page page = new Page(new URL(url));
            System.out.println(urlMap.get(page));
        }

        catch(MalformedURLException e){
            System.err.println("the stupid url u passed is illegal dumbass");
        }

    }

    // TODO: Implement all of this! You may choose your own data structures an internal APIs.
    // You should not need to worry about serialization (just make any other data structures you use
    // here also serializable - the Java standard library data structures already are, for example).
}