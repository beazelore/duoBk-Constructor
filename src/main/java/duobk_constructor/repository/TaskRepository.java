package duobk_constructor.repository;


import org.springframework.data.repository.CrudRepository;

import duobk_constructor.model.Task;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface TaskRepository extends CrudRepository<Task, Integer> {
    public List<Task> findByUserId(Integer userId);
}