package duobk_constructor.logic;


import duobk_constructor.logic.book.Sentence;
import duobk_constructor.logic.book.duo.DuoSentence;

public class CoefCalculatorSent {
    public CoefCalculatorSent() {
    }
    public double calculate(DuoSentence sentence){
        int wordsCount1 = 0;
        int wordsCount2 = 0;
        for(Sentence sent : sentence.getSentences1()){
            wordsCount1 += sent.toString().split(" ").length;
        }
        for(Sentence sent : sentence.getSentences2()){
            wordsCount2 += sent.toString().split(" ").length;
        }
        int wordCountDiff = Math.abs(wordsCount1 - wordsCount2);
        double result = 100;
        if(wordsCount1 < 10)
            return result - wordCountDiff*10;
        else if (wordsCount1 < 20)
            return result - wordCountDiff*7;
        else if (wordsCount1 < 30)
            return  result - wordCountDiff*4;
        else return result - wordCountDiff*3;
    }
}
