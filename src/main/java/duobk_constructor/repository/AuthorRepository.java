package duobk_constructor.repository;

import duobk_constructor.model.Author;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends CrudRepository<Author, Integer> {
    @Query(value = "select id, name from author",nativeQuery = true)
    public List<Object> getAllForMenu();
    @Query(value = "select distinct author.id, author.name,author.birth, author.death from ((author inner join duobook on author.id = duobook.author_id) inner join task on duobook.id = task.book_id) where task.status = \"LIVE\";",nativeQuery = true)
    public List<Object> getMenuItems();
    @Query(value = "select author.biography from author where author.id =:authorId", nativeQuery = true)
    public String getAuthorBiography(@Param(value = "authorId") String authorId);
}
