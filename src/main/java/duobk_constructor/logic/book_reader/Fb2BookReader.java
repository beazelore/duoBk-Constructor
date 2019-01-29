package duobk_constructor.logic.book_reader;

import duobk_constructor.logic.Language;
import duobk_constructor.logic.book.Book;

import java.io.*;

public class Fb2BookReader implements BookReader {
    public Fb2BookReader() {
    }

    @Override
    public Book read(InputStream inputStream, Language language) throws Exception {
        Fb2SAXParser parser = new Fb2SAXParser();
        Book book = parser.parse(inputStream,language);
        book.formParListAndSetIndexes();
        return book;

    }
}
