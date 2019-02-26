package duobk_constructor.logic;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SentenceBreakerTest {

    @Test
    public void breakString() {
        String sentence = "In the vestibule below was a letter-box into which no letter would go, and an electric button from which no mortal finger could coax a ring. Also appertaining thereunto was a card bearing the name Mr. James Dillingham Young.";
        SentenceBreaker breaker = new SentenceBreaker();
        ArrayList<String> sents = breaker.breakString(sentence, "eng");
        assertTrue(sents.size()>0);
    }
    @Test
    public void test2() {
        String sentence = "This is a sentence.  It has fruits, vegetables,\" + \" etc. but does not have meat.  Mr. Smith went to Washington.";
        SentenceBreaker breaker = new SentenceBreaker();
        ArrayList<String> sents = breaker.breakString(sentence, "eng");
        assertTrue(sents.size()>0);
    }
}