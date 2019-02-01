package duobk_constructor.logic.book;

import java.util.ArrayList;

public class Chapter {
    private ArrayList<Paragraph> paragraphs;
    private Book book;
    private  Integer index;


    public Chapter(Book book) {
        paragraphs = new ArrayList<>();
        this.book = book;
    }


    public Book getBook() {
        return book;
    }

    public ArrayList<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void addParagraph(Paragraph paragraph){
        paragraphs.add(paragraph);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Paragraph p : paragraphs){
            sb.append(p.toString() + "\n");
        }

        return sb.toString();
    }
}
