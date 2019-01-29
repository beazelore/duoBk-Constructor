package duobk_constructor.logic.book_reader;


import duobk_constructor.logic.Language;
import duobk_constructor.logic.book.Book;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface BookReader {
    public Book read(InputStream inputStream, Language language) throws Exception;
}
