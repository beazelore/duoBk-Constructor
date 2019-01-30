package duobk_constructor.service;

import duobk_constructor.logic.book.Book;
import duobk_constructor.logic.book.Chapter;
import duobk_constructor.logic.book.Paragraph;
import duobk_constructor.model.Task;
import duobk_constructor.repository.TaskRepository;
import duobk_constructor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    public List<Task> getUserTasks(Integer userId){
        return taskRepository.findByUserId(userId);
    }
    public Iterable<Task> getAll(){
        return taskRepository.findAll();
    }
    public List<Task> getAllFree(){
        List<Task> result = taskRepository.findByUserId(null);
        return result;
    }
    public Task create(String name, Integer entryId1, Integer entryId2, Integer bookId, String status){
        Task task = new Task();
        task.setName(name);
        task.setEntry1_id(entryId1);
        task.setEntry2_id(entryId2);
        task.setBookId(bookId);
        task.setStatus(status);
        return taskRepository.save(task);
    }
    public Task getTaskById(Integer id){
        return taskRepository.findById(id).get();
    }
    public Task setUserId(Integer taskId, Integer userId){
        Task task = getTaskById(taskId);
        task.setUserId(userId);
        return taskRepository.save(task);
    }
    public ResponseEntity<?> formBooksResponse(Book book1, Book book2){
        StringBuilder builder = new StringBuilder();
        for (Chapter chapter : book1.getChapters()){
            for (Paragraph p : chapter.getParagraphs()){
                builder.append("<option");
                builder.append(" value=").append(p.getIndex()).append('>');
                builder.append(p.getIndex()).append(". ").append(p.toString());
                builder.append("</option>");
            }
        }
        builder.append("!separator!");
        for (Chapter chapter : book2.getChapters()){
            for (Paragraph p : chapter.getParagraphs()){
                builder.append("<option");
                builder.append(" value=").append(p.getIndex()).append('>');
                builder.append(p.getIndex()).append(". ").append(p.toString());
                builder.append("</option>");
            }
        }
        return new ResponseEntity<String>(builder.toString(), HttpStatus.OK);
    }
}
