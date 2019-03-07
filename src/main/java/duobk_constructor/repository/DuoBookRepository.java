package duobk_constructor.repository;

import duobk_constructor.model.DuoBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DuoBookRepository extends JpaRepository<DuoBook, Integer> {
    public List<DuoBook> findByAuthorId(Integer authorId);
    @Query(value = "select id, name, status from duobook",nativeQuery = true)
    public List<Object> getAllBooksForMenu();
}
