package duobk_constructor.service;

import duobk_constructor.model.DuoBook;
import duobk_constructor.repository.DuoBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DuoBookService {
    @Autowired
    DuoBookRepository repository;

    public DuoBook create(String name, String status,Integer authorId, MultipartFile imageFile) throws IOException {
        DuoBook duoBook = new DuoBook();
        duoBook.setName(name);
        duoBook.setStatus(status);
        duoBook.setBook("<book></book>");
        duoBook.setImage(imageFile.getBytes());
        duoBook.setAuthorId(authorId);
        return repository.save(duoBook);
    }
    public Iterable<DuoBook> getAll(){
        return repository.findAll();
    }
    public List<Object> getAllForMenu(){
        return repository.getAllBooksForMenu();
    }
    public DuoBook getById(Integer id){
        return repository.findById(id).get();
    }
    public void save(DuoBook duoBook){
        repository.save(duoBook);
    }
    public void delete(DuoBook book){
        repository.delete(book);
    }
    public List<DuoBook > getByAuthorId(Integer authorId){
        return repository.findByAuthorId(authorId);
    }
    public Document getDocumentFromValue(DuoBook duoBook) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(duoBook.getBook().replace("&nbsp;"," ").getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        return  doc;
    }
    public List<Object> getMenuItems(){
        return repository.getMenuItems();
    }

}
