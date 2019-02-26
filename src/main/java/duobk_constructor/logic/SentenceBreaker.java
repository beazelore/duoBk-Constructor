package duobk_constructor.logic;


import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

public class SentenceBreaker {
    public static ArrayList<String> breakString(String string, String lang){
        ArrayList<String> result = new ArrayList<>();
        Locale locale = new Locale(lang);
        BreakIterator iterator = BreakIterator.getSentenceInstance(locale);
        iterator.setText(string);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            result.add(string.substring(start,end));
        }
        return result;
    }
}
