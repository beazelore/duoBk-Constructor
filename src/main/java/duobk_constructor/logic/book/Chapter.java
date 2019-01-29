package duobk_constructor.logic.book;

import java.util.ArrayList;

public class Chapter {
    public ArrayList<Paragraph> getParagraphs() {
        return paragraphs;
    }

    private ArrayList<Paragraph> paragraphs;

    public Book getBook() {
        return book;
    }

    private Book book;

    public Chapter(Book book) {
        paragraphs = new ArrayList<>();
        this.book = book;
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
