package duobk_constructor.service;

import duobk_constructor.model.User;
import duobk_constructor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User findByMail(String mail){
        return userRepository.findByMail(mail);
    }

    public Integer getUserIdByMail(String mail){
        return userRepository.findByMail(mail).getId();
    }
    public User getById(Integer id){return userRepository.findById(id).get();}
    public Iterable<User> getAll(){return  userRepository.findAll();}

}
