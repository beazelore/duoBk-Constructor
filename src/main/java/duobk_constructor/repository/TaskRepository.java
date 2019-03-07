package duobk_constructor.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import duobk_constructor.model.Task;
import org.springframework.data.repository.query.Param;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface TaskRepository extends JpaRepository<Task, Integer> {
    public List<Task> findByUserId(Integer userId);
    public List<Task> findByBookId(Integer bookId);
    @Query(value = "select task.id, task.name, task.status, user.mail, max(history_item.moment) " +
            "from task left join user on task.user_id=user.id " +
            "left join history_item on task.id=history_item.task_id group by history_item.task_id;", nativeQuery = true)
    public List<Object> getAllForMenu();
    @Query(value = "select task.id, task.name, task.status, max(history_item.moment)" +
            " from task inner join history_item on task.id=history_item.task_id" +
            " where task.status=\"NEW\" and task.user_id is null group by history_item.task_id", nativeQuery = true)
    public List<Object> getUserPool();
    @Query(value = "select task.id, task.name, task.status, max(history_item.moment)" +
            " from task inner join history_item on task.id=history_item.task_id " +
            "where (task.status=\"NEW\" or task.status=\"CHECK_NEEDED\") and task.user_id is null" +
            " group by history_item.task_id",nativeQuery = true)
    public List<Object> getAdminPool();
    @Query(value = "select task.id, task.name, task.status, max(history_item.moment)" +
            " from task inner join user on task.user_id=user.id" +
            " inner join history_item on task.id=history_item.task_id" +
            " where task.user_id=:userId group by history_item.task_id", nativeQuery = true)
    public List<Object> getUserTasks(@Param(value = "userId") Integer userId);
    @Query(value = "select task.id, task.name, task.status, max(history_item.moment)" +
            " from task inner join user on task.user_id=user.id" +
            " inner join history_item on task.id=history_item.task_id" +
            " where user.mail=:mail group by history_item.task_id", nativeQuery = true)
    public List<Object> getUserTasks(@Param(value = "mail") String mail);
}