package duobk_constructor.controller;

import duobk_constructor.helpers.UploadForm;
import duobk_constructor.model.Author;
import duobk_constructor.model.DuoBook;
import duobk_constructor.service.AuthorService;
import duobk_constructor.service.DuoBookService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(path="/authors")
public class AuthorController {
    @Autowired
    AuthorService authorService;
    @Autowired
    DuoBookService bookService;
    /**
     * Creates Author, save in db.
     * @param  form we utilize UploadForm class to provide all needed info
     *             author1 - for name
     *             title1 - for biography
     *             language1 - for birthDate
     *             language2 - for deathDate
     *             files - for image
     * */
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public void createAuthor(@ModelAttribute UploadForm form) throws IOException, ParseException {
        authorService.create(form.getAuthor1(),form.getTitle1(),form.getLanguage1(),form.getLanguage2(),form.getFiles()[0]);
    }

    @GetMapping(value = "/getAll")
    public Iterable<Author> getAllAuthors(){
        return authorService.getAll();
    }

    @GetMapping(value = "/getAllForMenu")
    public List<Object> getAllForMenu(){return authorService.getAllForMenu();}

    /**
     * Copies book's image bytes to HttpServletResponse OutputStream
     * */
    @GetMapping(value = "/image")
    public void getImage(@RequestParam(value = "id", required = true) String authorId, HttpServletResponse response) throws IOException {
        byte[] image = authorService.getById(authorId).getImage();
        if(image != null && image.length > 0){
            InputStream is = new ByteArrayInputStream(image);
            IOUtils.copy(is, response.getOutputStream());
        }
    }

    @GetMapping(value = "/getById")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody Author getById(@RequestParam(value = "id",required = true) String id ){
        return authorService.getById(id);
    }
    /**
     * Updates author row in db.
     * @param  uploadForm we utilize UploadForm class to provide all needed info
     *             author1 - for name
     *             title1 - for biography
     *             language1 - for birthDate
     *             language2 - for deathDate
     *             files - for image
     * */
    @PostMapping(value = "/update")
    public void updateAuthor(@RequestParam(value = "id",required = true) String authorId, @ModelAttribute UploadForm uploadForm) throws IOException, ParseException {
        authorService.update(authorId,uploadForm.getAuthor1(),uploadForm.getTitle1(),uploadForm.getLanguage1(),uploadForm.getLanguage2(),uploadForm.getFiles()[0]);
    }

    /**
     * Deletes author only if there are no books related with it in database
     * */
    @DeleteMapping(value = "/delete", consumes = "text/plain")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<String> deleteAuthor(@RequestBody String authorId){
        List<DuoBook> authorBooks = bookService.getByAuthorId(Integer.parseInt(authorId));
        if(authorBooks.size()>0)
            return new ResponseEntity<String>("Can't delete author, there are books related with it", HttpStatus.NOT_ACCEPTABLE);
        else {
            authorService.delete(authorId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
