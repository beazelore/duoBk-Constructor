package duobk_constructor.controller;

import duobk_constructor.helpers.IndexesForm;
import duobk_constructor.helpers.TaskWithMail;
import duobk_constructor.helpers.UploadForm;
import duobk_constructor.logic.AStar;
import duobk_constructor.logic.Language;
import duobk_constructor.logic.SentenceAStar;
import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.logic.book.Sentence;
import duobk_constructor.logic.book.duo.DuoParagraph;
import duobk_constructor.logic.book.duo.DuoSentence;
import duobk_constructor.model.*;
import duobk_constructor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(path="/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileReaderService fileReaderService;
    @Autowired
    private EntryService entryService;
    @Autowired
    DuoBookService duoBookService;
    @Autowired
    HistoryItemService historyService;

    @GetMapping(path="/all")
    public ArrayList getAllTasks(){
        List<Task> list = new ArrayList<>();
        ArrayList<TaskWithMail> tasksWithMails = new ArrayList<>();
        for(Task task : taskService.getAll()){
            Integer userId = task.getUserId();
            String mail = "NONE";
            if(userId != null)
                mail = userService.getById(userId).getMail();
            tasksWithMails.add(new TaskWithMail(task,mail));
        }
        return tasksWithMails;
    }

    @GetMapping(value = "/allNewWithNoUser")
    public Iterable<Task> getAllFreeTasks(){return  taskService.getAllNewFree();}

    @RequestMapping(value = "/take", method = RequestMethod.POST, consumes = "text/plain")
    public void takeTask(OAuth2Authentication authentication, @RequestBody String id){
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        String email = (String)properties.get("email");
        Integer userId = userService.getUserIdByMail(email);
        taskService.setUserId(Integer.parseInt(id), userId);
        return;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<Task> getUserTasks(OAuth2Authentication authentication) {
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        String email = (String)properties.get("email");
        List<Task> tasks = taskService.getUserTasks(userService.getUserIdByMail(email));
        return tasks;
    }

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void createTask(@ModelAttribute UploadForm form, Principal principal) throws Exception {
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        DuoBook duoBook = duoBookService.findById(form.getBook());
        StringBuilder taskName = new StringBuilder().append(form.getLanguage1()).append('/');
        if(form.getLanguage2().isEmpty() || form.getLanguage2() == null){
            taskName.append("en");
        }
        else{
            taskName.append(form.getLanguage2());
        }
        taskName.append(" ").append(duoBook.getName());
        Book book1;
        Entry entry1;
        Entry entry2;
        Book book2;
        Task task = new Task();
        if(form.getBookStatus().equals("NEW")){
            book1 = fileReaderService.read(form.getFiles()[0], form.getLanguage1());
            entry1= entryService.create(book1.toXML(),form.getAuthor1(),form.getTitle1(),form.getLanguage1(), false);
            book2 = fileReaderService.read(form.getFiles()[1],form.getLanguage2());
            entry2 = entryService.create(book2.toXML(), form.getAuthor2(),form.getTitle2(),form.getLanguage2(), false);
            task.setEntry1_id(entry1.getId());
        }
        else{
            //entry1 = entryService.createFromBook(duoBook);
            book2 = fileReaderService.read(form.getFiles()[0],form.getLanguage1());
            entry2 = entryService.create(book2.toXML(), form.getAuthor1(),form.getTitle1(),form.getLanguage1(), false);
        }
        task.setName(taskName.toString());
        task.setEntry2_id(entry2.getId());
        task.setBookId(form.getBook());
        task.setStatus("NEW");
        taskService.save(task);
        //taskService.create(taskName, entry1.getId(),entry2.getId(),form.getBook(),"NEW");
        if(form.getBookStatus().equals("NEW")){
            // here we should change the status of duoBook to FIRST_PROCESS (no other tasks can be added while book has this status)
            duoBook.setStatus("FIRST_PROCESS");
            duoBookService.save(duoBook);
        }
        // create history Item
        HistoryItem historyItem = new HistoryItem();
        historyItem.setStatusBefore("-");
        historyItem.setStatusAfter("NEW");
        historyItem.setMoment(new Date());
        historyItem.setTaskId(task.getId());
        historyItem.setExplanation("Task has just been created by "+ user.getMail() + ".");
        historyItem.setMessage(form.getMessage());
        historyItem.setUserId(user.getId());
        historyService.save(historyItem);
        return;
    }

    @RequestMapping(value = "/preProcess/do")
    public ResponseEntity<?> processAndSave(@RequestBody IndexesForm indexesForm, Principal principal) throws Exception {
        Task task = taskService.getTaskById(indexesForm.getTaskId());
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        DuoBook duoBook = duoBookService.findById(task.getBookId());
        Entry entry1;
        Entry entry2;
        Book book1;
        Book book2;

        if(duoBook.getStatus().equals("FIRST_PROCESS")){
            entry1 = entryService.getEntryById(task.getEntry1_id());
            book1 = new Book(entry1.getValue(), new Language(entry1.getLanguage()));
            entry2 = entryService.getEntryById(task.getEntry2_id());
            book2 = new Book(entry2.getValue(), new Language(entry2.getLanguage()));
        }
        else{
            book1 = new Book(duoBookService.getDocumentFromValue(duoBook),new Language("en"));
            entry2 = entryService.getEntryById(task.getEntry2_id());
            book2 = new Book(entry2.getValue(), new Language(entry2.getLanguage()));
        }


        AStar aStar = new AStar(book1,book2);
        aStar.Start(indexesForm.getStart1(),indexesForm.getStart2(),indexesForm.getEnd1(), indexesForm.getEnd2());
        ArrayList<DuoParagraph> result = aStar.getResult();
        String unprocessed = taskService.formUnprocessedAfterPreProcess(result);
        task.setUnprocessed(unprocessed);
        task.setProcessed("<processed></processed>");
        task.setBad("<bad></bad>");
        task.setResult("<result></result>");
        task.setStatus("PROCESS");
        taskService.save(task);
        //creating history item
        HistoryItem historyItem = new HistoryItem();
        historyItem.setStatusBefore("NEW");
        historyItem.setStatusAfter("PROCESS");
        historyItem.setExplanation("PRE-PROCESS was done by "+ user.getMail() + ".");
        historyItem.setMoment(new Date());
        historyItem.setUserId(user.getId());
        historyItem.setTaskId(task.getId());
        historyService.save(historyItem);
        return new ResponseEntity<IndexesForm>(indexesForm, HttpStatus.OK);
    }

    @RequestMapping(value = "/preProcess/getEntries")
    public ResponseEntity<?> getAndProcessEntries(@RequestParam(value = "id", required = true) String taskId) throws Exception {
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        DuoBook duoBook = duoBookService.findById(task.getBookId());
        Entry entry1;
        Book book1;
        Entry entry2;
        Book book2;
        if(task.getEntry1_id() != null){
            entry1 = entryService.getEntryById(task.getEntry1_id());
            book1 = new Book(entry1.getValue(), new Language(entry1.getLanguage()));
            entry2 = entryService.getEntryById(task.getEntry2_id());
            book2 = new Book(entry2.getValue(), new Language(entry2.getLanguage()));
        }
        else{
            book1 = new Book(duoBookService.getDocumentFromValue(duoBook),new Language("en"));
            entry2 = entryService.getEntryById(task.getEntry2_id());
            book2 = new Book(entry2.getValue(), new Language(entry2.getLanguage()));
        }
        return taskService.formBooksResponse(book1,book2); //order of arguments very important here
    }
    /*
    * if unprocessed column of task is empty returns true
    * */
    @RequestMapping(value = "/preProcess/checkUnprocessed", consumes = "text/plain")
    public boolean checkUnprocessedEmpty(@RequestBody String taskId){
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        if(task.getStatus().equals("NEW"))
            return true;
        else return false;
    }
    @RequestMapping(value="process/unprocessedToHTML", method = RequestMethod.GET)
    public ResponseEntity<?> greeting(@RequestParam(value="id",required = true) String id) throws ParserConfigurationException, SAXException, IOException {
        Task task = taskService.getTaskById(Integer.parseInt(id));
        if(task.getUnprocessed() == null){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return taskService.unprocessedToHtml(task.getUnprocessed());
    }

    @GetMapping(value = "/getById")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody Task getById(@RequestParam(value = "id",required = true) String id ){
        return taskService.getTaskById(Integer.parseInt(id));
    }

    @RequestMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void update(@ModelAttribute Task task, Principal principal){
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        Task taskFromDb = taskService.getTaskById(task.getId());
        task.setEntry1_id(taskFromDb.getEntry1_id());
        task.setEntry2_id(taskFromDb.getEntry2_id());
        task.setBad(taskFromDb.getBad());
        taskService.save(task);
        // creating history event
        HistoryItem historyItem = new HistoryItem();
        historyItem.setStatusBefore("UNKNOWN");
        historyItem.setStatusAfter(task.getStatus());
        historyItem.setUserId(user.getId());
        historyItem.setTaskId(task.getId());
        historyItem.setMoment(new Date());
        historyItem.setExplanation("Task was edited by user "+ user.getMail() + ".");
        historyService.save(historyItem);
    }

    @RequestMapping(value = "/process/sent/do")
    public ResponseEntity<?> doSentenceProcess(@RequestParam(value = "id",required = true) String taskId
            , @RequestParam(value = "index",required = true) String dpIndex) throws IOException, SAXException, ParserConfigurationException {
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        String unprocessed = task.getUnprocessed();
        String lang1 ="";
        if(task.getEntry1_id() == null)
            lang1 = "en";
        else
            lang1 = entryService.getEntryById(task.getEntry1_id()).getLanguage();
        String lang2 = entryService.getEntryById(task.getEntry2_id()).getLanguage();
        DuoParagraph duoParagraph = taskService.getDuoParagraphFromUnprocessed(unprocessed,dpIndex,lang1,lang2);
        SentenceAStar aStar = new SentenceAStar();
        aStar.doAStar(duoParagraph);
        if(aStar.getResult().size() == 0){
            ArrayList<DuoSentence> result = new ArrayList<>();
            ArrayList<Sentence> sentences1 = new ArrayList<>();
            ArrayList<Sentence> sentences2 = new ArrayList<>();
            for(Paragraph paragraph: duoParagraph.getParagraphs1())
                sentences1.addAll(paragraph.getSentences());
            for(Paragraph paragraph: duoParagraph.getParagraphs2())
                sentences2.addAll(paragraph.getSentences());
            result.add(new DuoSentence(sentences1,sentences2));
            return  taskService.formSentenceResponse(result);
        }
        return taskService.formSentenceResponse(aStar.getResult());
    }

    @RequestMapping(value = "/process/sent/correcting/do")
    public ResponseEntity<?> doSentenceProcessFromCorrecting(@RequestParam(value = "id",required = true) String taskId
            , @RequestBody IndexesForm indexesForm) throws IOException, SAXException, ParserConfigurationException {
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        String bad= task.getBad();
        String lang1 = "en";
        if(task.getEntry1_id() != null)
            lang1 = entryService.getEntryById(task.getEntry1_id()).getLanguage();
        String lang2 = entryService.getEntryById(task.getEntry2_id()).getLanguage();
        ArrayList<String> indexes1 = new ArrayList<>();
        for(Integer index : indexesForm.getStart1())
            indexes1.add(index.toString());
        ArrayList<String> indexes2 = new ArrayList<>();
        for(Integer index : indexesForm.getStart2())
            indexes2.add(index.toString());
        DuoParagraph duoParagraph = taskService.getDuoParagraphFromBad(bad,indexes1,indexes2,lang1,lang2);
        SentenceAStar aStar = new SentenceAStar();
        aStar.doAStar(duoParagraph);
        if(aStar.getResult().size() == 0){
            ArrayList<DuoSentence> result = new ArrayList<>();
            ArrayList<Sentence> sentences1 = new ArrayList<>();
            ArrayList<Sentence> sentences2 = new ArrayList<>();
            for(Paragraph paragraph: duoParagraph.getParagraphs1())
                sentences1.addAll(paragraph.getSentences());
            for(Paragraph paragraph: duoParagraph.getParagraphs2())
                sentences2.addAll(paragraph.getSentences());
            result.add(new DuoSentence(sentences1,sentences2));
            return  taskService.formSentenceResponse(result);
        }
        return taskService.formSentenceResponse(aStar.getResult());
    }

    @RequestMapping(value = "/process/moveToBad")
    public void moveToBad(@RequestParam(value = "id",required = true) String id, @RequestParam (value = "index",required =  true) String index) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        Task task = taskService.getTaskById(Integer.parseInt(id));
        taskService.removeDpFromUnprocessedToBad(task.getUnprocessed(),index,Integer.parseInt(id));
    }

    @RequestMapping(value = "/process/getBadResponse")
    public ResponseEntity<?> getBadResponse(@RequestParam(value = "id",required = true) String taskId) throws IOException, SAXException, ParserConfigurationException {
        String bad = taskService.getTaskById(Integer.parseInt(taskId)).getBad();
        return taskService.formBadResponse(bad);
    }

    @RequestMapping(value = "/process/sent/finish", consumes = "text/plain")
    public void finishSentProcess(@RequestParam (value = "id", required = true) String taskId, @RequestBody String dp) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        taskService.finishSentProcess(dp,taskService.getTaskById(Integer.parseInt(taskId)));
        return;
    }

    @RequestMapping(value = "/process/finish")
    public void finishProcess(@RequestParam (value = "id", required = true) String taskId) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        taskService.processToResult(taskService.getTaskById(Integer.parseInt(taskId)));
    }

    @GetMapping(value = "/getResult")
    public String getResult(@RequestParam (value = "id",required = true) String taskId){
        return taskService.getTaskById(Integer.parseInt(taskId)).getResult();
    }

    @RequestMapping(value = "/process/submit", consumes = "text/plain")
    public void submitTask(@RequestParam (value = "id", required = true) String taskId, @RequestBody String param, Principal principal){
        String[] params = param.split("!message!"); // that's how i form that param in js. !message! as a separator
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        task.setStatus("CHECK_NEEDED");
        task.setResult(params[0]);
        task.setUserId(null);
        taskService.save(task);
        //creating HistoryItem
        HistoryItem historyItem = new HistoryItem();
        historyItem.setTaskId(task.getId());
        historyItem.setUserId(user.getId());
        historyItem.setStatusBefore("PROCESS");
        historyItem.setStatusAfter("CHECK_NEEDED");
        historyItem.setMoment(new Date());
        historyItem.setExplanation("Task was submited by user "+ user.getMail());
        historyItem.setMessage(params[1]);
        historyService.save(historyItem);
/*        DuoBook book =duoBookService.findById(task.getBookId());
        if(book.getStatus().equals("FIRST_PROCESS")){
            book.setBook(task.getResult());
            book.setStatus("PROCESS");
            duoBookService.save(book);
        }
        else{

        }*/
    }
    @RequestMapping(value = "/integrateIntoBook")
    public String integrateTask(@RequestParam (value = "id",required = true) String taskId) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, TransformerException {
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        DuoBook duoBook = duoBookService.findById(task.getBookId());
        if(duoBook.getStatus().equals("FIRST_PROCESS"))
            return taskService.refactorFirstProcessedBook(task.getResult(),entryService.getEntryById(task.getEntry2_id()).getLanguage());
        return taskService.integrateTask(task,duoBook,entryService.getEntryById(task.getEntry2_id()).getLanguage());
    }

    @RequestMapping(value = "/updateBookValue", consumes = "text/plain")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateBookValue(@RequestParam (value = "id",required = true) String taskId, @RequestBody String resultWithMessage, Principal principal){
        String[] resultAndMessage = resultWithMessage.split("!message!");
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        DuoBook duoBook = duoBookService.findById(task.getBookId());
        duoBook.setBook(resultAndMessage[0]);
        if(duoBook.getStatus().equals("FIRST_PROCESS"))
            duoBook.setStatus("PROCESS");
        task.setStatus("DONE");
        taskService.save(task);
        duoBookService.save(duoBook);
        //creating history item
        HistoryItem historyItem = new HistoryItem();
        historyItem.setTaskId(task.getId());
        historyItem.setUserId(user.getId());
        historyItem.setMessage(resultAndMessage[1]);
        historyItem.setExplanation("Task was checked and accepted by " + user.getMail() + ".");
        historyItem.setMoment(new Date());
        historyItem.setStatusBefore("CHECK_NEEDED");
        historyItem.setStatusAfter("DONE");
        historyService.save(historyItem);
    }

    @DeleteMapping(value = "/delete", consumes = "text/plain")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteTask(@RequestBody String taskId){
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        List<HistoryItem> taskHistory = historyService.getTaskHistory(task.getId());
        for(HistoryItem item : taskHistory)
            historyService.delete(item);
        taskService.delete(task);
        if(task.getEntry1_id() != null){
            Entry entry1 = entryService.getEntryById(task.getEntry1_id());
            entryService.delete(entry1);
        }
        if(task.getEntry2_id() != null){
            Entry entry2 = entryService.getEntryById(task.getEntry2_id());
            entryService.delete(entry2);
        }
    }
}
