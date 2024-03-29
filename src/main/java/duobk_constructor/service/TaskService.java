package duobk_constructor.service;

import duobk_constructor.Tools;
import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.Sentence;
import duobk_constructor.logic.book.duo.DuoParagraph;
import duobk_constructor.logic.book.duo.DuoSentence;
import duobk_constructor.model.DuoBook;
import duobk_constructor.model.Task;
import duobk_constructor.repository.TaskRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    public List<Object> getUserTasks(Integer userId){
        return taskRepository.getUserTasks(userId);
    }
    public List<Object> getUserTasks(String mail){
        return taskRepository.getUserTasks(mail);
    }
    public Iterable<Task> getAll(){
        return taskRepository.findAll();
    }
    public List<Object> getAllForMenu(){return taskRepository.getAllForMenu();}
    public List<Object> getUserPool(){return taskRepository.getUserPool();}
    public List<Object> getAdminPool(){
        return taskRepository.getAdminPool();
    }
    public Task create(String name, Integer entryId1, Integer entryId2, Integer bookId, String status, String unprocessed1, String unprocessed2){
        Task task = new Task();
        task.setName(name);
        task.setEntry1_id(entryId1);
        task.setEntry2_id(entryId2);
        task.setBookId(bookId);
        task.setStatus(status);
        task.setResult("<result></result>");
        task.setBad("<bad></bad>");
        return taskRepository.save(task);
    }
    public void delete(Task task){
        taskRepository.delete(task);
    }
    public Task save(Task task){
            return taskRepository.save(task);
    }
    public Task getTaskById(Integer id){
        return taskRepository.findById(id).get();
    }
    public Task setUserId(Integer taskId, Integer userId){
        Task task = getTaskById(taskId);
        task.setUserId(userId);
        return taskRepository.save(task);
    }
    public List<Task> getAllWithBookId(Integer bookId){
        return taskRepository.findByBookId(bookId);
    }
    public Integer getTaskOwnerID(String taskId){
        return taskRepository.getTaskOwnerID(taskId);
    }
    /**
     * Returns book in form of unprocessed_1/2 column of Task table
     * */
    public String formUnprocessedFromBook(Book book) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();

        // create the root element node
        Element rootEl = doc.createElement("unprocessed");
        for (Chapter chapter : book.getChapters()){
            //Element chapterEl = doc.createElement("chapter");
            for (Paragraph paragraph : chapter.getParagraphs()){
                Element pEl = doc.createElement("p");
                pEl.setAttribute("chapter", chapter.getIndex().toString());
                pEl.setAttribute("index", paragraph.getIndex().toString());
                Node textNode = doc.createTextNode(paragraph.toString());
                pEl.appendChild(textNode);
                rootEl.appendChild(pEl);
            }
        }
        doc.appendChild(rootEl);
        return docToString(doc);
    }
    /**
     * Returns HTML to be inserted inside of pre-precess.html selects.
     * So it forms options for two selects separated with "!separator"
     * */
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
    public String formUnprocessedForPreProcess(String unprocessed_1, String unprocessed_2) throws IOException, SAXException, ParserConfigurationException {
        StringBuilder builder = new StringBuilder();

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unprocessed_1.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList pList = doc.getElementsByTagName("p");
        for(int i =0 ; i< pList.getLength(); i++){
            Element pEl = (Element) pList.item(i);
            builder.append("<option");
            builder.append(" value=").append(pEl.getAttribute("index"))
                    .append(" chapter=").append(pEl.getAttribute("chapter")).append('>');
            builder.append(pEl.getAttribute("index")).append(". ").append(extractTextChildren(pEl));
            builder.append("</option>");
        }

        builder.append("!separator!");
        stream = new ByteArrayInputStream(unprocessed_2.getBytes(StandardCharsets.UTF_8));
        doc = db.parse(stream);

        pList = doc.getElementsByTagName("p");
        for(int i =0 ; i< pList.getLength(); i++){
            Element pEl = (Element) pList.item(i);
            builder.append("<option");
            builder.append(" value=").append(pEl.getAttribute("index"))
                    .append(" chapter=").append(pEl.getAttribute("chapter")).append('>');
            builder.append(pEl.getAttribute("index")).append(". ").append(extractTextChildren(pEl));
            builder.append("</option>");
        }
        return builder.toString();
    }
    /*
    * after Dijkstra algorithm, we convert it's result to string that will be saved in
    * "unprocessed" column of Task table in db.
    * */
    public String formUnprocessedAfterPreProcess(ArrayList<DuoParagraph> duoParagraphs) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<root>");
        for(int i=0; i< duoParagraphs.size(); i++){
            DuoParagraph paragraph = duoParagraphs.get(i);
            stringBuilder.append("<dp index=\"").append(i).append("\" indexes1=\"").append(getParagraphsIndexesCSV(paragraph.getParagraphs1())).append("\" ")
                    .append("indexes2=\"").append(getParagraphsIndexesCSV(paragraph.getParagraphs2())).append("\" ")
                    .append("chapter=\"").append(paragraph.getParagraphs1().get(0).getChapter().getIndex()).append("\">")
                    .append(getParagraphsUnprocessedString(paragraph.getParagraphs1(),true))
                    .append(getParagraphsUnprocessedString(paragraph.getParagraphs2(),false))
                    .append("</dp>");
        }
        stringBuilder.append("</root>");
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        return prettyFormatXml(doc);
    }
    // just helper to form string of paragraph indexes separated with comas;
    private String getParagraphsIndexesCSV(ArrayList<Paragraph> paragraphs){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0; i < paragraphs.size(); i ++){
            if(i == 0)
                stringBuilder.append(paragraphs.get(i).getIndex());
            else
                stringBuilder.append(',').append(paragraphs.get(i).getIndex());
        }
        return stringBuilder.toString();
    }
    /*
     * <p1 id="">....</><p1 id="">....</> <p2 id="">....</p2>
     *  that's how paraphs are represent in "unprocessed" column of Task table in database
     */
    private String getParagraphsUnprocessedString(ArrayList<Paragraph> paragraphs, boolean fromBook1){
        StringBuilder stringBuilder = new StringBuilder();
        for(Paragraph paragraph : paragraphs){
            if(fromBook1)
                stringBuilder.append("<p1 ");
            else stringBuilder.append("<p2 ");
            stringBuilder.append("index=\"").append(paragraph.getIndex()).append("\">")
                    .append(StringEscapeUtils.escapeXml11(paragraph.toString()));
            if(fromBook1)
                stringBuilder.append("</p1>");
            else  stringBuilder.append("</p2>");
        }
        return stringBuilder.toString();
    }
    public ResponseEntity<String> unprocessedToHtml(String unprocessed) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unprocessed.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList dpList = doc.getElementsByTagName("dp");


        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dpList.getLength(); i++){
            Element paragraph = (Element)dpList.item(i);
            String dpIndex = paragraph.getAttribute("index");
            builder.append("<div class=\"row connection\" id=\"row-connection"+dpIndex+"\">").append("<div class=\"col-sm\">")
                    .append("<select multiple class=\"form-control first\">");
            NodeList p1List = paragraph.getElementsByTagName("p1");
            NodeList p2List = paragraph.getElementsByTagName("p2");
            for(int q=0; q<p1List.getLength();q++){
                Element p1 = (Element) p1List.item(q);
                String p1Index = p1.getAttribute("index");
                builder.append("<option value=\"").append(p1Index).append("\">").append(p1Index).append(".  ").append(extractTextChildren(p1)).append("</option>");
            }
            builder.append("</select>").append("</div>").append("<div class=\"col-sm-1 vertical-center\">" +
                    "            <div class=\"btn-group-vertical\">" +
                    "                <button type=\"button\" class=\"btn btn-success\" id=\"").append(dpIndex).append("\" chapter=\"").append(paragraph.getAttribute("chapter")).append("\">Good</button>" +
                    "                <button type=\"button\" class=\"btn btn-warning\" id=\"").append(dpIndex).append("\" chapter=\"").append(paragraph.getAttribute("chapter")).append("\">Bad</button>" +
                    "            </div>" +
                    "        </div>").append("<div class=\"col-sm\">").append("<select multiple class=\"form-control second\">");
            for(int q=0; q<p2List.getLength();q++){
                Element p2 = (Element) p2List.item(q);
                String p2Index = p2.getAttribute("index");
                builder.append("<option value=\"").append(p2Index).append("\">").append(p2.getAttribute("index")).append(".  ").append(extractTextChildren(p2)).append("</option>");
            }
            builder.append("</select>").append("</div>").append("</div>");
        }
        return new ResponseEntity<String>(builder.toString(), HttpStatus.OK);
    }

    public DuoParagraph getDuoParagraphFromUnprocessed(String unprocessed, String dpIndex, String lang1, String lang2) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unprocessed.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList dpList = doc.getElementsByTagName("dp");
        for(int i =0; i < dpList.getLength();i++){
            Element dp = (Element)dpList.item(i);
            if(dp.getAttribute("index").equals(dpIndex)){
                ArrayList<Paragraph> paragraphs1 = new ArrayList<>();
                ArrayList<Paragraph> paragraphs2 = new ArrayList<>();
                NodeList p1List = dp.getElementsByTagName("p1");
                NodeList p2List = dp.getElementsByTagName("p2");
                for(int q=0; q<p1List.getLength();q++){
                    String content = extractTextChildren((Element) p1List.item(q));
                    Paragraph paragraph = new Paragraph(content,lang1);
                    String index = ((Element)p1List.item(q)).getAttribute("index");
                    paragraph.setIndex(Integer.parseInt(index));
                    paragraphs1.add(paragraph);
                }
                for(int q=0; q<p2List.getLength();q++){
                    String content = extractTextChildren((Element) p2List.item(q));
                    Paragraph paragraph = new Paragraph(content,lang2);
                    String index = ((Element)p2List.item(q)).getAttribute("index");
                    paragraph.setIndex(Integer.parseInt(index));
                    paragraphs2.add(paragraph);
                }
                return new DuoParagraph(paragraphs1,paragraphs2);
            }
        }
        return null;
    }
    public DuoParagraph getDuoParagraphFromUnprocessed(String unproc1, String unproc2, ArrayList<String> indexes1, ArrayList<String> indexes2, String lang1, String lang2) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unproc1.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList pList = doc.getElementsByTagName("p");
        ArrayList<Paragraph> paragraphs1 = new ArrayList<>();
        ArrayList<Paragraph> paragraphs2 = new ArrayList<>();
        //NodeList p1List = doc.getElementsByTagName("p1");
        //NodeList p2List = doc.getElementsByTagName("p2");
        for (int q = 0; q < pList.getLength(); q++) {
            Element p = (Element) pList.item(q);
            if (indexes1.contains(p.getAttribute("index"))) {
                String content = extractTextChildren(p);
                Paragraph paragraph = new Paragraph(content, lang1);
                paragraph.setIndex(Integer.parseInt(p.getAttribute("index")));
                paragraphs1.add(paragraph);
            }
        }
        stream = new ByteArrayInputStream(unproc2.getBytes(StandardCharsets.UTF_8));
        doc = db.parse(stream);
        pList = doc.getElementsByTagName("p");
        for (int q = 0; q < pList.getLength(); q++) {
            Element p = (Element) pList.item(q);
            if (indexes2.contains(p.getAttribute("index"))) {
                String content = extractTextChildren(p);
                Paragraph paragraph = new Paragraph(content, lang2);
                paragraph.setIndex(Integer.parseInt(p.getAttribute("index")));
                paragraphs2.add(paragraph);
            }
        }
        return new DuoParagraph(paragraphs1,paragraphs2);
    }
    public DuoParagraph getDuoParagraphFromBad(String bad, ArrayList<String> indexes1, ArrayList<String> indexes2, String lang1, String lang2) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(bad.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList dpList = doc.getElementsByTagName("dp");
        ArrayList<Paragraph> paragraphs1 = new ArrayList<>();
        ArrayList<Paragraph> paragraphs2 = new ArrayList<>();
        NodeList p1List = doc.getElementsByTagName("p1");
        NodeList p2List = doc.getElementsByTagName("p2");
            for (int q = 0; q < p1List.getLength(); q++) {
                Element p1 = (Element) p1List.item(q);
                if (indexes1.contains(p1.getAttribute("index"))) {
                    String content = extractTextChildren(p1);
                    Paragraph paragraph = new Paragraph(content, lang1);
                    paragraph.setIndex(Integer.parseInt(p1.getAttribute("index")));
                    paragraphs1.add(paragraph);
                }
            }
            for (int q = 0; q < p2List.getLength(); q++) {
                Element p2 = (Element) p2List.item(q);
                if (indexes2.contains(p2.getAttribute("index"))) {
                    String content = extractTextChildren(p2);
                    Paragraph paragraph = new Paragraph(content, lang2);
                    paragraph.setIndex(Integer.parseInt(p2.getAttribute("index")));
                    paragraphs2.add(paragraph);
                }
            }
        return new DuoParagraph(paragraphs1,paragraphs2);
    }

    public ResponseEntity<?> formSentenceResponse(ArrayList<DuoSentence> result){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < result.size(); i++){
            DuoSentence sentence = result.get(i);
            builder.append("<div class=\"row connection\">").append("<div class=\"col-sm\">")
                    .append("<select multiple class=\"form-control first\">");
            for(Sentence s : sentence.getSentences1())
                builder.append("<option value=\"").append(s.getIndexInDuo()).append("\" pIndex=\"").append(s.getParagraph().getIndex())
                        .append("\">").append(s.getIndexInDuo()).append(".  ").append(s.toString()).append("</option>");
            builder.append("</select>").append("</div>").append("<div class=\"col-sm-1 vertical-center\">" +
                    "            <div class=\"btn-group-vertical\">" +
                    "                <button type=\"button\" class=\"btn btn-success\" id=\"").append(i).append("\">Good</button>" +
                    "                <button type=\"button\" class=\"btn btn-warning\" id=\"").append(i).append("\">Bad</button>" +
                    "            </div>" +
                    "        </div>").append("<div class=\"col-sm\">").append("<select multiple class=\"form-control second\">");
            for(Sentence s : sentence.getSentences2())
                builder.append("<option value=\"").append(s.getIndexInDuo()).append("\" pIndex=\"").append(s.getParagraph().getIndex())
                        .append("\">").append(s.getIndexInDuo()).append(".  ").append(s.toString()).append("</option>");
            builder.append("</select>").append("</div>").append("</div>");
        }
        return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
    }

    public void removeDpFromUnprocessedToBad(String unprocessed, String index, Integer taskId) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unprocessed.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList dpList = doc.getElementsByTagName("dp");
        for(int i =0; i < dpList.getLength();i++){
            Element dp = (Element)dpList.item(i);
            if(dp.getAttribute("index").equals(index)){
                dp.getParentNode().removeChild(dp);
                String newUnprocessed = prettyFormatXml(doc);
                Task task = getTaskById(taskId);
                task.setUnprocessed(newUnprocessed);
                addDpToBad(dp,task);
                save(task);
            }
        }
    }

    private void addDpToBad(Element dpElement, Task task) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        NodeList paragraphs1 = dpElement.getElementsByTagName("p1");
        NodeList paragraphs2 = dpElement.getElementsByTagName("p2");
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(task.getBad().getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        Element bad = (Element) doc.getElementsByTagName("bad").item(0);
        for(int i =0 ; i < paragraphs1.getLength();i++){
            Element pNode = (Element) doc.importNode(paragraphs1.item(i),true);
            pNode.setAttribute("chapter", dpElement.getAttribute("chapter"));
            bad.appendChild(pNode);
        }
        for(int i =0 ; i < paragraphs2.getLength();i++){
            Element pNode = (Element) doc.importNode(paragraphs2.item(i),true);
            pNode.setAttribute("chapter", dpElement.getAttribute("chapter"));
            bad.appendChild(pNode);
        }
        String newBad = prettyFormatXml(doc);
        task.setBad(newBad);
    }

    public ResponseEntity<?> formBadResponse(String bad) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(bad.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList p1List = doc.getElementsByTagName("p1");
        NodeList p2List = doc.getElementsByTagName("p2");
        StringBuilder builder = new StringBuilder();
        for(int i =0; i < p1List.getLength(); i++){
            Element p1 = (Element) p1List.item(i);
            builder.append("<option");
            builder.append(" value=").append(p1.getAttribute("index")).append(" chapter=").append(p1.getAttribute("chapter")).append('>');
            builder.append(p1.getAttribute("index")).append(".  ").append(extractTextChildren(p1));
            builder.append("</option>");
        }
        builder.append("!separator!");
        for(int i =0; i < p2List.getLength(); i++){
            Element p2 = (Element) p2List.item(i);
            builder.append("<option");
            builder.append(" value=").append(p2.getAttribute("index")).append(" chapter=").append(p2.getAttribute("chapter")).append('>');
            builder.append(p2.getAttribute("index")).append(".  ").append(extractTextChildren(p2));
            builder.append("</option>");
        }
        return new ResponseEntity<>(builder.toString(),HttpStatus.OK);
    }

    public void finishSentProcess(String dpString, Task task) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(dpString.getBytes(StandardCharsets.UTF_8));
        Document dpDoc = db.parse(stream);
        stream = new ByteArrayInputStream(task.getResult().getBytes(StandardCharsets.UTF_8));
        Document resultDoc = db.parse(stream);
        Element dp = (Element) dpDoc.getElementsByTagName("dp").item(0);


        // 1. order s1 and s2 inside ds by indexes
        NodeList dsList = dpDoc.getElementsByTagName("ds");
        for(int i =0; i < dsList.getLength(); i++){
            NodeList s1List = ((Element)dsList.item(i)).getElementsByTagName("s1");
            NodeList s2List = ((Element)dsList.item(i)).getElementsByTagName("s2");
            for (int q=1; q < s1List.getLength(); q++){
                int z = 1;
                Element currentS1 = (Element)  s1List.item(q);
                while (q-z >=0){
                    Element elToCompare = (Element) s1List.item(q-z);
                    Integer indexCurrent = Integer.parseInt(currentS1.getAttribute("index"));
                    Integer indexToCompare = Integer.parseInt(elToCompare.getAttribute("index"));
                    if(indexCurrent< indexToCompare){
                        Element newCurrent = (Element) currentS1.cloneNode(true);
                        currentS1.getParentNode().removeChild(currentS1);
                        elToCompare.getParentNode().insertBefore(newCurrent,elToCompare);
                        currentS1 = newCurrent;
                    }
                    z++;
                }
            }
            for (int q=0; q < s2List.getLength(); q++){
                int z = 1;
                Element currentS2 = (Element)  s2List.item(q);
                while (q-z >=0){
                    Element elToCompare = (Element) s2List.item(q-z);
                    Integer indexCurrent = Integer.parseInt(currentS2.getAttribute("index"));
                    Integer indexToCompare = Integer.parseInt(elToCompare.getAttribute("index"));
                    if(indexCurrent< indexToCompare){
                        Element newCurrent = (Element) currentS2.cloneNode(true);
                        currentS2.getParentNode().removeChild(currentS2);
                        elToCompare.getParentNode().insertBefore(newCurrent,elToCompare);
                        currentS2 = newCurrent;
                    }
                    z++;
                }
            }
            // 2.add index attribute to each <ds>
            Element firstS1 = (Element) ((Element)dsList.item(i)).getElementsByTagName("s1").item(0);
            ((Element)dsList.item(i)).setAttribute("index", firstS1.getAttribute("index") );
            // 4. add pIndex attribute to each ds
            ((Element)dsList.item(i)).setAttribute("pIndex", firstS1.getAttribute("pIndex") );

        }
        // 3. order ds inside of dp by index
        for(int i =1; i < dsList.getLength(); i++){
            int z = 1;
            Element currentDs = (Element) dsList.item(i);
            while (i-z >= 0){
                Element dsToCompare = (Element) dsList.item(i-z);
                Integer currentIndex = Integer.parseInt(currentDs.getAttribute("index"));
                Integer indexToCompare = Integer.parseInt(dsToCompare.getAttribute("index"));
                if(currentIndex < indexToCompare){
                    Element newCurrent = (Element) currentDs.cloneNode(true);
                    dpDoc.importNode(newCurrent,true);
                    currentDs.getParentNode().removeChild(currentDs);
                    dsToCompare.getParentNode().insertBefore(newCurrent, dsToCompare);
                    currentDs = newCurrent;
                    //currentDs.getParentNode().removeChild(currentDs);
                }
                z++;
            }
        }
        // 3.1 check if dp can be divided into several parts and do it if possible
        ArrayList<Element> newDps = divideDp(dp);
        // 5. add pIndex attribute to each dp
        for(int i =0; i <  newDps.size(); i++){
            Element dpEl = newDps.get(i);
            dpEl.setAttribute("pIndex", ((Element)dpEl.getElementsByTagName("ds").item(0)).getAttribute("pIndex"));
            // 6. inject dp's into result
            integrateIntoResult(dpEl, resultDoc);
        }



        NodeList s1List = dpDoc.getElementsByTagName("s1");
        NodeList s2List = dpDoc.getElementsByTagName("s2");

        // delete from unprocessed
        stream = new ByteArrayInputStream(task.getUnprocessed().getBytes(StandardCharsets.UTF_8));
        Document unprocessedDoc = db.parse(stream);
        NodeList dpListUnproc = unprocessedDoc.getElementsByTagName("dp");
        //NodeList p2List = unprocessedDoc.getElementsByTagName("p2");
        for(int i =0; i < dpListUnproc.getLength(); i++){
            Element dpEl = (Element) dpListUnproc.item(i);
            ArrayList<String> indexes1 = new ArrayList<String>(Arrays.asList(dpEl.getAttribute("indexes1").split(",")));
            for(int q=0; q< s1List.getLength(); q++){
                Element s1 = (Element) s1List.item(q);
                if(indexes1.contains(s1.getAttribute("pIndex"))){
                    dpEl.getParentNode().removeChild(dpEl);
                    break;
                }
            }

        }

        // delete from bad
        stream = new ByteArrayInputStream(task.getBad().getBytes(StandardCharsets.UTF_8));
        Document badDoc = db.parse(stream);
        NodeList p1List = badDoc.getElementsByTagName("p1");
        NodeList p2List = badDoc.getElementsByTagName("p2");
        for(int i = 0 ; i < p1List.getLength(); i++){
            Element p1 = (Element) p1List.item(i);
            String index = p1.getAttribute("index");
            for(int q = 0; q < s1List.getLength();q++){
                Element s1 = (Element) s1List.item(q);
                if(s1.getAttribute("pIndex").equals(index)){
                    p1.getParentNode().removeChild(p1);
                    i--;
                    break;
                }
            }
        }
        for(int i = 0 ; i < p2List.getLength(); i++){
            Element p2 = (Element) p2List.item(i);
            String index = p2.getAttribute("index");
            for(int q = 0; q < s2List.getLength();q++){
                Element s2 = (Element) s2List.item(q);
                if(s2.getAttribute("pIndex").equals(index)){
                    p2.getParentNode().removeChild(p2);
                    i--;
                    break;
                }
            }
        }
        //delete from unprocessed_1
        stream = new ByteArrayInputStream(task.getUnprocessed1().getBytes(StandardCharsets.UTF_8));
        Document unprocessed1Doc = db.parse(stream);
        NodeList pList = unprocessed1Doc.getElementsByTagName("p");
        for(int i=0; i < pList.getLength(); i++){
            Element pEl = (Element) pList.item(i);
            for(int q = 0; q< s1List.getLength();q++){
                Element s1El = (Element) s1List.item(q);
                if(pEl.getAttribute("index").equals(s1El.getAttribute("pIndex"))){
                    pEl.getParentNode().removeChild(pEl);
                    i--;
                    break;
                }
            }
        }
        //delete from unprocessed_2
        stream = new ByteArrayInputStream(task.getUnprocessed2().getBytes(StandardCharsets.UTF_8));
        Document unprocessed2Doc = db.parse(stream);
        pList =  unprocessed2Doc.getElementsByTagName("p");
        for(int i=0; i < pList.getLength(); i++){
            Element pEl = (Element) pList.item(i);
            for(int q = 0; q< s2List.getLength();q++){
                Element s2El = (Element) s2List.item(q);
                if(pEl.getAttribute("index").equals(s2El.getAttribute("pIndex"))){
                    pEl.getParentNode().removeChild(pEl);
                    i--;
                    break;
                }
            }
        }
        //modify processed
        //stream = new ByteArrayInputStream(task.getProcessed().getBytes(StandardCharsets.UTF_8));
        //Document processedDoc = db.parse(stream);
        //Element root = (Element) processedDoc.getElementsByTagName("processed").item(0);
        //Node newDp = processedDoc.importNode(dp,true);
        //root.appendChild(newDp);
        //saving task
        String unprocessed = prettyFormatXml(unprocessedDoc);
        String bad = prettyFormatXml(badDoc);
        //String processed = prettyFormatXml(processedDoc);
        String result = prettyFormatXml(resultDoc);
        String unprocessed1 = docToString(unprocessed1Doc);
        String unprocessed2 = docToString(unprocessed2Doc);

        task.setUnprocessed(unprocessed);
        task.setBad(bad);
        //task.setProcessed(processed);
        task.setResult(result);
        task.setUnprocessed1(unprocessed1);
        task.setUnprocessed2(unprocessed2);

        taskRepository.save(task);
        return;
    }
    /**
     * Removes paragraph from unprocessed1/2
     * */
    public void deleteFromUnprocessed(Task task, boolean fromFirst, ArrayList<Integer> indexes) throws ParserConfigurationException, IOException, SAXException {
        if(indexes == null || indexes.size() ==0)
            return;
        String unproc ="";
        if(fromFirst)
            unproc = task.getUnprocessed1();
        else
            unproc = task.getUnprocessed2();
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unproc.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList pList = doc.getElementsByTagName("p");
        for(int i=0; i < pList.getLength(); i++){
            Element pEl = (Element)pList.item(i);
            if(indexes.contains(Integer.parseInt(pEl.getAttribute("index")))){
                pEl.getParentNode().removeChild(pEl);
                i--;
            }
        }
        if(fromFirst)
            task.setUnprocessed1(docToString(doc));
        else
            task.setUnprocessed2(docToString(doc));
        save(task);
        return;
    }
    public void integrateIntoResult(Element dpEl, Document resultDoc){
        Element rootEl = (Element)resultDoc.getElementsByTagName("result").item(0);

        // find needed chapter
        NodeList chapterList = rootEl.getElementsByTagName("chapter");
        Element neededChapter = null;
        for(int i =0; i < chapterList.getLength(); i++){
            Element chapterEl = (Element)chapterList.item(i);
            if(chapterEl.getAttribute("index").equals(dpEl.getAttribute("chapter"))){
                neededChapter = chapterEl;
                break;
            }
        }
        if(neededChapter == null){
            neededChapter = resultDoc.createElement("chapter");
            neededChapter.setAttribute("index", dpEl.getAttribute("chapter"));
            Node newDp = resultDoc.importNode(dpEl,true);
            neededChapter.appendChild(newDp);
            rootEl.appendChild(neededChapter);
            return ;
        }
        else{
            NodeList dpList = neededChapter.getElementsByTagName("dp");
            if(dpList.getLength() == 0){
                Node newDp =resultDoc.importNode(dpEl,true);
                neededChapter.appendChild(newDp);
                return;
            }
            else{
                for(int i =0; i < dpList.getLength();i++){
                    Element dpToCompare = (Element) dpList.item(i);
                    if(Integer.parseInt(dpEl.getAttribute("pIndex")) < Integer.parseInt(dpToCompare.getAttribute("pIndex"))){
                        Node newDp = resultDoc.importNode(dpEl, true);
                        dpToCompare.getParentNode().insertBefore(newDp,dpToCompare);
                        return;
                    }
                }
                //we are here only if we didn't add dp to result
                Node newDp = resultDoc.importNode(dpEl, true);
                neededChapter.appendChild(newDp);
                return;
            }
        }
    }

    private ArrayList<Element> divideDp(Element dpEl){
        ArrayList<Element> result = new ArrayList<>();
        NodeList dsList = dpEl.getElementsByTagName("ds");
        for(int i =0; i < dsList.getLength(); i++){
            Element dsEl = (Element) dsList.item(i);
            boolean inserted = false;
            for(Element el : result){
                if(el.getAttribute("pIndex").equals(dsEl.getAttribute("pIndex"))){
                    Element newDs = (Element) dsEl.cloneNode(true);
                    //Element lastDsInDp = (Element) el.getLastChild();
                    //newDs.setAttribute("index",String.valueOf(Integer.parseInt(lastDsInDp.getAttribute("index")) +1));
                    el.appendChild(newDs);
                    inserted = true;
                }
            }
            if(!inserted){
                Element newDp = dpEl.getOwnerDocument().createElement("dp");
                newDp.setAttribute("pIndex", dsEl.getAttribute("pIndex"));
                newDp.setAttribute("chapter", dpEl.getAttribute("chapter"));
                Element newDs = (Element) dsEl.cloneNode(true);
                newDs.setAttribute("index","0");
                newDp.appendChild(newDs);
                result.add(newDp);
            }
        }
        // set s1 indexes to start from 0 inside of dp
        for(Element dp : result){
            NodeList s1List = dp.getElementsByTagName("s1");
            for(int i=0; i< s1List.getLength();i++){
                Element s1El = (Element) s1List.item(i);
                s1El.setAttribute("index", String.valueOf(i));
            }
        }
        return result;
    }

    public String integrateTask(Task task, DuoBook duoBook, String lang) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(task.getResult().getBytes(StandardCharsets.UTF_8));
        Document resultDoc = db.parse(stream);

        stream = new ByteArrayInputStream(duoBook.getBook().getBytes(StandardCharsets.UTF_8));
        Document bookDoc = db.parse(stream);

        NodeList resultDpList = resultDoc.getElementsByTagName("dp");
        NodeList bookS1List = bookDoc.getElementsByTagName("s1");
        for(int i =0; i < resultDpList.getLength(); i++){
            Element dp = (Element) resultDpList.item(i);
            NodeList dsList = dp.getElementsByTagName("ds");
            for(int q = 0; q < dsList.getLength(); q++){
                Element dsEl = (Element) dsList.item(q);
                NodeList s1List = dsEl.getElementsByTagName("s1");
                NodeList s2List = dsEl.getElementsByTagName("s2");
                boolean connected = false;
                for(int z =0; z < s1List.getLength(); z++){
                    Element s1El  = (Element) s1List.item(z);
                    for(int w=0; w < bookS1List.getLength();w++){
                        Element bookS1El = (Element) bookS1List.item(w);
                        if(s1El.getAttribute("index").equals(bookS1El.getAttribute("index")) &&
                        s1El.getAttribute("pIndex").equals(bookS1El.getAttribute("pIndex"))){
                            for(int n=0; n<s2List.getLength();n++){
                                Element newS2 = (Element) bookDoc.importNode(s2List.item(n),true);
                                newS2.setAttribute("lang",lang);
                                bookDoc.renameNode(newS2,null, "s");
                                bookS1El.getParentNode().appendChild(newS2);
                            }
                            connected = true;
                        }
                        if(connected)
                            break;
                    }
                }
            }
        }
        return prettyFormatXml(bookDoc);
    }

    public String refactorFirstProcessedBook(String result, String language) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
        Document resultDoc = db.parse(stream);

        NodeList s2List = resultDoc.getElementsByTagName("s2");
        for(int i =0; i < s2List.getLength();){
            Node sNode = resultDoc.renameNode(s2List.item(i),null,"s");
            Element s = (Element) sNode;
            s.setAttribute("lang",language);
        }

        return prettyFormatXml(resultDoc);
    }

    public static String prettyFormatXml(Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        return xmlString;
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
