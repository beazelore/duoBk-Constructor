package duobk_constructor.service;

import duobk_constructor.logic.book.Book;
import duobk_constructor.model.DuoBook;
import duobk_constructor.model.Entry;
import duobk_constructor.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Service
public class EntryService {
    @Autowired
    EntryRepository entryRepository;
    public Entry create(String author, String title, String lang){
        Entry entry = new Entry();
        entry.setAuthor(author);
        entry.setLanguage(lang);
        entry.setTitle(title);
        return entryRepository.save(entry);
    }
    public Entry create(Book book, String author, String title, String lang) throws ParserConfigurationException, TransformerException {
        Entry entry = new Entry();
        entry.setAuthor(author);
        entry.setLanguage(lang);
        entry.setTitle(title);
        entry.setValue(docToString(book.toDocument()));
        return entryRepository.save(entry);
    }
    public Entry getEntryById(Integer id){
        return entryRepository.findById(id).get();
    }
    public void delete(Entry entry){
        entryRepository.delete(entry);
    }

    public static String docToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
}
