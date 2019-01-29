package duobk_constructor.logic.book;


import duobk_constructor.logic.SentenceBreaker;
import duobk_constructor.logic.book.duo.ParagraphData;

import java.util.ArrayList;

public class Paragraph {
    public ArrayList<Sentence> getSentences() {
        return sentences;
    }

    private ArrayList<Sentence> sentences;
    private ParagraphData data;

    public ParagraphData getData() {
        return data;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    private int index;
    private Chapter chapter;
    public Paragraph(String content, Chapter chapter){
        sentences = new ArrayList<>();
        ArrayList<String> stringSentences = SentenceBreaker.breakString(content.replace("\n",""), chapter.getBook().getLanguage().toString());
        for (String el : stringSentences){
            sentences.add(new Sentence(el,this));
        }
        data = new ParagraphData(this);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Sentence sentence : sentences){
            sb.append(sentence.toString());
        }
        return  sb.toString();
    }
}
