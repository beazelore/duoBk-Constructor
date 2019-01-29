package duobk_constructor.logic.book.duo;

import duobk_constructor.logic.CoefCalculator;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.Sentence;

import java.util.ArrayList;

public class DuoParagraph {
    private ArrayList<DuoSentence> sentences;
    private ArrayList<Paragraph> paragraphs1;

    public int getWeight() {
        if (weight == -1){
            CoefCalculator calc = new CoefCalculator();
            weight = (int)calc.calculate(this);
        }
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    private int weight = -1;

    public ArrayList<Paragraph> getParagraphs2() {
        return paragraphs2;
    }


    ArrayList<Paragraph> paragraphs2;

    public ArrayList<Paragraph> getParagraphs1() {
        return paragraphs1;
    }


    public ArrayList<DuoSentence> getSentences() {
        return sentences;
    }

    public void setSentences(ArrayList<DuoSentence> sentences) {
        this.sentences = sentences;
    }

    public DuoParagraph() {
    }

    public DuoParagraph(ArrayList<DuoSentence> sentences) {
        this.sentences = sentences;
    }

    public DuoParagraph(ArrayList<Paragraph> paragraphs1, ArrayList<Paragraph> paragraphs2) {
        this.paragraphs1 = paragraphs1;
        this.paragraphs2 = paragraphs2;
        int duoIndex = 0;
        for(Paragraph p : paragraphs1){
            for(Sentence s : p.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
        }
        duoIndex = 0;
        for(Paragraph p : paragraphs2){
            for(Sentence s : p.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
        }
    }
    public DuoParagraph(Paragraph paragraph1, ArrayList<Paragraph> paragraphs2) {
        this.paragraphs1 = new ArrayList<>();
        paragraphs1.add(paragraph1);
        this.paragraphs2 = paragraphs2;
        int duoIndex = 0;
            for(Sentence s : paragraph1.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
        duoIndex = 0;
        for(Paragraph p : paragraphs2){
            for(Sentence s : p.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
        }
    }
    public DuoParagraph(ArrayList<Paragraph> paragraphs1, Paragraph paragraph2) {
        this.paragraphs1 = paragraphs1;
        this.paragraphs2 = new ArrayList<>();
        paragraphs2.add(paragraph2);
        int duoIndex = 0;
        for(Paragraph p : paragraphs1){
            for(Sentence s : p.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
        }
        duoIndex = 0;
            for(Sentence s : paragraph2.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
    }
    public DuoParagraph(Paragraph paragraph1, Paragraph paragraph2) {
        this.paragraphs1 = new ArrayList<>();
        this.paragraphs2 = new ArrayList<>();
        this.paragraphs1.add(paragraph1);
        this.paragraphs2.add(paragraph2);
        int duoIndex = 0;
            for(Sentence s : paragraph1.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
        duoIndex = 0;
            for(Sentence s : paragraph2.getSentences()){
                s.setIndexInDuo(duoIndex);
                duoIndex++;
            }
    }

    public Sentence getSentenceByDuoIndex(int duoIndex, boolean fromFirst){
        if(fromFirst){
            for(Paragraph p : paragraphs1){
                for(Sentence s : p.getSentences()){
                    if(s.getIndexInDuo() == duoIndex)
                        return s;
                }
            }
        }
        else{

            for(Paragraph p : paragraphs2){
                for(Sentence s : p.getSentences()){
                    if(s.getIndexInDuo() == duoIndex)
                        return s;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DuoParagraph){
            DuoParagraph par = (DuoParagraph)obj;
            if (par.getParagraphs1().size() != paragraphs1.size() || par.getParagraphs2().size() != paragraphs2.size())
                return false;
            for (int i=0, k =0; i < paragraphs1.size() && k < par.getParagraphs1().size(); k++, i++){
                if (paragraphs1.get(i) != par.getParagraphs1().get(k))
                    return false;
            }
            for (int i=0, k =0; i < paragraphs2.size() && k < par.getParagraphs2().size(); k++, i++){
                if (paragraphs2.get(i) != par.getParagraphs2().get(k))
                    return false;
            }
            return true;
        }
        else return false;
    }
}
