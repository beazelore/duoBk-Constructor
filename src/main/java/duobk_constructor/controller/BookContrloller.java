package duobk_constructor.controller;

import duobk_constructor.model.DuoBook;
import duobk_constructor.model.HistoryItem;
import duobk_constructor.model.Task;
import duobk_constructor.service.DuoBookService;
import duobk_constructor.service.EntryService;
import duobk_constructor.service.HistoryItemService;
import duobk_constructor.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/books")
public class BookContrloller {
    @Autowired
    DuoBookService bookService;
    @Autowired
    TaskService taskService;
    @Autowired
    EntryService entryService;
    @Autowired
    HistoryItemService historyService;
    @RequestMapping(value = "/create",method = RequestMethod.POST, consumes = "text/plain")
    public void createBook(@RequestBody String name){
        bookService.create(name,"NEW");
        return;
    }

    @GetMapping(path="/getAll")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody Iterable<DuoBook> getAllBooks(){
        return bookService.getAll();
    }

    @GetMapping(value = "/getById")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody DuoBook getById(@RequestParam(value = "id",required = true) String id ){
        return bookService.findById(Integer.parseInt(id));
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateBook(@ModelAttribute DuoBook duoBook){
        bookService.save(duoBook);
        return;
    }

    @DeleteMapping(value = "/delete", consumes = "text/plain")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public void updateBook(@RequestBody String bookId){
        DuoBook book = bookService.findById(Integer.parseInt(bookId));
        List<Task> bookTasks= taskService.getAllWithBookId(book.getId());
        for(Task task : bookTasks){
            List<HistoryItem> taskHistory = historyService.getTaskHistory(task.getId());
            for(HistoryItem item : taskHistory)
                historyService.delete(item);
            taskService.delete(task);
            if(task.getEntry1_id() != null)
                entryService.delete(entryService.getEntryById(task.getEntry1_id()));
            if(task.getEntry2_id() != null)
                entryService.delete(entryService.getEntryById(task.getEntry2_id()));
        }
        bookService.delete(book);

    }
}
