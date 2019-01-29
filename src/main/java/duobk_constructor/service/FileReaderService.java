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
    public ResponseEntity<?> formBooksResponse(Book book1, Book book2){
        StringBuilder builder = new StringBuilder();
        for (Chapter chapter : book1.getChapters()){
            for (Paragraph p : chapter.getParagraphs()){
                builder.append("<option");
                builder.append(" value=").append(p.getIndex()).append('>');
                builder.append(p.getIndex()).append(". ").append(p.toString());
                builder.append("</option>");
            }
        }
        builder.append("!separator!");
        for (Chapter chapter : book2.getChapters()){
            for (Paragraph p : chapter.getParagraphs()){
                builder.append("<option");
                builder.append(" value=").append(p.getIndex()).append('>');
                builder.append(p.getIndex()).append(". ").append(p.toString());
                builder.append("</option>");
            }
        }
        return new ResponseEntity<String>(builder.toString(), HttpStatus.OK);
    }
}
