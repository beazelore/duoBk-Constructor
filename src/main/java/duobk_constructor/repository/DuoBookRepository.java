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
    @Query(value = "select duobook.id, duobook.name, author.name as author_name, author.id as author_id, group_concat(entry.language order by entry.id) as languages from (((duobook inner join author on duobook.author_id = author.id) inner join task on task.book_id = duobook.id)) inner join entry on task.entry1_id = entry.id  or task.entry2_id = entry.id  where task.status = \"LIVE\" group by duobook.id;", nativeQuery = true)
    public List<Object> getMenuItems();
}
