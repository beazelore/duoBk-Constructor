package duobk_constructor.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * Class that maps all the html files to appropriate address
 * */
@Controller
public class MainController {

    @RequestMapping("/tasks")
    public String tasks(Model model, Authentication authentication) {
        return "tasks.html";
    }

    @RequestMapping("/")
    public String defaultRedirect(){
        return "redirect:/tasks";
    }

    @RequestMapping("/admin/createTask")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createTask(){
        return "create-task.html";
    }

    @RequestMapping("/tasks/preProcess")
    public String pickIndexes(){
        return "pre-process.html";
    }

    @RequestMapping("/admin/createBook")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createBook(){
        return "create-book.html";
    }

    @RequestMapping("/tasks/process")
    public String procss(){
        return  "process.html";
    }

    @RequestMapping("/admin/books")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String allBooks(){
        return "allbooks.html";
    }

    @RequestMapping("/admin/tasks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String allTasks(){
        return "alltasks.html";
    }

    @RequestMapping("/admin/books/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editBook(){
        return "book-edit.html";
    }

    @RequestMapping("/admin/tasks/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editTask(){
        return "task-edit.html";
    }

    @RequestMapping("/tasks/process/sent")
    public String processSent(){
        return  "process-sent.html";
    }

    @RequestMapping("/tasks/submit")
    public String submitTask(){
        return  "submit-task.html";
    }

    @RequestMapping("/admin/tasks/check")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String checkTask(){
        return "check-task.html";
    }

    @RequestMapping("/admin/users")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public String adminUsers(){
        return "allusers.html";
    }
    @RequestMapping("/admin/users/edit")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public String editUser(){
        return "user-edit.html";
    }
}
