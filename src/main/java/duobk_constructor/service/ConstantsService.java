package duobk_constructor.service;

import duobk_constructor.model.Constants;
import duobk_constructor.repository.ConstantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConstantsService {
    @Autowired
    ConstantsRepository repository;
    public Constants getAllConstants(){
        return repository.findAll().iterator().next();
    }
}
