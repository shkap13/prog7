package assignment;
import assignment.WebQueryEngine;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Queue;
import java.util.LinkedList;

import javax.security.auth.kerberos.KerberosPrincipal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

public class Testing {
    
    @Test
    public void testCreateTokens(){
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
        token2.add("ged");
        token2.add("(");
        token2.add(")");
       

        System.out.println(eng.createTokens("abcde|\"pqr hello\"&\"ged\"()"));

        assertAll(
            () -> assertTrue(eng.createTokens("\"abc\"&\"def\"").equals(token1)),
            () -> assertTrue(eng.createTokens("abcde|\"pqr hello\"&\"ged\"()").equals(token2))
        );
    }

    // @Test 
    // public void testParseQueryQueue(){
    //     WebQueryEngine eng = new WebQueryEngine();
    //     Queue<String> actualQue = eng.parseQueryQueue("((hello&it)|(\"jk i love\"|lmao))");

    //     Queue<String> que = new LinkedList<String>();
    //     que.add("hello");
    //     que.add("it");
    //     que.add("&");
    //     que.add("jk i love");
    //     que.add("lmao");
    //     que.add("|");
    //     que.add("|");

    //     assertTrue(actualQue.equals(que));

    // }

    @Test
    public void testGetURLForAllWords(){
        
    }

    @Test
    public void testCleanUpQuery(){
        WebQueryEngine eng = new WebQueryEngine();

        assertAll(
            () -> assertEquals("!not", eng.cleanUpQuery("! not")),
            () -> assertEquals("click&pages", eng.cleanUpQuery("click     pages")),
            () -> assertEquals("(!(\"hallelujah lin\"&please)|save)", eng.cleanUpQuery("(! (\"hallelujah LIN\" please)     | save)")),
            () -> assertEquals("(should&quiet)", eng.cleanUpQuery("(should & quiet)")),
            () -> assertEquals("(a|!a)", eng.cleanUpQuery("(a | !a)"))


        );
        
    }

    @Test
    public void testRemoveRelative(){
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        String lol = handler.removeRelative("/Users/shreyakappala/Desktop/workspace/prog7/../../../president96/");
        System.out.println(lol);

        assertEquals("/Users/shreyakappala/president96/", lol);
    }
}
