package duobk_constructor.controller;

import duobk_constructor.helpers.IndexesForm;
import duobk_constructor.helpers.TaskWithInfo;
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
import duobk_constructor.security.MyGrantedAuthority;
import duobk_constructor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.Principal;
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

    @GetMapping(value = "/getById")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody Task getById(@RequestParam(value = "id",required = true) String id ){
        return taskService.getTaskById(Integer.parseInt(id));
    }
    /**
     * Creates all the entries needed for task, creates task itself, creates HistoryItem, saves everything to db.
     * */
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void createTask(@ModelAttribute UploadForm form, Principal principal) throws Exception {
        // get current user
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        // get book
        DuoBook duoBook = duoBookService.getById(form.getBook());
        // form taskName
        StringBuilder taskName = new StringBuilder().append(form.getLanguage1()).append('/');
        if(form.getLanguage2().isEmpty() || form.getLanguage2() == null){
            taskName.append("en");
        }
        else{
            taskName.append(form.getLanguage2());
        }
        taskName.append(" ").append(duoBook.getName());

        //create entries
        Book book1;
        Entry entry1;
        Entry entry2;
        Book book2;
        Task task = new Task();
        if(form.getBookStatus().equals("NEW")){
            book1 = fileReaderService.read(form.getFiles()[0], form.getLanguage1());
            entry1= entryService.create(book1,form.getAuthor1(),form.getTitle1(),form.getLanguage1());
            book2 = fileReaderService.read(form.getFiles()[1],form.getLanguage2());
            entry2 = entryService.create(book2, form.getAuthor2(),form.getTitle2(),form.getLanguage2());
            task.setEntry1_id(entry1.getId());
        }
        else{
            // if only one new entry, create only entry2
            book1 = new Book(duoBookService.getDocumentFromValue(duoBook),new Language("en"));
            book2 = fileReaderService.read(form.getFiles()[0],form.getLanguage1());
            entry2 = entryService.create(book2, form.getAuthor1(),form.getTitle1(),form.getLanguage1());
        }
        task.setName(taskName.toString());
        task.setEntry2_id(entry2.getId());
        task.setBookId(form.getBook());
        task.setStatus("NEW");
        task.setUnprocessed1(taskService.formUnprocessedFromBook(book1));
        task.setUnprocessed2(taskService.formUnprocessedFromBook(book2));
        task.setUnprocessed("<unprocessed></unprocessed>");
        task.setResult("<result></result>");
        task.setBad("<bad></bad>");
        taskService.save(task);
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
    }
    /**
     * Returns list of Objects, where Object is array that consist of values in next order:
     * Id, Name, Status, user mail, last modified
     * */
    @GetMapping(path = "/getAllForMenu")
    public List<Object> getAllForMenu(){return taskService.getAllForMenu();}
    /**
     * Forms pool for users according to their authority.
     * Simple users get all NEW tasks with no current user.
     * Admins get all NEW/DONE tasks with no current user.
     * Also last modified date is attached to each resulting task.
     * @return  list of Objects, where Object is array that consist of values in next order:
     * taks id, task name, task status, task last modified
     * */
    @GetMapping(value = "/getTaskPool")
    public List<Object> getPool(Principal principal){
        // detect if user is an admin
        Authentication auth = ((OAuth2Authentication) principal).getUserAuthentication();
        Object[] authorities = auth.getAuthorities().toArray();
        boolean isAdmin = false;
        for(int i =0; i < authorities.length; i++){
            MyGrantedAuthority authority = (MyGrantedAuthority) authorities[i];
            if(authority.getAuthority().equals("ROLE_ADMIN"))
                isAdmin = true;
        }
        // Pool for not-admins is all tasks with NEW status that has no current user
        // Pool for admins is all NEW/DONE tasks with no current user
        List<Task> tasks;
        if(!isAdmin){
            return taskService.getUserPool();
        }
        else{
            return taskService.getAdminPool();
        }
    }
    /**
     * Sets user_id value for task row in db.
     * */
    @RequestMapping(value = "/take", method = RequestMethod.POST, consumes = "text/plain")
    public void takeTask(OAuth2Authentication authentication, @RequestBody String id){
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        String email = (String)properties.get("email");
        Integer userId = userService.getUserIdByMail(email);
        taskService.setUserId(Integer.parseInt(id), userId);
    }
    /**
     * Returns list of user's tasks like Objects, where Object is array that consist of values in next order:
     * task id, task name, task status, task last modified
     * */
    @GetMapping(value = "/user")
    public List<Object> getUserTasks(OAuth2Authentication authentication) {
        // get user mail
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        String email = (String)properties.get("email");
        return taskService.getUserTasks(email);
    }
    /**
     * Does auto-connecting process, forms unprocessed from auto-connecting result,
     * creates HistoryItem, save everything to db
     * */
    @RequestMapping(value = "/preProcess/do")
    public void doPreProcess(@RequestBody IndexesForm indexesForm, OAuth2Authentication authentication) throws Exception {
        // get task
        Task task = taskService.getTaskById(indexesForm.getTaskId());
        // get duoBook
        DuoBook duoBook = duoBookService.getById(task.getBookId());
        // get entries and create book instances
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
        // connecting process
        AStar aStar = new AStar(book1,book2);
        aStar.Start(indexesForm.getStart1(),indexesForm.getStart2(),indexesForm.getEnd1(), indexesForm.getEnd2());
        ArrayList<DuoParagraph> result = aStar.getResult();
        // create unprocessed column value
        String unprocessed = taskService.formUnprocessedAfterPreProcess(result);
        // set unprocessed to task and default values of other columns, save task to db
        task.setUnprocessed(unprocessed);
        //task.setProcessed("<processed></processed>");
        task.setBad("<bad></bad>");
        //task.setResult("<result></result>");
        task.setStatus("PROCESS");
        taskService.save(task);
        //creating history item
        //HistoryItem historyItem = new HistoryItem();
        //historyItem.setStatusBefore("NEW");
        //historyItem.setStatusAfter("PROCESS");
        //historyItem.setExplanation("PRE-PROCESS was done by "+ user.getMail() + ".");
        //historyItem.setMoment(new Date());
        //historyItem.setUserId(user.getId());
        //historyItem.setTaskId(task.getId());
        //historyService.save(historyItem);
    }
    /**
     * Creates books from entries and returns html code for displaying inside of select element.
     * Look inside TaskService.formBooksResponse(Book, Book) to see how response is formed.
     * */
    @RequestMapping(value = "/preProcess/getEntries")
    public ResponseEntity<?> getEntriesAsHTMLOptions(@RequestParam(value = "id", required = true) String taskId) throws Exception {
        //get task, get duoBook
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        DuoBook duoBook = duoBookService.getById(task.getBookId());
        // get entries, create Books
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
    @GetMapping(value = "/preProcess/getUnprocessedAsHTML")
    public String getUnprocessedValuesAsHTMLOptions(@RequestParam(value = "id",required = true) String taskId) throws ParserConfigurationException, SAXException, IOException {
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        return  taskService.formUnprocessedForPreProcess(task.getUnprocessed1(), task.getUnprocessed2());
    }
    /**
     * If Pre-Process hasn't yet been done on this task(status == NEW), returns true
     * */
    @RequestMapping(value = "/preProcess/checkUnprocessed", consumes = "text/plain")
    public boolean checkIfPreProcessWasNotDone(@RequestBody String taskId){
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        if(task.getStatus().equals("NEW"))
            return true;
        else return false;
    }
    /**
     * Forms HTML code from tasks's unprocessed column for displaying inside of process.html
     * */
    @RequestMapping(value="process/unprocessedToHTML", method = RequestMethod.GET)
    public ResponseEntity<?> greeting(@RequestParam(value="id",required = true) String id) throws ParserConfigurationException, SAXException, IOException {
        Task task = taskService.getTaskById(Integer.parseInt(id));
        if(task.getUnprocessed() == null){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return taskService.unprocessedToHtml(task.getUnprocessed());
    }
    /**
     * Modifies task row in database, creates HistoryItem
     * */
    @RequestMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void update(@ModelAttribute Task task, Principal principal){
        // get user
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        // get, modify and save task
        Task taskFromDb = taskService.getTaskById(task.getId());
        task.setEntry1_id(taskFromDb.getEntry1_id());
        task.setEntry2_id(taskFromDb.getEntry2_id());
        task.setBad(taskFromDb.getBad());
        if(task.getUserId() == -1)
            task.setUserId(null);
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
    /**
     * Does auto-connecting process for sentences of dp element from unprocessed.
     * Forms HTML code from auto-connecting result for displaying inside of process-sent.html
     * */
    @RequestMapping(value = "/process/sent/do")
    public ResponseEntity<?> doSentenceProcess(@RequestParam(value = "id",required = true) String taskId
            , @RequestParam(value = "index",required = true) String dpIndex) throws IOException, SAXException, ParserConfigurationException {
        // get task's unprocessed
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        String unprocessed = task.getUnprocessed();
        // get languages (if there's no entry1_id, it means english entry was used instead as default)
        String lang1;
        if(task.getEntry1_id() == null)
            lang1 = "en";
        else
            lang1 = entryService.getEntryById(task.getEntry1_id()).getLanguage();
        String lang2 = entryService.getEntryById(task.getEntry2_id()).getLanguage();
        // create DuoParagraph from Paragraphs of both books that we are about to process
        DuoParagraph duoParagraph = taskService.getDuoParagraphFromUnprocessed(unprocessed,dpIndex,lang1,lang2);
        // do auto-connecting process
        SentenceAStar aStar = new SentenceAStar();
        aStar.doAStar(duoParagraph);
        // if auto-connecting process failed to connect
        // (so far it happened only when there were < 3 sentences in one of paragraphs)
        // we just connect everything together
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
    /**
     * Does auto-connecting process for sentences from paragraphs in task's bad column.
     * Forms HTML code from auto-connecting result for displaying inside of process-sent.html
     * @param indexesForm indexes of paragraphs to connect;
     * @param fromBad 1 or true if we should find that paragraphs in bad, else if in unprocessed1/2
     * in start1 should be indexes of paragraphs1, in start 2 of paragraphs2.)
     * */
    @RequestMapping(value = "/process/sent/correcting/do")
    public ResponseEntity<?> doSentenceProcessFromCorrecting(@RequestParam(value = "id",required = true) String taskId,
            @RequestParam(value = "fromBad", required = true) String fromBad,
            @RequestBody IndexesForm indexesForm) throws IOException, SAXException, ParserConfigurationException {
        // get task's bad column
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        String bad= task.getBad();
        // get languages (if there's no entry1_id, it means english entry was used instead as default)
        String lang1 = "en";
        if(task.getEntry1_id() != null)
            lang1 = entryService.getEntryById(task.getEntry1_id()).getLanguage();
        String lang2 = entryService.getEntryById(task.getEntry2_id()).getLanguage();
        // create DuoParagraph from paragraphs with given indexes
        ArrayList<String> indexes1 = new ArrayList<>();
        for(Integer index : indexesForm.getStart1())
            indexes1.add(index.toString());
        ArrayList<String> indexes2 = new ArrayList<>();
        for(Integer index : indexesForm.getStart2())
            indexes2.add(index.toString());
        DuoParagraph duoParagraph;
        if(fromBad.equals("1")|| fromBad.equals("true"))
            duoParagraph = taskService.getDuoParagraphFromBad(bad,indexes1,indexes2,lang1,lang2);
        else
            duoParagraph = taskService.getDuoParagraphFromUnprocessed(task.getUnprocessed1(),task.getUnprocessed2(),indexes1,indexes2,lang1,lang2);
        // do auto-connecting process
        SentenceAStar aStar = new SentenceAStar();
        aStar.doAStar(duoParagraph);
        // if auto-connecting process failed to connect
        // (so far it happened only when there were < 3 sentences in one of paragraphs)
        // we just connect everything together
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
    /**
     * Removes dp element from unprocessed column of task
     * , add all p1 and p2 elements of that dp to bad column of task
     * @param  index index of dp element
     * */
    @RequestMapping(value = "/process/moveToBad")
    public void moveToBad(@RequestParam(value = "id",required = true) String id, @RequestParam (value = "index",required =  true) String index) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        Task task = taskService.getTaskById(Integer.parseInt(id));
        taskService.removeDpFromUnprocessedToBad(task.getUnprocessed(),index,Integer.parseInt(id));
    }
    /**
     * Forms HTML code for displaying into selects of "Correcting" tab of process.html
     * Look inside TaskService.formBadResponse(String bad) to see how that code is formed
     * */
    @RequestMapping(value = "/process/getBadResponse")
    public ResponseEntity<?> getBadResponse(@RequestParam(value = "id",required = true) String taskId) throws IOException, SAXException, ParserConfigurationException {
        String bad = taskService.getTaskById(Integer.parseInt(taskId)).getBad();
        return taskService.formBadResponse(bad);
    }
    /**
     * Deletes dp element from unprocessed. Deletes all p1 and p2 of dp from bad.
     * Adds dp to processed, save processed in db.
     * */
    @RequestMapping(value = "/process/sent/finish", consumes = "text/plain")
    public void finishSentProcess(@RequestParam (value = "id", required = true) String taskId, @RequestBody String dp) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        taskService.finishSentProcess(dp,taskService.getTaskById(Integer.parseInt(taskId)));
        return;
    }
    /**
     * Returns result column of task
     * @param taskId ID of task
     * */
    @GetMapping(value = "/getResult")
    public String getResult(@RequestParam (value = "id",required = true) String taskId){
        return taskService.getTaskById(Integer.parseInt(taskId)).getResult();
    }
    /**
     * Updates task's result column
     * @param newResult new value of result column
     * @param taskId ID of task to be updated
     * */
    @PostMapping(value = "/updateResult", consumes = "text/plain")
    public void updateResult(@RequestParam (value = "id",required = true) String taskId, @RequestBody String newResult){
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        task.setResult(newResult);
        taskService.save(task);
    }
    /**
     * Changes task status to CHECK_NEEDED, sets result value, removes task from user, create HistoryItem
     * @param taskId ID of task to submit
     * @param param String in form "result !message! message", where result - new result, message - message for History
     * @param principal user details (it's automatically injected)
     * */
    @RequestMapping(value = "/process/submit", consumes = "text/plain")
    public void submitTask(@RequestParam (value = "id", required = true) String taskId, @RequestBody String param, Principal principal){
        // get current user
        String[] params = param.split("!message!"); // that's how i form that param in js. !message! as a separator
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        // modify task status, result, remove task from user, save changes
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
    }
    /**
     * Integrates Task into DuoBook.
     * If it's Book's FIRST-PROCESS, just save tasks result into book
     * else we merge task's result with book's value
     * @param taskId ID of task
     * */
    @RequestMapping(value = "/integrateIntoBook")
    public String integrateTask(@RequestParam (value = "id",required = true) String taskId) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, TransformerException {
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        DuoBook duoBook = duoBookService.getById(task.getBookId());
        if(duoBook.getStatus().equals("FIRST_PROCESS"))
            return taskService.refactorFirstProcessedBook(task.getResult(),entryService.getEntryById(task.getEntry2_id()).getLanguage());
        return taskService.integrateTask(task,duoBook,entryService.getEntryById(task.getEntry2_id()).getLanguage());
    }
    /**
     * Updates task status to DONE, updates task's result column, removes task's user and clears unprocessed, bad, creates HistoryItem
     * */
    @RequestMapping(value = "/confirmBook", consumes = "text/plain")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void confirmBook(@RequestParam (value = "id",required = true) String taskId, @RequestBody String resultWithMessage, Principal principal){
        // get user
        Map<String, String> details = (Map<String, String>) ((OAuth2Authentication) principal).getUserAuthentication().getDetails();
        User user = userService.getByMail(details.get("email"));
        // get task, get book
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        DuoBook duoBook = duoBookService.getById(task.getBookId());
        // update book's result and status
        String[] resultAndMessage = resultWithMessage.split("!message!");
        duoBook.setBook(resultAndMessage[0]);
        if(duoBook.getStatus().equals("FIRST_PROCESS"))
            duoBook.setStatus("PROCESS");
        // update task status
        task.setStatus("DONE");
        // remove task from user and save
        task.setUserId(null);
        task.setUnprocessed("");
        task.setUnprocessed1("");
        task.setUnprocessed2("");
        task.setBad("");
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
    /**
     * Delete's each HistoryItem related with task, deletes task itself, deletes each task's entry
     * */
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
    /**
     * Remove paragraphs from unprocessed1/2
     * @param indexesForm indexesForm.start1 for unprocessed1 indexes, indexesForm.start2 for unprocessed2 indexes
     * */
    @RequestMapping(value = "/deleteFromUnprocessed")
    public void deleteParagraphFromUnprocessed(@RequestBody IndexesForm indexesForm, @RequestParam(value = "id",required = true) String taskId) throws IOException, SAXException, ParserConfigurationException {
        Task task = taskService.getTaskById(Integer.parseInt(taskId));
        taskService.deleteFromUnprocessed(task,true,indexesForm.getStart1());
        taskService.deleteFromUnprocessed(task,false,indexesForm.getStart2());
    }
    @GetMapping(value = "/checkPermission")
    public ResponseEntity checkPermission(@RequestParam(value = "id", required = true) String taskId, OAuth2Authentication authentication){
        // get current user
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        String email = (String)properties.get("email");
        Integer userID = userService.getByMail(email).getId();
        //get task owner ID
        Integer taskOwnerID = taskService.getTaskOwnerID(taskId);
        if(taskOwnerID.equals(userID))
            return new ResponseEntity(HttpStatus.OK);
        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
}
