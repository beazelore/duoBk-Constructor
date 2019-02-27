package duobk_constructor.service;

import duobk_constructor.logic.Language;
import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book_reader.BookReaderBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileReaderService {
    public Book read(MultipartFile file, String language) throws Exception {
        BookReaderBridge reader = new BookReaderBridge();
        return reader.read(file, new Language(language));
    }
}
