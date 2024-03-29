package duobk_constructor.repository;

import org.springframework.data.repository.CrudRepository;

import duobk_constructor.model.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByMail(String mail);
}