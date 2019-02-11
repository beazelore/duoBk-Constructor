package duobk_constructor.logic.book_reader;

import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URISyntaxException;

public class Fb2SAXEventHandler extends DefaultHandler {
    private Book book;
    private Chapter currentChapter;
    private StringBuilder currentString;
    private StringBuilder currentParagraph;
    private boolean poemMode;
    private boolean wasBodyClosed = false;
    private boolean wasBodyStarted = false;
    public Book getBook() {
        return book;
    }

    public Fb2SAXEventHandler(Book book) {
        this.book = book;
        currentString = new StringBuilder();
        currentParagraph = new StringBuilder();
        currentChapter = new Chapter(book);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (localName.toLowerCase()){
            case "body":
                wasBodyStarted = true;
                break;
            case "section":
            case "chapter":
                if (currentChapter != null)
                    book.addChapter(currentChapter);
                currentChapter = new Chapter(book);
                break;
            case "p":
            case "epigraph":
            case "v":
            case "title":
                currentParagraph = new StringBuilder();
                break;


        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentString.append(ch,start,length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName.toLowerCase()){
            case "body":
                wasBodyClosed = true;
                break;
            case "section":
            case "chapter":
                book.addChapter(currentChapter);
                currentChapter = new Chapter(book);
                break;
            case "p":
            case "title":
            case "epigraph":
                if (currentString.length()!= 0){
                currentParagraph.append(currentString.toString());
                }
                currentString = new StringBuilder();
                Paragraph paragraph = new Paragraph(currentParagraph.toString(),currentChapter);
                currentChapter.addParagraph(paragraph);
                currentParagraph = new StringBuilder();
                break;
                default:
                    if (wasBodyStarted){
                        if (currentString.length()!= 0){
                            currentParagraph.append(currentString);
                        }
                        currentString = new StringBuilder();
                        return;
                    }


        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}

