package duobk_constructor.controller;

import duobk_constructor.helpers.IndexesForm;
import duobk_constructor.helpers.UploadForm;
import duobk_constructor.logic.book.Book;
import duobk_constructor.model.DuoBook;
import duobk_constructor.model.Entry;
import duobk_constructor.model.Task;
import duobk_constructor.model.User;
import duobk_constructor.repository.UserRepository;
import duobk_constructor.service.*;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import duobk_constructor.repository.TaskRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Task> getAllTasks(){
        return taskService.getAll();
    }

    @GetMapping(value = "/allWithNoUser")
    public Iterable<Task> getAllFreeTasks(){return  taskService.getAllFree();}

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
    public void processUploadedFiles(@ModelAttribute UploadForm form) throws Exception {
        DuoBook duoBook = duoBookService.findById(form.getBook());
        String taskName = new StringBuilder().append(form.getLanguage1()).append('/').append(form.getLanguage2()).append(duoBook.getName()).toString();
        Book book1 = fileReaderService.read(form.getFiles()[0], form.getLanguage1());
        Entry entry1= entryService.create(book1.toXML(),form.getAuthor1(),form.getTitle1(),form.getLanguage1(), false);
        Entry entry2;
        Book book2;
        if(form.getBookStatus().equals("NEW")){
            book2 = fileReaderService.read(form.getFiles()[1],form.getLanguage2());
            entry2 = entryService.create(book2.toXML(), form.getAuthor2(),form.getTitle2(),form.getLanguage2(), false);
        }
        else{
            entry2 = entryService.createFromBook(duoBook);
        }
        taskService.create(taskName, entry1.getId(),entry2.getId(),form.getBook(),"NEW");
        if(form.getBookStatus().equals("NEW")){
            // here we should change the status of duoBook to FIRST_PROCESS (no other tasks can be added while book has this status)
            duoBook.setStatus("FIRST_PROCESS");
            duoBookService.save(duoBook);
        }

        return;
    }

    @RequestMapping(value = "/create/new/process")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> processAndSave(@RequestBody IndexesForm indexesForm){




        return new ResponseEntity<IndexesForm>(indexesForm, HttpStatus.OK);
    }

}
