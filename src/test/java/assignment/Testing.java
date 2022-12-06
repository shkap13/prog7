package assignment;
import assignment.WebQueryEngine;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.kerberos.KerberosPrincipal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;


public class Testing {
    //CRAWLING MARKUP HANDLER

    @Test
    public void testRemoveRelative(){
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        String lol = handler.removeRelative("/Users/shreyakappala/Desktop/workspace/prog7/../../../president96/");
        System.out.println(lol);

        assertEquals("/Users/shreyakappala/president96/", lol);

        lol = handler.removeRelative("/Users/shreyakappala/Desktop/workspace/prog7/../../minitestfolder/page0");
        assertEquals("/Users/shreyakappala/Desktop/minitestfolder/page0", lol);

        // assertEquals("/Users/shreyakappala/Desktop/minitestfolder/page0", handler.removeRelative("/Users/shreyakappala/Desktop/minitestfolder/page0"));
    }


    @Test
    public void testSetCurrentPathString(){
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();
        
        //changes when there is relative path and when there is not
        assertEquals("/Users/shreyakappala/president96/", handler.setCurrentPathString("/Users/shreyakappala/Desktop/workspace/prog7/../../../president96/index.html"));
        assertEquals("/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/", handler.setCurrentPathString("/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0"));
        assertEquals("/Users/shreyakappala/Desktop/minitestfolder/", handler.setCurrentPathString("/Users/shreyakappala/Desktop/workspace/prog7/../../minitestfolder/page0"));

    }

    @Test
    public void testUpdateAllPath() throws MalformedURLException{
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();
        handler.setCurrentPathString("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0");

        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("href", "page0");

        handler.handleOpenElement("a", attributes1, 0, 0);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("href", "page1");

        handler.handleOpenElement("a", attributes2, 0, 0);

        ArrayList<String> url1 = handler.allPaths;

        ArrayList<String> myList1 = new ArrayList<String>();
        myList1.add(("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0"));
        myList1.add("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page1");

        handler.handleOpenElement("a", attributes2, 0, 0);

        assertEquals(url1.get(0), myList1.get(0));
        assertEquals(url1.get(1), myList1.get(1));
        assertEquals(myList1.size(), url1.size());
        
    }

    @Test
    public void testNewURL() throws MalformedURLException{
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();
        handler.setCurrentPathString("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0");

        HashMap<String, String> attributes1 = new HashMap<String, String>();
        attributes1.put("href", "page0");

        handler.handleOpenElement("a", attributes1, 0, 0);

        HashMap<String, String> attributes2 = new HashMap<String, String>();
        attributes2.put("href", "page1");

        handler.handleOpenElement("a", attributes2, 0, 0);

        List<URL> url1 = handler.newURLs();

        List<URL> myList1 = new LinkedList<URL>();
        myList1.add((new URL("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0")));
        myList1.add(new URL("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page1"));

        assertEquals(url1.get(0), myList1.get(0));
        assertEquals(url1.get(1), myList1.get(1));
        


        HashMap<String, String> attributes3 = new HashMap<String, String>();
        attributes3.put("href", "page2");

        handler.handleOpenElement("a", attributes3, 0, 0);

        url1 = handler.newURLs();
        myList1.clear();
        myList1.add(new URL("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page2"));

        assertEquals(myList1.size(), url1.size());
        assertEquals(myList1.get(0), url1.get(0));
    }


    //WEBINDEX METHODS

    @Test
    public void testAddURL() throws MalformedURLException{
        WebIndex index = new WebIndex();

        index.addURl("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0 YOO ITS PAGE 0 click for page 1 click for page 2 xD senator");

        assertEquals(15, index.wordStrings.length);
        assertEquals(1, index.urlMap.size());
        Set<Page> temp = new HashSet<Page>();
        
        temp.add(new Page(new URL("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0")));
        assertEquals(temp.size(), index.urlMap.keySet().size());
        System.out.println(index.urlMap.toString());

        temp.add(new Page(new URL("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page1")));
        index.addURl("file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page1 hello my name is bob lmao hi bob");
        assertEquals(temp.size(), index.urlMap.keySet().size());
        System.out.println(index.urlMap.toString());
    }

    @Test
    public void testSetWordMap() throws MalformedURLException{
        boolean ayo = false;

        WebIndex index = new WebIndex();
        String s1 = "file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0 YOO ITS PAGE 0 click for page 1 click for page 2 xD senator";
        String s2 = "file:/Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page1 hello my name is bob lmao hi bob";

        String [] arr1 = s1.split(" ");
        String [] arr2 = s2.split(" ");

        index.addURl(s1);
        index.addURl(s2);


        HashMap map = index.returnURLMapCopy();
        
        Iterator iter = map.keySet().iterator();

        HashMap<String, HashSet<Integer>> map1 = new HashMap<>();
        HashMap<String, HashSet<Integer>> map2 = new HashMap<>();


        if(iter.hasNext()){
            map1 = (HashMap<String, HashSet<Integer>>) map.get((Page) iter.next());
        }
        

        if(iter.hasNext()){
            map2 = (HashMap<String, HashSet<Integer>>) map.get((Page) iter.next());
        }
        
        System.out.println(map1);

        for(int i = 1; i < arr1.length; i++){
            ayo = false;
            if(map2.get(arr1[i].toLowerCase()) != null){
                ayo = true;
            }

            System.out.println(arr1[i].toLowerCase());
            assertTrue(ayo);
        }

        for(int i = 1; i < arr2.length; i++){
            ayo = false;
            if(map1.get(arr2[i].toLowerCase()) != null){
                ayo = true;
            }

            assertTrue(ayo);
        }
    
    }

    @Test
    public void testGetURLForWords(){

        String [] args = {"file:///Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0"};
        WebCrawler.main(args);
        

        assertEquals(3, WebCrawler.index.getURLForWord("click").size());
        assertEquals(2, WebCrawler.index.getURLForWord("senator").size());
        assertEquals(2, WebCrawler.index.getURLForWord("hallelujah").size());
        assertEquals(3, WebCrawler.index.getURLForWord("click").size());
        assertEquals(1, WebCrawler.index.getURLForWord("enddddd").size());
        
    }

    @Test
    public void testGetURLForAllWords(){

        String [] args = {"file:///Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0"};
        WebCrawler.main(args);

        String [] arr1 = {"click", "for", "page"};
        String [] arr2 = {"senator", "page"};
        String [] arr3 = {"hallelujah"};
        String [] arr4 = {"click", "for", "page", "2"};
        String [] arr5 = {"click", "for", "god", "2"};
        String [] arr6 = {"xD", "officials", "praying", "enddddd"};

        
        assertEquals(3, WebCrawler.index.getURLForAllWords(arr1).size());
        assertEquals(2, WebCrawler.index.getURLForAllWords(arr2).size());
        assertEquals(2, WebCrawler.index.getURLForAllWords(arr3).size());
        assertEquals(3, WebCrawler.index.getURLForAllWords(arr4).size());
        assertEquals(0, WebCrawler.index.getURLForAllWords(arr5).size());
        assertEquals(0, WebCrawler.index.getURLForAllWords(arr6).size());

        
    }

    @Test
    public void testgetURLForAllPhrases(){
        String [] args = {"file:///Users/shreyakappala/Desktop/workspace/prog7/minitestfolder/page0"};
        WebCrawler.main(args);

        //all words passed here need to be lowercased because they would have been forced lowercase earlier

        String [] arr1 = {"click", "for", "page"};
        String [] arr2 = {"hold", "your", "breath"};
        String [] arr3 = {"hallelujah", "praying"};
        String [] arr4 = {"xd", "senator"};
        String [] arr5 = {"for", "good"};
        String [] arr6 = {"play", "time"};

        System.out.println(WebCrawler.index.getURLForAllWords(arr4).size());


        assertEquals(3, WebCrawler.index.getURLForAllPhrase(WebCrawler.index.getURLForAllWords(arr1), arr1).size());
        assertEquals(2, WebCrawler.index.getURLForAllPhrase(WebCrawler.index.getURLForAllWords(arr2), arr2).size());
        assertEquals(2, WebCrawler.index.getURLForAllPhrase(WebCrawler.index.getURLForAllWords(arr3), arr3).size());
        assertEquals(1, WebCrawler.index.getURLForAllPhrase(WebCrawler.index.getURLForAllWords(arr4), arr4).size());
        assertEquals(0, WebCrawler.index.getURLForAllPhrase(WebCrawler.index.getURLForAllWords(arr5), arr5).size());
        assertEquals(0, WebCrawler.index.getURLForAllPhrase(WebCrawler.index.getURLForAllWords(arr6), arr6).size());


    }


    //WEBQUERYENGINE METHODS

    @Test
    public void testCleanUpQuery(){
        WebQueryEngine eng = new WebQueryEngine();

        assertAll(
            () -> assertEquals("!not", eng.cleanUpQuery("! not")),
            () -> assertEquals("click&pages", eng.cleanUpQuery("click     pages")),
            () -> assertEquals("(!(\"hallelujah lin\"&please)|save)", eng.cleanUpQuery("(! (\"hallelujah LIN\" please)     | save)")),
            () -> assertEquals("(should&quiet)", eng.cleanUpQuery("(should & quiet)")),
            () -> assertEquals("(a|!a)", eng.cleanUpQuery("(a | !a)")),
            () -> assertEquals("\"pleasegodhelpme\"&!looll", eng.cleanUpQuery(" \"pleasegodhelpme\"&        !      looll")),
            () -> assertEquals("i&love&chocolate", eng.cleanUpQuery("i love chocolate")),
            () -> assertEquals("hall&bob&!george", eng.cleanUpQuery("\t \t \n \r hall & \r bob \t ! \t \t George"))

        );
        
    }

    @Test
    public void testCreatePhraseTokens(){
        WebQueryEngine eng = new WebQueryEngine();

        assertEquals(" hello lalalala ", eng.createPhraseToken(0, "\" hello lalalala \" cake "));
        assertEquals("hihi", eng.createPhraseToken(9, " asdkfj; \"hihi\""));

    }

    
    @Test
    public void testCreateTokens(){
        //MAYBE ADD MORE???
        WebQueryEngine eng = new WebQueryEngine();
        ArrayList<String> token1 = new ArrayList<>();
        token1.add("abc");
        token1.add("&");
        token1.add("def");

        ArrayList<String> token2 = new ArrayList<>();
        token2.add("abcde");
        token2.add("|");
        token2.add("pqr hello");
        token2.add("&");
        token2.add("!");
        token2.add("ged");
        token2.add("(");
        token2.add(")");
       

        assertAll(
            () -> assertTrue(eng.createTokens("\"abc\"&\"def\"").equals(token1)),
            () -> assertTrue(eng.createTokens("abcde|\"pqr hello\"&!\"ged\"()").equals(token2))
        );
    }


    @Test 
    public void testParseQueryQueue(){
        WebQueryEngine eng = new WebQueryEngine();
        ArrayList<String> tokens = new ArrayList<String>(); 
        tokens.add("hello");
        tokens.add("&");
        tokens.add("!");
        tokens.add("hallelujah");
        tokens.add("&");
        tokens.add("(");
        tokens.add("save me");
        tokens.add("|");
        tokens.add("im fine");
        tokens.add(")");
        Queue<String> actualQue = eng.parseQueryQueue(tokens);

        Queue<String> que = new LinkedList<String>();
        que.add("hello");
        que.add("hallelujah");
        que.add("!");
        que.add("&");
        que.add("save me");
        que.add("im fine");
        que.add("|");
        que.add("&");

        assertEquals(que, actualQue);

    }

    @Test
    public void testComplexQuery(){
        String [] args = {"file:///Users/shreyakappala/Desktop/workspace/prog7/president96/index.html"};
        WebCrawler.main(args);
        
        WebIndex index = WebCrawler.index;

        WebQueryEngine web = new WebQueryEngine(index);

        ArrayList<String> begging = new ArrayList<String>();

        begging.add("help");
        begging.add("&");
        begging.add("(");
        begging.add("me");
        begging.add("|");
        begging.add("!");
        begging.add("me");
        begging.add(")");

        assertEquals(web.wordQuery("help").size(), web.complexQuery(begging).size());
    }
}
