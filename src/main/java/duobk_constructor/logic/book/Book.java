package duobk_constructor.logic.book;


import duobk_constructor.logic.Language;
import duobk_constructor.logic.book_reader.Fb2BookReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Book {
    private ArrayList<Chapter> chapters;
    private String title;

    public ArrayList<Paragraph> getParagraphs() {
        if (paragraphs != null)
            return paragraphs;
        else{
            formParListAndSetIndexes();
            return paragraphs;
        }
    }

    private ArrayList<Paragraph> paragraphs;

    public Book(Language language) {
        this.language = language;
        chapters = new ArrayList<>();
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public void addChapter(Chapter chapter){
        this.chapters.add(chapter);
    }

    public void formParListAndSetIndexes(){
        paragraphs = new ArrayList<>();
        int indexPar = 0;
        for (Chapter chapter : chapters){
            for (Paragraph paragraph : chapter.getParagraphs()){
                paragraphs.add(paragraph);
                paragraph.setIndex(indexPar);
                indexPar++;
                int indexSent = 0;
                for(Sentence sentence : paragraph.getSentences()){
                    sentence.setIndex(indexSent);
                    indexSent++;
                }
            }
        }
    }

    private Language language;
    public String toXML(){
        StringBuilder builder = new StringBuilder();
        for (Chapter chapter : chapters){
            builder.append("<chapter>");
            for (Paragraph paragraph : chapter.getParagraphs()){
                builder.append("<p>");
                for (Sentence sentence : paragraph.getSentences()){
                    builder.append("<s>").append(sentence.toString()).append("</>");
                }
                builder.append("</p>");
            }
            builder.append("</chapter>");
        }
        return builder.toString();
    }
    public Book(String XML, Language language) throws Exception {
        Fb2BookReader reader = new Fb2BookReader();
        InputStream stream = new ByteArrayInputStream(XML.getBytes(StandardCharsets.UTF_8));
        Book book = reader.read(stream,language);
        this.chapters = book.chapters;
        this.language = book.language;
        this.paragraphs = book.paragraphs;
        this.title = book.title;
    }
/*
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Chapter ch : chapters)
            sb.append(ch.toString());

        return  sb.toString();
    }*/
}
