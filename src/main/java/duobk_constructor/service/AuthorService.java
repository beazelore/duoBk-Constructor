package duobk_constructor.service;

import duobk_constructor.model.Author;
import duobk_constructor.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AuthorService {
    @Autowired
    AuthorRepository repository;
    public Author getById(String id){
        return repository.findById(Integer.parseInt(id)).get();
    }
    public Author create(String name, String biography, String birthDate, String deathDate, MultipartFile imageFile) throws ParseException, IOException {
        Author author = new Author();
        author.setName(name);
        author.setBiography(biography);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date dateBirth = formatter.parse(birthDate);
        Date dateDeath = formatter.parse(deathDate);
        author.setBirthDate(dateBirth);
        author.setDeathDate(dateDeath);
        author.setImage(imageFile.getBytes());
        return repository.save(author);
    }
    public Author update(String id,String name, String biography, String birthDate, String deathDate, MultipartFile imageFile) throws ParseException, IOException {
        Author author = getById(id);
        author.setName(name);
        author.setBiography(biography);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date dateBirth = formatter.parse(birthDate);
        Date dateDeath = formatter.parse(deathDate);
        author.setBirthDate(dateBirth);
        author.setDeathDate(dateDeath);
        if(imageFile != null)
            author.setImage(imageFile.getBytes());
        return repository.save(author);
    }
    public void delete(String id){
        Author author = repository.findById(Integer.parseInt(id)).get();
        repository.delete(author);
    }
    public Iterable<Author> getAll(){return repository.findAll();}
    public List<Object> getAllForMenu(){return repository.getAllForMenu();}
    public List<Object> getMenuItems(){return repository.getMenuItems();}
    public String getAuthorBiography(String authorId){return repository.getAuthorBiography(authorId);}
}
