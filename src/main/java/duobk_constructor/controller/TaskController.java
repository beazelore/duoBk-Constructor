package duobk_constructor.controller;

import duobk_constructor.helpers.IndexesForm;
import duobk_constructor.helpers.UploadForm;
import duobk_constructor.logic.book.Book;
import duobk_constructor.model.Task;
import duobk_constructor.model.User;
import duobk_constructor.repository.UserRepository;
import duobk_constructor.service.FileReaderService;
import duobk_constructor.service.TaskService;
import duobk_constructor.service.UserService;
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
    @GetMapping(path="/all")
    public @ResponseBody Iterable<Task> getAllTasks(){
        return taskService.getAll();
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<Task> getUserTasks(OAuth2Authentication authentication) {
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        String email = (String)properties.get("email");
        List<Task> tasks = taskService.getUserTasks(userService.getUserIdByMail(email));
        return tasks;
    }

    @RequestMapping(value = "/create/uploadMultiFiles",method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> processUploadedFiles(@ModelAttribute UploadForm form) throws Exception {
        Book book1 = fileReaderService.read(form.getFiles()[0],form.getLanguage1());
        Book book2 = fileReaderService.read(form.getFiles()[1],form.getLanguage2());

        return fileReaderService.formBooksResponse(book1,book2);
    }

    @RequestMapping(value = "/create/new/process")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> processAndSave(@RequestBody IndexesForm indexesForm){




        return new ResponseEntity<IndexesForm>(indexesForm, HttpStatus.OK);
    }

}
