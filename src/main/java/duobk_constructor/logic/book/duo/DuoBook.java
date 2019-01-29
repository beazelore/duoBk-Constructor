package duobk_constructor.logic.book.duo;

import java.util.ArrayList;

public class DuoBook {
    private ArrayList<DuoChapter> chapters;

    public ArrayList<DuoChapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<DuoChapter> chapters) {
        this.chapters = chapters;
    }

    public DuoBook() {
    }

    public DuoBook(ArrayList<DuoChapter> chapters) {
        this.chapters = chapters;
    }
}
