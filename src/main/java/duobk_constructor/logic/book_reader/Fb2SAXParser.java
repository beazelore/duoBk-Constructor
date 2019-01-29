package duobk_constructor.logic.book_reader;

import duobk_constructor.logic.Language;
import duobk_constructor.logic.book.Book;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

public class Fb2SAXParser {
    public Fb2SAXParser() {
    }
    public Book parse(InputStream inputStream, Language language) throws Exception {
        //book.setLanguage(detectLanguage(inputStream));
        Book book = new Book(language);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        Fb2SAXEventHandler handler = new Fb2SAXEventHandler(book);
        xmlReader.setContentHandler(handler);
        xmlReader.parse(new InputSource(inputStream));
        return handler.getBook();
    }
}
