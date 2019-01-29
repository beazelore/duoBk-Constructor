package duobk_constructor.logic.book.duo;


import duobk_constructor.logic.CoefCalculatorSent;
import duobk_constructor.logic.book.Sentence;

import java.util.ArrayList;

public class DuoSentence {
    public DuoSentence(ArrayList<Sentence> sentences1, ArrayList<Sentence> sentences2) {
        this.sentences1 = sentences1;
        this.sentences2 = sentences2;
    }
    public DuoSentence(Sentence sentence1, ArrayList<Sentence> sentences2) {
        this.sentences1 = new ArrayList<>();
        this.sentences1.add(sentence1);
        this.sentences2 = sentences2;
    }
    public DuoSentence(ArrayList<Sentence> sentences1, Sentence sentence2) {
        this.sentences1 = sentences1;
        this.sentences2 = new ArrayList<>();
        this.sentences2.add(sentence2);
    }
    public DuoSentence(Sentence sentence1, Sentence sentence2) {
        this.sentences1 = new ArrayList<>();
        this.sentences2 = new ArrayList<>();
        this.sentences2.add(sentence2);
        this.sentences1.add(sentence1);
    }

    private ArrayList<Sentence> sentences1;
    private ArrayList<Sentence> sentences2;
    private int weight = -1;

    public int getWeight() {
        if(weight == -1){
            CoefCalculatorSent calculator= new CoefCalculatorSent();
            weight = (int)calculator.calculate(this);
        }
        return weight;
    }

    public ArrayList<Sentence> getSentences1() {
        return sentences1;
    }

    public void setSentences1(ArrayList<Sentence> sentences1) {
        this.sentences1 = sentences1;
    }

    public ArrayList<Sentence> getSentences2() {
        return sentences2;
    }

    public void setSentences2(ArrayList<Sentence> sentences2) {
        this.sentences2 = sentences2;
    }


    public DuoSentence() {
        sentences1 = new ArrayList<>();
        sentences2 = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DuoSentence))
            return false;
        DuoSentence other = (DuoSentence)obj;
        if(other.getSentences1().size() != sentences1.size() || other.getSentences2().size() != sentences2.size())
            return false;
        for(int i =0; i < other.getSentences1().size(); i++){
            if(!sentences1.get(i).equals(other.getSentences1().get(i)))
                return false;
        }
        for(int i =0; i < other.getSentences2().size(); i++){
            if(!sentences2.get(i).equals(other.getSentences2().get(i)))
                return false;
        }
        return true;
    }
}
