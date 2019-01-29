package duobk_constructor.service;

import duobk_constructor.model.Task;
import duobk_constructor.repository.TaskRepository;
import duobk_constructor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;

    public List<Task> getUserTasks(Integer userId){
        return taskRepository.findByUserId(userId);
    }

    public Iterable<Task> getAll(){
        return taskRepository.findAll();
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
}
