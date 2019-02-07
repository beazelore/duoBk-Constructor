package duobk_constructor.service;

import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.Sentence;
import duobk_constructor.logic.book.duo.DuoParagraph;
import duobk_constructor.logic.book.duo.DuoSentence;
import duobk_constructor.model.Task;
import duobk_constructor.repository.TaskRepository;
import duobk_constructor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    public List<Task> getUserTasks(Integer userId){
        return taskRepository.findByUserId(userId);
    }
    public Iterable<Task> getAll(){
        return taskRepository.findAll();
    }
    public List<Task> getAllFree(){
        List<Task> result = taskRepository.findByUserId(null);
        return result;
    }
    public Task create(String name, Integer entryId1, Integer entryId2, Integer bookId, String status){
        Task task = new Task();
        task.setName(name);
        task.setEntry1_id(entryId1);
        task.setEntry2_id(entryId2);
        task.setBookId(bookId);
        task.setStatus(status);
        return taskRepository.save(task);
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
    /*
    * after Dijkstra algorithm, we convert it's result to string that will be saved in
    * "unprocessed" column of Task table in db.
    * */
    public String formUnprocessedAfterPreProcess(ArrayList<DuoParagraph> duoParagraphs){
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
        return stringBuilder.toString();
    }
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
                    .append(paragraph.toString());
            if(fromBook1)
                stringBuilder.append("</p1>");
            else  stringBuilder.append("</p2>");
        }
        return stringBuilder.toString();
    }
    public ResponseEntity<String> unprocessedToHtml(String unprocessed) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unprocessed.getBytes(StandardCharsets.UTF_8));
        //InputSource is = new InputSource();
        //is.setCharacterStream(new StringReader(unprocessed));
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

    public void removeDpFromUnprocessedToBad(String unprocessed, String index, Integer taskId) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(unprocessed.getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        NodeList dpList = doc.getElementsByTagName("dp");
        for(int i =0; i < dpList.getLength();i++){
            Element dp = (Element)dpList.item(i);
            if(dp.getAttribute("index").equals(index)){
                dp.getParentNode().removeChild(dp);
                String newUnprocessed = getStringFromDocument(doc);
                Task task = getTaskById(taskId);
                task.setUnprocessed(newUnprocessed);
                addDpToBad(dp,task);
                save(task);
            }
        }
    }

    private String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    private void addDpToBad(Element dpElement, Task task) throws IOException, SAXException, ParserConfigurationException {
        NodeList paragraphs = dpElement.getChildNodes();
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(task.getBad().getBytes(StandardCharsets.UTF_8));
        Document doc = db.parse(stream);
        Element bad = (Element) doc.getElementsByTagName("bad").item(0);
        for(int i =0 ; i < paragraphs.getLength();i++){
            Element pNode = (Element) doc.importNode(paragraphs.item(i),true);
            pNode.setAttribute("chapter", dpElement.getAttribute("chapter"));
            bad.appendChild(pNode);
        }
        String newBad = getStringFromDocument(doc);
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
            builder.append(p1.getAttribute("index")).append(". ").append(extractTextChildren(p1));
            builder.append("</option>");
        }
        builder.append("!separator!");
        for(int i =0; i < p2List.getLength(); i++){
            Element p2 = (Element) p2List.item(i);
            builder.append("<option");
            builder.append(" value=").append(p2.getAttribute("index")).append(" chapter=").append(p2.getAttribute("chapter")).append('>');
            builder.append(p2.getAttribute("index")).append(". ").append(extractTextChildren(p2));
            builder.append("</option>");
        }
        return new ResponseEntity<>(builder.toString(),HttpStatus.OK);
    }

    public void finishSentProcess(String dp, Task task) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(dp.getBytes(StandardCharsets.UTF_8));
        Document dpDoc = db.parse(stream);
        NodeList dpListProcessed = dpDoc.getElementsByTagName("dp");
        NodeList s1List = dpDoc.getElementsByTagName("s1");
        NodeList s2List = dpDoc.getElementsByTagName("s2");

        // delete from unprocessed
        stream = new ByteArrayInputStream(task.getUnprocessed().getBytes(StandardCharsets.UTF_8));
        Document unprocessedDoc = db.parse(stream);
        NodeList dpList = unprocessedDoc.getElementsByTagName("dp");
        //NodeList p2List = unprocessedDoc.getElementsByTagName("p2");
        for(int i =0; i < dpList.getLength(); i++){
            Element dpEl = (Element) dpList.item(i);
            ArrayList<String> indexes1 = new ArrayList<String>(Arrays.asList(dpEl.getAttribute("indexes1").split(",")));
            //ArrayList<String> indexes2 = new ArrayList<String>(Arrays.asList(dpEl.getAttribute("indexes2").split(",")));
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
                Element s1 = (Element) s1List.item(i);
                if(s1.getAttribute("pIndex").equals(index)){
                    p1.getParentNode().removeChild(p1);
                    break;
                }
            }
        }
        for(int i = 0 ; i < p2List.getLength(); i++){
            Element p2 = (Element) p2List.item(i);
            String index = p2.getAttribute("index");
            for(int q = 0; q < s2List.getLength();q++){
                Element s2 = (Element) s2List.item(i);
                if(s2.getAttribute("pIndex").equals(index)){
                    p2.getParentNode().removeChild(p2);
                    break;
                }
            }
        }
        //modify processed
        stream = new ByteArrayInputStream(task.getProcessed().getBytes(StandardCharsets.UTF_8));
        Document processedDoc = db.parse(stream);
        Element root = (Element) processedDoc.getElementsByTagName("processed").item(0);
        for(int i =0; i < dpListProcessed.getLength(); i++){
            Node newDp = processedDoc.importNode(dpListProcessed.item(i),true);
            root.appendChild(newDp);
        }
        //saving task
        String unprocessed = getStringFromDocument(unprocessedDoc);
        String bad = getStringFromDocument(badDoc);
        String processed = getStringFromDocument(processedDoc);

        task.setUnprocessed(unprocessed);
        task.setBad(bad);
        task.setProcessed(processed);

        taskRepository.save(task);
        return;
    }

    public void processToResult(Task task) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(task.getProcessed().getBytes(StandardCharsets.UTF_8));
        Document processedDoc = db.parse(stream);

        // 1. order s1 and s2 inside ds by indexes
        NodeList dsList = processedDoc.getElementsByTagName("ds");
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
                        elToCompare.getParentNode().insertBefore(newCurrent,elToCompare);
                        currentS1.getParentNode().removeChild(currentS1);
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
                        elToCompare.getParentNode().insertBefore(newCurrent,elToCompare);
                        currentS2.getParentNode().removeChild(currentS2);
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
        NodeList dpList = processedDoc.getElementsByTagName("dp");
        for(int q=0; q< dpList.getLength(); q++){
            Element dp = (Element) dpList.item(q);
            dsList = dp.getElementsByTagName("ds");
            for(int i =1; i < dsList.getLength(); i++){
                int z = 1;
                Element currentDs = (Element) dsList.item(i);
                while (i-z >= 0){
                    Element dsToCompare = (Element) dsList.item(i-z);
                    Integer currentIndex = Integer.parseInt(currentDs.getAttribute("index"));
                    Integer indexToCompare = Integer.parseInt(dsToCompare.getAttribute("index"));
                    if(currentIndex < indexToCompare){
                        Element newCurrent = (Element) currentDs.cloneNode(true);
                        dsToCompare.getParentNode().insertBefore(newCurrent, dsToCompare);
                        currentDs.getParentNode().removeChild(currentDs);
                    }
                    z++;
                }
            }
        }


        // 5. add pIndex attribute to each dp
        dpList = processedDoc.getElementsByTagName("dp");
        for(int i =0; i <  dpList.getLength(); i++){
            Element dp = (Element) dpList.item(i);
            dp.setAttribute("pIndex", ((Element)dp.getElementsByTagName("ds").item(0)).getAttribute("pIndex"));
        }

        // 6. order dp by pIndex
        dpList = processedDoc.getElementsByTagName("dp");
        for(int i =1; i <  dpList.getLength(); i++){
            Element currentDp = (Element) dpList.item(i);
            int z = 1;
            while(i-z >=0){
                Element dpToCompare = (Element) dpList.item(i-z);
                Integer currentIndex = Integer.parseInt(currentDp.getAttribute("pIndex"));
                Integer indexToCompare = Integer.parseInt(dpToCompare.getAttribute("pIndex"));
                if(currentIndex < indexToCompare){
                    Element newCurrent = (Element) currentDp.cloneNode(true);
                    dpToCompare.getParentNode().insertBefore(newCurrent,dpToCompare);
                    currentDp.getParentNode().removeChild(currentDp);
                }
                z++;
            }
        }

        // 7. form result document with chapters;

        stream = new ByteArrayInputStream(task.getResult().getBytes(StandardCharsets.UTF_8));
        Document resultDoc = db.parse(stream);
        Element rootResult = (Element) resultDoc.getElementsByTagName("result").item(0);
        dpList = processedDoc.getElementsByTagName("dp");
        boolean inserted = false;
        for(int i=0; i < dpList.getLength(); i++){
            Element dpEl = (Element) dpList.item(i);
            String chapterIndex = dpEl.getAttribute("chapter");
            NodeList resultChapters = resultDoc.getElementsByTagName("chapter");
            for(int q =0; q < resultChapters.getLength(); i ++){
                Element chapter = (Element) resultChapters.item(q);
                if(chapter.getAttribute("index").equals(chapterIndex)){
                    Node newDp = resultDoc.importNode(dpEl, true);
                    chapter.appendChild(newDp);
                    inserted = true;
                    break;
                }
                inserted = false;
            }
            if(!inserted){
                Element chapter = resultDoc.createElement("chapter");
                chapter.setAttribute("index",chapterIndex);
                Node newDp = resultDoc.importNode(dpEl,true);
                chapter.appendChild(newDp);
                rootResult.appendChild(chapter);
            }
        }

        // 8. result document toString and save task
        task.setResult(getStringFromDocument(resultDoc));
        taskRepository.save(task);

    }
}
