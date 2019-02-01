package duobk_constructor.service;

import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.duo.DuoParagraph;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        for(DuoParagraph paragraph : duoParagraphs){
            stringBuilder.append("<dp indexes1=\"").append(getParagraphsIndexesCSV(paragraph.getParagraphs1())).append("\" ")
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
            builder.append("<div class=\"row connection\">").append("<div class=\"col-sm\">")
                    .append("<select multiple class=\"form-control first\">");
            NodeList p1List = paragraph.getElementsByTagName("p1");
            NodeList p2List = paragraph.getElementsByTagName("p2");
            for(int q=0; q<p1List.getLength();q++){
                Element p1 = (Element) p1List.item(q);
                builder.append("<option>").append(p1.getAttribute("index")).append(".  ").append(extractTextChildren(p1)).append("</option>");
            }
            builder.append("</select>").append("</div>").append("<div class=\"col-sm-1 vertical-center\">" +
                    "            <div class=\"btn-group-vertical\">" +
                    "                <button type=\"button\" class=\"btn btn-success\" id=\"").append(i).append("\">Good</button>" +
                    "                <button type=\"button\" class=\"btn btn-warning\" id=\"").append(i).append("\">Bad</button>" +
                    "            </div>" +
                    "        </div>").append("<div class=\"col-sm\">").append("<select multiple class=\"form-control second\">");
            for(int q=0; q<p2List.getLength();q++){
                Element p2 = (Element) p2List.item(q);
                builder.append("<option>").append(p2.getAttribute("index")).append(".  ").append(extractTextChildren(p2)).append("</option>");
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
}
