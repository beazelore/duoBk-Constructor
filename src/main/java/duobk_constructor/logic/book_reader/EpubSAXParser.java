package duobk_constructor.logic.book_reader;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.*;


import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import org.xml.sax.*;

public class EpubSAXParser {
    public EpubSAXParser() {
    }
    public Chapter parse(InputStream inputStream, Book book) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);
        //spf.setFeature("http://xml.org/sax/features/namespaces", false);
        //spf.setFeature("http://xml.org/sax/features/validation", false);
        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        EpubSAXEventHandler handler = new EpubSAXEventHandler(book);
        xmlReader.setContentHandler(handler);
        xmlReader.parse(new InputSource(inputStream));
        return handler.getResult();
    }
}
