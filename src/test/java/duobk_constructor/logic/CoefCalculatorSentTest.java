package duobk_constructor.logic;

import duobk_constructor.logic.book.Sentence;
import duobk_constructor.logic.book.duo.DuoParagraph;
import duobk_constructor.logic.book.duo.DuoSentence;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CoefCalculatorSentTest {

    @Test
    public void calculate() {
        // useless, incorrect test. I hoped no one will ever see this, but if u are reading this, it's a f***ing success.
        // get coef of correct
        String sent1 = "Pennies saved one and two at a time by bulldozing the grocer and the vegetable man and the butcher until one’s cheeks burned with the silent imputation of parsimony that such close dealing implied. ";
        String sent2 = "За каждую из этих монеток пришлось торговаться с бакалейщиком, зеленщиком, мясником так, что даже уши горели от безмолвного неодобрения, которое вызывала подобная бережливость. ";
        Double coef1 = getCoef(sent1, sent2);
        // get coef of incorrect
        String sent3 = "Делла пересчитала три раза.";
        ArrayList sentences = new ArrayList();
        sentences.add(sent2);
        sentences.add(sent3);
        Double coef2 = getCoef(sent1, sentences);
        assertTrue(coef1 < coef2);
    }
    private Double getCoef(ArrayList<String> stringSents1, ArrayList<String> stringSents2){
        CoefCalculatorSent calculator = new CoefCalculatorSent();
        // create correct DuoSentence
        ArrayList<Sentence> sentences1 = new ArrayList<>();
        for(String sent : stringSents1)
            sentences1.add(new Sentence(sent,null));
        ArrayList<Sentence> sentences2 = new ArrayList<>();
        for(String sent : stringSents2)
            sentences2.add(new Sentence(sent,null));
        DuoSentence duoSentence = new DuoSentence(sentences1, sentences2);
        Double coef = calculator.calculate(duoSentence);
        return coef;
    }
    private Double getCoef(String stringSent1, ArrayList<String> stringSents2){
        CoefCalculatorSent calculator = new CoefCalculatorSent();
        // create correct DuoSentence
        Sentence sentence1 = new Sentence(stringSent1,null);
        ArrayList<Sentence> sentences2 = new ArrayList<>();
        for(String sent : stringSents2)
            sentences2.add(new Sentence(sent,null));
        DuoSentence duoSentence = new DuoSentence(sentence1, sentences2);
        Double coef = calculator.calculate(duoSentence);
        return coef;
    }
    private Double getCoef(String stringSent1, String stringSent2){
        CoefCalculatorSent calculator = new CoefCalculatorSent();
        // create correct DuoSentence
        Sentence sentence1 = new Sentence(stringSent1,null);
        Sentence sentence2 = new Sentence(stringSent2,null);
        DuoSentence duoSentence = new DuoSentence(sentence1, sentence2);
        Double coef = calculator.calculate(duoSentence);
        return coef;
    }
}