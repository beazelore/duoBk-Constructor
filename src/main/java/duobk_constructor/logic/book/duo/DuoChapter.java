package duobk_constructor.logic.book.duo;

import java.util.ArrayList;

public class DuoChapter {
    ArrayList<DuoParagraph> paragraphs;

    public ArrayList<DuoParagraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(ArrayList<DuoParagraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public DuoChapter() {
    }

    public DuoChapter(ArrayList<DuoParagraph> paragraphs) {
        this.paragraphs = paragraphs;
    }
}
