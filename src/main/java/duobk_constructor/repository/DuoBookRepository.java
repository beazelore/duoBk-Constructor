package duobk_constructor.repository;

import duobk_constructor.model.DuoBook;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DuoBookRepository extends CrudRepository<DuoBook, Integer> {
    public List<DuoBook> findByAuthorId(Integer authorId);
}
