package duobk_constructor.controller;

import duobk_constructor.model.DuoBook;
import duobk_constructor.model.Task;
import duobk_constructor.service.DuoBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/books")
public class BookContrloller {
    @Autowired
    DuoBookService bookService;
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
}
