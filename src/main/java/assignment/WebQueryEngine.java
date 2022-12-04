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

        ArrayList<String> tokens = createTokens(query);

        if(tokens.size() == 1){
            if(newquery.charAt(0) == '!'){
                System.out.println("got to negations for word");
                Set<Page> allURL = index.getAllURL();
                allURL.removeAll(wordQuery(newquery.substring(1)));
                return allURL;
            }
            return wordQuery(newquery);
        }
        else{
            return phraseQuery(tokens);
        }

    }

    public HashSet<Page> wordQuery(String word){
        HashSet<Page> lol = index.getURLForWord(word);
        System.out.println(lol);
        return lol;
    }

    public Set<Page> phraseQuery(ArrayList<String> tokens){
        System.out.println("in phrase query at all");
        Queue<String> que = parseQueryQueue(tokens);
        String qpoll;
        boolean isString;

        Stack<Set <Page>> finalStack = new Stack<Set <Page>>();

        while(!que.isEmpty()){
            qpoll = que.poll();
            System.out.println("qpoll is: " + qpoll);
            
            if(!((qpoll.equals("&")) || (qpoll.equals("|")) || (qpoll.equals("!")))){
                finalStack.push(wordQuery(qpoll));
            }
            else{
                System.out.println("into phrase query for operators");
                String stringTok1 = "";
                Set<Page> setTok1 = null;

                // if(finalStack.peek() instanceof String){
                //     stringTok1 = (String) finalStack.pop();
                //     setTok1 = wordQuery(stringTok1);
                //     // isString = true;
                // }
                // else{
                    setTok1 = (Set<Page>) finalStack.pop();
                    // isString = false;
                // }

                if(qpoll.equals("!")){
                    Set<Page> allURLSet = index.getAllURL();
                    
                    // if(isString){
                    //     finalStack.push(allURLSet.removeAll(wordQuery(stringTok1)));
                    // }
                    // else{
                    //     finalStack.push(allURLSet.removeAll(setTok1));
                    // }

                    allURLSet.remove(setTok1);
                    finalStack.push(allURLSet);
                }
                else{
                    String stringTok2 = "";
                    Set<Page> setTok2 = null;
    
                    // if(finalStack.peek() instanceof String){
                    //     stringTok2 = (String) finalStack.pop();
                    //     setTok2 = wordQuery(stringTok2);
                    //     // isString = true;
                    // }
                    // else{
                        setTok2 = (Set<Page>) finalStack.pop();
                        // isString = false;
                    // }

                    if(qpoll.equals("&")){
                        System.out.println("got to and");
                        setTok1.retainAll(setTok2);
                        finalStack.push(setTok1);
                    }
                    else{
                        System.out.println("got to or");
                        setTok1.addAll(setTok2);
                        finalStack.push(setTok1);
                    }
                }
                
            }
            
        }
        System.out.println(finalStack.peek());
        return (Set<Page>) finalStack.pop();


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
