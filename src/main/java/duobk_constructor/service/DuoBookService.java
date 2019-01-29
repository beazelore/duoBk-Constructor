package duobk_constructor.service;

import duobk_constructor.model.DuoBook;
import duobk_constructor.repository.DuoBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DuoBookService {
    @Autowired
    DuoBookRepository repository;
    public DuoBook create(String name, String status){
        DuoBook duoBook = new DuoBook();
        duoBook.setName(name);
        duoBook.setStatus(status);
        return repository.save(duoBook);
    }
    public Iterable<DuoBook> getAll(){
        return repository.findAll();
    }
    public DuoBook findById(Integer id){
        return repository.findById(id).get();
    }
    public void save(DuoBook duoBook){
        repository.save(duoBook);
    }
}
