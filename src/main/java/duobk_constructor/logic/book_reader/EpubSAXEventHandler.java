package duobk_constructor.logic.book_reader;


import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class EpubSAXEventHandler extends DefaultHandler {
    public Chapter getResult(){
        return chapter;
    }
    private Chapter chapter;
    private StringBuilder currentParagraph;
    private StringBuilder currentString;
    private boolean bodyStarted = false;

    public EpubSAXEventHandler(Book book){
        currentString = new StringBuilder();
        currentParagraph = new StringBuilder();
        chapter = new Chapter(book);
        currentParagraph = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.toLowerCase().equals("body"))
            bodyStarted = true;
        if (currentString.toString().trim().isEmpty())
            currentString = new StringBuilder();
        if (!currentString.toString().isEmpty()){
            currentParagraph.append(currentString);
            currentString = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (currentString.toString().isEmpty())
            return;
        if (currentString.toString().trim().isEmpty()){
            currentString.delete(0, currentString.length()-1);
            return;
        }
        switch (localName.toLowerCase()){
            case "p":
            case "h1":
            case "h2":
            case "h3":
            case "h4":
            case "h5":
            case "h6":
            case "li":
                if (currentString.length()!= 0){
                    currentParagraph.append(currentString.toString());
                }
                currentString = new StringBuilder();
                Paragraph paragraph = null;
                paragraph = new Paragraph(currentParagraph.toString(),chapter);
                chapter.addParagraph(paragraph);
                currentParagraph = new StringBuilder();
                return;
            case "b":
            case "em":
            case "i":
            case "a":
            case "strong":
            case "sub":
            case "sup":
            default:
                if (bodyStarted){
                    if (currentString.length()!= 0){
                        currentParagraph.append(currentString);
                    }
                    currentString = new StringBuilder();
                    return;
                }

        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (bodyStarted)
            currentString.append(ch,start,length);
        //Todo: handle all that CDATA stuff in characters()
    }
}