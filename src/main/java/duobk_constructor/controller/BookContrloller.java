package duobk_constructor.controller;

import duobk_constructor.helpers.UploadForm;
import duobk_constructor.helpers.UploadedDuoBook;
import duobk_constructor.logic.book.BookMenuItem;
import duobk_constructor.model.DuoBook;
import duobk_constructor.model.Entry;
import duobk_constructor.model.HistoryItem;
import duobk_constructor.model.Task;
import duobk_constructor.service.DuoBookService;
import duobk_constructor.service.EntryService;
import duobk_constructor.service.HistoryItemService;
import duobk_constructor.service.TaskService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public void createBook(@ModelAttribute UploadForm form) throws IOException {
        bookService.create(form.getTitle1(),"NEW",Integer.parseInt(form.getAuthor1()), form.getFiles()[0]);
    }

    @GetMapping(path="/getAll")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody Iterable<DuoBook> getAllBooks(){
        return bookService.getAll();
    }
    /**
     * Returns list of Objects, where Object consist of Id, Name, Status
     * */
    @GetMapping(path = "/getAllForMenu")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Object> getAllForMenu(){
        return bookService.getAllForMenu();
    }

    @GetMapping(value = "/getById")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody DuoBook getById(@RequestParam(value = "id",required = true) String id ){
        return bookService.getById(Integer.parseInt(id));
    }
    /**
     * Updates book's database row when it was updated.
     * In book-edit.html, when save button clicked this is called.
     * */
    @PostMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateBook(@ModelAttribute UploadedDuoBook duoBook) throws IOException {
        DuoBook bookFromDb = bookService.getById(duoBook.getId());
        bookFromDb.setName(duoBook.getName());
        bookFromDb.setBook(duoBook.getBook());
        bookFromDb.setStatus(duoBook.getStatus());
        bookFromDb.setAuthorId(duoBook.getAuthorId());
        if(duoBook.getUploadedImage().getSize() != 0){
            bookFromDb.setImage(duoBook.getUploadedImage().getBytes());
        }
        bookService.save(bookFromDb);
        return;
    }
    /**
     * Deletes all tasks related with book, all history items related with that tasks and duobook itself
     * */
    @DeleteMapping(value = "/delete", consumes = "text/plain")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public void deleteBook(@RequestBody String bookId){
        DuoBook book = bookService.getById(Integer.parseInt(bookId));
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
    /**
     * Returns list of items to display in menu. (Title, Author, Image, Languages)
     * */
    @GetMapping(value = "/getBookMenuItems")
    public List<Object> getMenuItems(){
        return bookService.getMenuItems();
    }
    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImageBytes(@RequestParam(value = "id", required = true) String bookId){
        byte[] image = bookService.getById(Integer.parseInt(bookId)).getImage();
        return image;
    }
    /**
     * Returns content of a book with given ID. (Book column of DuoBook in db)
     * */
    @GetMapping(value = "/getContent")
    public String getContent (@RequestParam (value = "id", required = true) String bookId){
        return bookService.getById(Integer.parseInt(bookId)).getBook();
    }
}
