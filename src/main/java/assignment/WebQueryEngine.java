package assignment;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 * TODO: Implement this!
 */
public class WebQueryEngine {
    WebIndex index;

    public WebQueryEngine(WebIndex wIndex){
        index = wIndex;
    }

    public WebQueryEngine(){
        index = null;
    }
    /**
     * Returns a WebQueryEngine that uses the given Index to construct answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        // TODO: Implement this!
        return new WebQueryEngine(index);
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query expression.
     *
     * @param query A query expression.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) {
        String newquery = query.replaceAll("[ ]+", " ");
        if(!newquery.contains(" ")){
            return wordQuery(newquery);
        }
        // else{
        //     phraseQuery(newquery);
        // }

        return new LinkedHashSet<>();
    }

    public Collection<Page> wordQuery(String word){
        return index.getURLForWord(word);
    }

    // public Collection<Page> phraseQuery(String phrase){
    //     parseQuery(String line);

    // }

    public void parseQuery(String line){
        Stack opStack = new Stack();
        Queue que = new LinkedList<String>();

        ArrayList<String> tokens = createTokens(line);
    }

    public ArrayList<String> createTokens(String line){
        ArrayList<String> allTokens = new ArrayList<String>();
        String token = "";
        for(int i = 0; i < line.length(); ){

            if(line.charAt(i) == '\"'){
                String temp = createPhraseToken(i, line);
                allTokens.add(temp);
                i = i + temp.length() + 2;
            }
            else if((line.charAt(i) == '&') || (line.charAt(i) == '|') || (line.charAt(i) == '(') || (line.charAt(i) == ')')){
                if(!token.equals("")){
                    allTokens.add(token);
                }
                token = "";
                allTokens.add(Character.toString(line.charAt(i)));
                i++;
            }
            else{
                token = token + Character.toString(line.charAt(i));
                i++;
            }
        }

        return allTokens;
    }

    public String createPhraseToken(int index, String line){
        String phraseToken = "";
        for(int i = index+1; i < line.length(); i++){
            if(line.charAt(i) == '\"'){
                return phraseToken;
            }

            phraseToken = phraseToken + Character.toString(line.charAt(i));
        }

        return phraseToken;
    }
}
