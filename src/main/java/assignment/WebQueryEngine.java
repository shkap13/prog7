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

        String newquery = cleanUpQuery(query);        

        ArrayList<String> tokens = createTokens(query);

        if((tokens.size() == 1) || (tokens.size() == 2 && tokens.get(0).equals("!"))){
            if(newquery.charAt(0) == '!'){
                Set<Page> allURL = index.getAllURL();
                allURL.removeAll(wordQuery(newquery.substring(1)));
                return allURL;
            }
            if(newquery.charAt(0) == '\"'){
                return phraseQuery(tokens.get(0));
            }
            return wordQuery(newquery);
        }
        else{
            return complexQuery(tokens);
        }

    }

    public HashSet<Page> wordQuery(String word){
        HashSet<Page> lol = index.getURLForWord(word);
        return lol;
    }

    public HashSet<Page> phraseQuery(String phrase){
        String [] words = phrase.split(" ");

        return index.getURLForAllPhrase(index.getURLForAllWords(words), words);
    }

    public Set<Page> complexQuery(ArrayList<String> tokens){
        Queue<String> que = parseQueryQueue(tokens);
        String qpoll;

        Stack<Set <Page>> finalStack = new Stack<Set <Page>>();

        while(!que.isEmpty()){
            qpoll = que.poll();
            
            //pushes to stack if not operator
            if(!((qpoll.equals("&")) || (qpoll.equals("|")) || (qpoll.equals("!")))){
                if(qpoll.contains(" ")){
                    finalStack.push(phraseQuery(qpoll));
                }
                else{
                    finalStack.push(wordQuery(qpoll));
                }
            }
            else{
                Set<Page> setTok1 = null;

                setTok1 = finalStack.pop();
            
                //dealing with !
                if(qpoll.equals("!")){
                    HashSet<Page> allURLSet = new HashSet<Page>(index.getAllURL());

                    allURLSet.remove(setTok1);
                    finalStack.push(allURLSet);
                }
                else{
                    String stringTok2 = "";
                    Set<Page> setTok2 = null;

                    setTok2 = (Set<Page>) finalStack.pop();

                    if(qpoll.equals("&")){
                        setTok1.retainAll(setTok2);
                        finalStack.push(setTok1);
                    }
                    else{
                        setTok1.addAll(setTok2);
                        finalStack.push(setTok1);
                    }
                }
                
            }
            
        }
        return finalStack.pop();


    }

    public Queue<String> parseQueryQueue(ArrayList<String> tokens){
        Stack<String> operStack = new Stack<String>();
        Queue<String> que = new LinkedList<String>();

        for(int i = 0; i < tokens.size(); i++){
            if((tokens.get(i).equals(")"))){
                
                //add operators to queue
                while((!operStack.peek().equals("("))){
                    que.add(operStack.pop());
                }
                //remove open paranthesis
                operStack.pop();

                if(!operStack.isEmpty() && (operStack.peek().equals("!"))){
                    que.add(operStack.pop());
                }

            }
            //add operators to the stack
            else if((tokens.get(i).equals("&")) || (tokens.get(i).equals("|")) || (tokens.get(i).equals("(")) || (tokens.get(i).equals("!"))){
                operStack.push(tokens.get(i));
            }
            //add any words/phrases to the queue
            else{
                que.add(tokens.get(i));
            }
        }

        return que;
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
            else if((line.charAt(i) == '&') || (line.charAt(i) == '|') || (line.charAt(i) == '(') || (line.charAt(i) == ')') || (line.charAt(i) == '!')){
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
                if(i == line.length()){
                    allTokens.add(token);
                }
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

    public String cleanUpQuery(String query){
        query = query.replaceAll("[ ]+", " ");
        query = query.toLowerCase();
        boolean isPhrase = false;
        String newQuery = "";
        
        for(int i = 0; i < query.length(); i++){
            newQuery = newQuery + query.charAt(i);

            if(!isPhrase){
                if(query.charAt(i) == '\"'){
                    isPhrase = true;
                }
                if(query.charAt(i) == ' '){
                    newQuery = newQuery.substring(0, newQuery.length() - 1) + "&";
                    if((newQuery.charAt(newQuery.length() - 2) == '!') || (newQuery.charAt(newQuery.length() - 2) == '|') || (newQuery.charAt(newQuery.length() - 2) == ')')){
                        newQuery = newQuery.substring(0, newQuery.length() - 1);
                    }
                }
            }
            else{
                if(query.charAt(i) == '\"'){
                    isPhrase = false;
                }
            }
        }

        return newQuery;
    }
}
