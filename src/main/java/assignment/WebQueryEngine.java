package assignment;
import java.net.URL;
import java.nio.channels.NetworkChannel;
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

        System.out.println("the newquery is: " + newquery);
        ArrayList<String> tokens = createTokens(newquery);

        if((tokens.size() == 1) || (tokens.size() == 2 && tokens.get(0).equals("!"))){
            if(newquery.charAt(0) == '!'){
                newquery = tokens.get(1);
                if(newquery.contains(" ")){
                    System.out.println("bitch are u going in here");
                    Set<Page> allURL = index.getAllURL();
                    allURL.removeAll(phraseQuery(newquery));
                    System.out.println("phrase is: " + newquery + ", " + allURL.size());
                    return allURL;
                }
                else{
                    Set<Page> allURL = index.getAllURL();
                    allURL.removeAll(wordQuery(newquery.substring(1)));
                    System.out.println(allURL.size());
                    return allURL;
                }
                
            }
            if(newquery.charAt(0) == '\"'){
                HashSet<Page> returnSet = phraseQuery(tokens.get(0));
                System.out.println(returnSet.size());
                return returnSet;
            }
            HashSet<Page> returnSet = wordQuery(newquery);
            System.out.println(returnSet.size());
            return returnSet;
        }
        else{
            Set<Page> returnSet = complexQuery(tokens);
            System.out.println(returnSet.size());
            return returnSet;
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
                    System.out.println("pushed from space and pushed: " + qpoll + ", " + phraseQuery(qpoll).size());
                }
                else{
                    finalStack.push(wordQuery(qpoll));
                    System.out.println("pushed from word query and pushed: " + qpoll + ", " + wordQuery(qpoll).size());
                }
            }
            else{
                Set<Page> setTok1 = finalStack.pop();
            
                //dealing with !
                if(qpoll.equals("!")){
                    HashSet<Page> allURLSet = new HashSet<Page>(index.getAllURL());

                    allURLSet.removeAll(setTok1);
                    finalStack.push(allURLSet);
                    System.out.println("pushed from !: " + allURLSet.size());

                }
                else{
                    Set<Page> setTok2 = null;

                    setTok2 = finalStack.pop();

                    if(qpoll.equals("&")){
                        System.out.println("size of setTok1 and size of setTok2: " + setTok1.size() + ", " + setTok2.size());
                        setTok1.retainAll(setTok2);
                        System.out.println("sixe of setTok1: " + setTok1.size());
                        finalStack.push(setTok1);
                        System.out.println("pushed from and: " + setTok1.size());
                    }
                    else{
                        setTok1.addAll(setTok2);
                        finalStack.push(setTok1);
                        System.out.println("pushed from or: " + setTok1.size());

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

            }
            else if (tokens.get(i).equals("(")){
                operStack.push(tokens.get(i));
            }
            else if ((tokens.get(i).equals("!"))){
                operStack.push(tokens.get(i));
                
            }
            //add operators to the stack
            else if((tokens.get(i).equals("&"))|| (tokens.get(i).equals("|"))){
                
                while((!operStack.isEmpty()) && (!operStack.peek().equals("("))){
                    que.add(operStack.pop());
                }

                operStack.push(tokens.get(i));
            }    
            //add any words/phrases to the queue
            else{
                que.add(tokens.get(i));
            }
        }

        while(!operStack.isEmpty()){
            que.add(operStack.pop());
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

        System.out.println("query without spaces is: " + query);
        boolean isPhrase = false;
        String newQuery = "";
        
        for(int i = 0; i < query.length(); i++){
            newQuery = newQuery + query.charAt(i);

            if(!isPhrase){
                if(query.charAt(i) == '\"'){
                    isPhrase = true;
                }
                if(query.charAt(i) == ' '){
                            
                    // newQuery = newQuery.substring(0, newQuery.length() - 1) + "&";
                    // if((newQuery.charAt(newQuery.length() - 2) == '!') || (newQuery.charAt(newQuery.length() - 2) == '|') || (newQuery.charAt(newQuery.length() - 2) == ')') || (newQuery.charAt(newQuery.length() - 2) == '&')){
                    //     newQuery = newQuery.substring(0, newQuery.length() - 1);
                    //     if((newQuery.charAt(newQuery.length() - 2) == '&') && (newQuery.charAt(newQuery.length() - 1) == '&')){
                    //         newQuery = newQuery.substring(0, newQuery.length() - 1);
                    //     }
                    // }
                   
                    newQuery = newQuery.substring(0, newQuery.length() - 1);
                    if((i+1 < query.length()) && (i-1 >= 0)){
                        // if(((query.charAt(i+1) == '!') || (query.charAt(i+1) == '|') || (query.charAt(i+1) == ')') || (query.charAt(i+1) == '&') || (query.charAt(i-1) == '!') || (query.charAt(i-1) == '|') || (query.charAt(i-1) == '&') || (query.charAt(i-1) == '('))){
                        
                        // }
                        if(((query.charAt(i+1) == '|') || (query.charAt(i+1) == ')') || (query.charAt(i+1) == '&') || (query.charAt(i-1) == '!') || (query.charAt(i-1) == '|') || (query.charAt(i-1) == '&') || (query.charAt(i-1) == '('))){
                        
                        }
                        else{
                            newQuery = newQuery + "&";
                        }

                    }
                }
            }
            else{
                if(query.charAt(i) == '\"'){
                    isPhrase = false;
                }
            }

            // System.out.println(newQuery);


        }

        return newQuery;
    }
}
