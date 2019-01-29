package duobk_constructor.logic.book_reader;


import duobk_constructor.logic.Language;
import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class EpubBookReader implements BookReader {
    public EpubBookReader() {
    }
    @Override
    public Book read(InputStream inputStream, Language language) throws Exception {
        Book book = new Book(language);
        nl.siegmann.epublib.domain.Book tempBook = initializeEpubLibBook(inputStream);
        String title = tempBook.getTitle();
        book.setTitle(title);
        ArrayList<Chapter> chapters = new ArrayList<>();
            for (int i = 0; i < tempBook.getSpine().size(); i++) {
                EpubSAXParser parser = new EpubSAXParser();
                InputStream inputStreamChapter = tempBook.getSpine().getResource(i).getInputStream();
                Chapter chapter = parser.parse(inputStreamChapter, book);
                if (chapter.getParagraphs().size() > 0)
                    chapters.add(chapter);
            }
        book.setChapters(chapters);
        book.formParListAndSetIndexes();
        return book;
        }
    private nl.siegmann.epublib.domain.Book initializeEpubLibBook(InputStream inputStream) {
        try {
            nl.siegmann.epublib.domain.Book book = (new EpubReader()).readEpub(inputStream);
            return book;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
