package duobk_constructor.logic.book;


import duobk_constructor.logic.Language;
import duobk_constructor.logic.book_reader.Fb2BookReader;
import org.omg.CORBA.INTERNAL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Book {
    private ArrayList<Chapter> chapters;
    private String title;

    public ArrayList<Paragraph> getParagraphs() {
        return paragraphs;
    }

    private ArrayList<Paragraph> paragraphs;

    public Book(Language language) {
        this.language = language;
        chapters = new ArrayList<>();
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public void addChapter(Chapter chapter){
        this.chapters.add(chapter);
    }

    public void formParListAndSetIndexes(){
        paragraphs = new ArrayList<>();
        int indexPar = 0;
        int indexChapter = 0;
        for (Chapter chapter : chapters){
            chapter.setIndex(indexChapter);
            indexChapter++;
            for (Paragraph paragraph : chapter.getParagraphs()){
                paragraphs.add(paragraph);
                if(paragraph.getIndex()==null)
                    paragraph.setIndex(indexPar);
                indexPar++;
                int indexSent = 0;
                for(Sentence sentence : paragraph.getSentences()){
                    sentence.setIndex(indexSent);
                    indexSent++;
                }
            }
        }
    }

    private Language language;
    public Document toDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();

        // create the root element node
        Element bookEl = doc.createElement("book");
        for (Chapter chapter : chapters){
            Element chapterEl = doc.createElement("chapter");
            for (Paragraph paragraph : chapter.getParagraphs()){
                Element pEl = doc.createElement("p");
                for (Sentence sentence : paragraph.getSentences()){
                    Element sEl = doc.createElement("s");
                    Node textNode = doc.createTextNode(sentence.toString());
                    sEl.appendChild(textNode);
                    pEl.appendChild(sEl);
                }
                chapterEl.appendChild(pEl);
            }
            bookEl.appendChild(chapterEl);
        }
        doc.appendChild(bookEl);
        return doc;
    }
    public Book(String XML, Language language) throws Exception {
        Fb2BookReader reader = new Fb2BookReader();
        InputStream stream = new ByteArrayInputStream(XML.getBytes(StandardCharsets.UTF_8));
        Book book = reader.read(stream,language);
        this.chapters = book.chapters;
        this.language = book.language;
        this.paragraphs = book.paragraphs;
        this.title = book.title;
        formParListAndSetIndexes();
    }
    // to create Book from result and duoBook(bd)
    public Book(Document resultDoc, Language languge){
        this.chapters = new ArrayList<>();
        this.paragraphs = new ArrayList<>();
        NodeList chapters = resultDoc.getElementsByTagName("chapter");
        for(int i =0; i < chapters.getLength(); i++){
            Chapter chapter = new Chapter();
            Element chapterEl = (Element) chapters.item(i);
            chapter.setIndex(Integer.parseInt(chapterEl.getAttribute("index")));
            NodeList paragraphs = chapterEl.getElementsByTagName("dp");
            for(int q=0; q< paragraphs.getLength(); q ++){
                Paragraph paragraph = new Paragraph(chapter);
                Element dpEl = (Element) paragraphs.item(q);
                paragraph.setIndex(Integer.parseInt(dpEl.getAttribute("pIndex")));
                NodeList s1List = dpEl.getElementsByTagName("s1");
                for(int z=0; z< s1List.getLength(); z++){
                    Element s1El = (Element) s1List.item(z);
                    Sentence sentence = new Sentence(extractTextChildren(s1El),paragraph);
                    sentence.setIndex(Integer.parseInt(s1El.getAttribute("index")));
                    paragraph.addSentence(sentence);
                }
                chapter.addParagraph(paragraph);
                this.paragraphs.add(paragraph);
            }
            this.chapters.add(chapter);
        }

    }
    private String extractTextChildren(Element parentNode) {
        NodeList childNodes = parentNode.getChildNodes();
        String result = new String();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                result += node.getNodeValue();
            }
        }
        return result;
    }
/*
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Chapter ch : chapters)
            sb.append(ch.toString());

        return  sb.toString();
    }*/
}
