package duobk_constructor.logic.book_reader;

import duobk_constructor.logic.Language;
import duobk_constructor.logic.book.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public class DuobkBookReader implements BookReader {
    public DuobkBookReader() {
    }

    @Override
    public Book read(InputStream inputStream, Language language) {
        return null;
    }
}
