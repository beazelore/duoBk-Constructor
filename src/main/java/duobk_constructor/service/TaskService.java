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
}
