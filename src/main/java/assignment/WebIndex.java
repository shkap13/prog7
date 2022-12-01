package assignment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.net.MalformedURLException;
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
    HashMap<String, HashMap<URL, HashMap<String, HashSet<Integer>>>> wordMap;

    public WebIndex(){
        wordMap = new HashMap<String, HashMap<URL, HashMap<String, HashSet<Integer>>>>();
        //first index will be the url
        // List<String> wordStrings = new ArrayList<String> (Arrays.asList(pageString.split(" ")));
    }

    public void addURl(String pageString){
        String [] wordStrings = pageString.split(" ");
        try{
            URL tempURL = new URL(wordStrings[0]);
            setAllMaps(tempURL, wordStrings);
        }
        catch(MalformedURLException e){
            System.err.println("Malformed URL Exception from WebIndex");
        }

    
    }

    public void setAllMaps(URL url, String [] wordStrings){
        String currWord = "";
        
        for(int i = 1; i < wordStrings.length; i++){
            currWord = wordStrings[i];
            
            if(wordMap.get(currWord) == null){
                wordMap.put(currWord, new HashMap<URL, HashMap<String, HashSet<Integer>>>());
                setNewURLMap(url, i, wordStrings);
            }
            else{
                
                if(wordMap.get(currWord).get(url) == null){
                    wordMap.get(currWord).put(url, new HashMap<String, HashSet<Integer>>());
                    setNextWordMap(url, i, wordStrings);
                }
                else{
                
                    //checck if i+1 i not equal to wordStrings length
                    if(wordMap.get(currWord).get(url).get(wordStrings[i+1]) == null){
                        HashSet<Integer> temp = new HashSet<Integer>();
                        temp.add(i+1);
                        wordMap.get(currWord).get(url).put(wordStrings[i+1], temp);
                    }
                    else{
                        wordMap.get(currWord).get(url).get(wordStrings[i+1]).add(i+1);
                    }
                }
            }
        }

    }

    public void setNewURLMap(URL url, int index, String[] wordStrings){
        wordMap.get(wordStrings[index]).put(url, new HashMap<String, HashSet<Integer>>());
        setNextWordMap(url, index, wordStrings);
    }

    public void setNextWordMap(URL url, int index, String[] wordStrings){
        HashSet<Integer> temp = new HashSet<Integer>();
        temp.add(index+1);
        wordMap.get(wordStrings[index]).get(url).put(wordStrings[index+1], temp);
    }

 


    // TODO: Implement all of this! You may choose your own data structures an internal APIs.
    // You should not need to worry about serialization (just make any other data structures you use
    // here also serializable - the Java standard library data structures already are, for example).
}
