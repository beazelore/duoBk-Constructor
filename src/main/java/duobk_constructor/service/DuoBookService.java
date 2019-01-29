package duobk_constructor.service;

import duobk_constructor.model.DuoBook;
import duobk_constructor.repository.DuoBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DuoBookService {
    @Autowired
    DuoBookRepository repository;
    public void create(String name, String status){
        DuoBook duoBook = new DuoBook();
        duoBook.setName(name);
        duoBook.setStatus(status);
        repository.save(duoBook);
    }
    public Iterable<DuoBook> getAll(){
        return repository.findAll();
    }
}
