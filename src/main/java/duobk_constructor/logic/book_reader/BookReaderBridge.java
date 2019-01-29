package duobk_constructor.logic.book_reader;


import duobk_constructor.logic.Language;
import duobk_constructor.logic.book.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
public class BookReaderBridge {
    public Book read(MultipartFile file, Language language) throws Exception {
        String extension = "";

        int i = file.getOriginalFilename().lastIndexOf('.');
        if (i > 0) {
            extension = file.getOriginalFilename().substring(i+1);
        }

        BookReader reader;
        switch (extension.toLowerCase()){
            case "epub":
                reader = new EpubBookReader();
                break;
            case "fb2":
                reader = new Fb2BookReader();
                break;
            case "duobk":
                reader = new DuobkBookReader();
                break;
                default:
                    throw new Exception("Not correct file format");
        }
        return reader.read(file.getInputStream(), language);
    }
}
