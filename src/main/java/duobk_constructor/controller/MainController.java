package duobk_constructor.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Controller
public class MainController {
    private String mess = "if you see it, everything is allright";
@RequestMapping("/tasks")
    public String tasks(Model model, Authentication authentication) {
        model.addAttribute("message", mess);
        model.addAttribute("authentication", authentication);
        return "tasks.html";
    }
    @RequestMapping("/")
    public String defaultRedirect(){
        return "redirect:/tasks";
    }
    @RequestMapping("/index")
    public String index(){
        return "index.html";
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
    public String procss(){return  "process.html";}

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
    public String processSent(){return  "process-sent.html";}
}
