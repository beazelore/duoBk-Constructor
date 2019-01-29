package duobk_constructor.controller;

import duobk_constructor.model.DuoBook;
import duobk_constructor.model.Task;
import duobk_constructor.service.DuoBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookContrloller {
    @Autowired
    DuoBookService bookService;
    @RequestMapping(value = "/createBook",method = RequestMethod.POST, consumes = "text/plain")
    public void createBook(@RequestBody String name){
        bookService.create(name,"NEW");
        return;
    }

    @GetMapping(path="/getAllBooks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody Iterable<DuoBook> getAllBooks(){
        return bookService.getAll();
    }
}
