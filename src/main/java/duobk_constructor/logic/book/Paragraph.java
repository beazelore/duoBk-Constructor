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
        if(data == null){
            data = new ParagraphData(this);
        }
        return data;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public Chapter getChapter() {
        return chapter;
    }

    private Integer index;
    private Chapter chapter;
    public Paragraph(String content, Chapter chapter){
        sentences = new ArrayList<>();
        this.chapter = chapter;
        ArrayList<String> stringSentences = SentenceBreaker.breakString(content.replace("\n"," "), chapter.getBook().getLanguage().toString());
        for (String el : stringSentences){
            sentences.add(new Sentence(el,this));
        }
        data = new ParagraphData(this);
    }
    public Paragraph(String content, String lang){
        sentences = new ArrayList<>();
        ArrayList<String> stringSentences = SentenceBreaker.breakString(content.replace("\n"," "), lang);
        for (String el : stringSentences){
            sentences.add(new Sentence(el,this));
        }
    }

    public Paragraph(Chapter chapter) {
        this.chapter = chapter;
        sentences = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Sentence sentence : sentences){
            sb.append(sentence.toString());
        }
        return  sb.toString();
    }

    public void  addSentence( Sentence sentence ){
        sentences.add(sentence);
    }

}
