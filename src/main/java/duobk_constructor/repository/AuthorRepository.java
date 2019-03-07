package duobk_constructor.repository;

import duobk_constructor.model.Author;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends CrudRepository<Author, Integer> {
    @Query(value = "select id, name from author",nativeQuery = true)
    public List<Object> getAllForMenu();
}
