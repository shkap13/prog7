package assignment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    HashMap<String, HashMap<String, HashSet<Integer>>> urlMap;

    public WebIndex(){
        urlMap = new HashMap<String, HashMap<String, HashSet<Integer>>>();
        //first index will be the url
        // List<String> wordStrings = new ArrayList<String> (Arrays.asList(pageString.split(" ")));
    }

    public void addURl(String pageString){
        String [] wordStrings = pageString.split(" ");
        // URL tempUrl = new URL(wordStrings[0]);
        String tempUrl = wordStrings[0];

        if(urlMap.get(tempUrl) != null){
            urlMap.put(tempUrl, setWordMap(tempUrl, wordStrings));
        }

        //if i go back to using the URL, need malformed URL exception again

    }

    public HashMap<String, HashSet<Integer>> setWordMap(String word, String [] wordStrings){
        HashMap<String, HashSet<Integer>> wordMap = new HashMap<String, HashSet<Integer>>();
        
        if(wordMap.get(word) != null){
            wordMap.put(word, setLocationSet(word, wordStrings));
        }

        return wordMap;
    }

    public HashSet<Integer> setLocationSet(String word, String [] wordStrings){
        HashSet<Integer> locSet = new HashSet<Integer>();

        for(int i = 1; i < wordStrings.length; i++){
            if(wordStrings[i].equals(word)){
                locSet.add(i);
            }
        }

        return locSet;
    }




    // TODO: Implement all of this! You may choose your own data structures an internal APIs.
    // You should not need to worry about serialization (just make any other data structures you use
    // here also serializable - the Java standard library data structures already are, for example).
}
