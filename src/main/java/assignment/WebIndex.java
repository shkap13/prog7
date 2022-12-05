package assignment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    }

    public void addURl(String pageString){

        try{
            String [] wordStrings = pageString.split(" ");
            Page tempPage = new Page(new URL(wordStrings[0]));
    
            if(urlMap.get(tempPage) == null){
                urlMap.put(tempPage, setWordMap(wordStrings));
                // System.out.println("urls are getting added to the map");
                // System.out.println("is the urlMap empty?: " + urlMap.isEmpty());
            }

            // System.out.println("url was added!: " + wordStrings[0]);

        }

        catch(MalformedURLException e){
            System.err.println("Malformed URL while creating WebIndex");
        }
       
    }

    public HashMap<String, HashSet<Integer>> setWordMap(String [] wordStrings){
        HashMap<String, HashSet<Integer>> wordMap = new HashMap<String, HashSet<Integer>>();
        
       for(int i = 1; i < wordStrings.length; i++){
            
            if(wordMap.get(wordStrings[i].toLowerCase()) == null){
                HashSet<Integer> temp = new HashSet<Integer>();
                temp.add(i);
                wordMap.put(wordStrings[i].toLowerCase(), temp);
            }
            else{
                wordMap.get(wordStrings[i].toLowerCase()).add(i);
            }
       }

       return wordMap;
    }

    public HashSet<Page> getURLForWord(String word){
        HashSet<Page> urlList = new HashSet<Page>();
        if(urlMap == null){
            return null;
        }
        if(urlMap.keySet() == null){
            return null;
        }
        Iterator iter = urlMap.keySet().iterator();

        while(iter.hasNext()){
            Page page = (Page) iter.next();
            HashMap<String, HashSet<Integer>> map = (HashMap<String, HashSet<Integer>>) urlMap.get(page).clone();
            
            if(map.get(word) != null){
                urlList.add(page);
            }
        }    
        
        return urlList;   
    }

    //returns set of urls containing all of the words in the array passed 
    //useful for phrases
    public HashSet<Page> getURLForAllWords(String [] words){

        System.out.println("words.length: " + words.length);
        HashSet<Page> currentSet = new HashSet<Page>(getURLForWord(words[0]));
        for(int i = 1; i < words.length; i++){
            HashSet<Page> tempSet = new HashSet<Page> (getURLForWord(words[i]));
            currentSet.retainAll(tempSet);
        }

        return currentSet;
    }

    public HashSet<Page> getURLForAllPhrase(HashSet<Page> allURL, String [] words){

        HashSet<Page> newURLSet = new HashSet<Page>(allURL);
        Iterator<Page> iter = allURL.iterator();

        while(iter.hasNext()){
            Page currentPage = iter.next();
            HashMap<String, HashSet<Integer>> tempWordMap = urlMap.get(currentPage);

            HashSet<Integer> locations = new HashSet<Integer>(tempWordMap.get(words[0]));

            
            for(int i = 1; i < words.length; i++){
                HashSet<Integer> nextLocations = tempWordMap.get(words[i]);

                HashSet<Integer> newSet = new HashSet<Integer>();


                Iterator<Integer> locIterator = nextLocations.iterator();


                while(locIterator.hasNext()){
                    int temp = locIterator.next();
                    temp--;

                    if(locations.contains(temp)){
                        newSet.add(temp + 1);
                    }
                }

                locations = newSet;
            }

            if(locations.size() < 1){
                newURLSet.remove(currentPage);
            }

        }

        return newURLSet;

    }

    public Set<Page> getAllURL(){
        HashSet<Page> newSet = new HashSet<>(urlMap.keySet());
        return newSet;
    }

}