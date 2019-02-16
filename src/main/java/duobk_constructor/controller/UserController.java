package duobk_constructor.controller;

import duobk_constructor.model.User;
import duobk_constructor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/current",method = RequestMethod.GET)
    public Principal user(Principal principal) {
        return principal;
    }

    @GetMapping(value = "/getAll")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Iterable<User> getAll(){
        return  userService.getAll();
    }

    @GetMapping(value = "/getById")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public User getUserById(@RequestParam(value = "id", required = true)String id){
        return userService.getById(Integer.parseInt(id));
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public void updateUser(@ModelAttribute User user){
        User userFromBd = userService.getById(user.getId());
        userFromBd.setUserType(user.getUserType());
        userService.save(userFromBd);
    }

}
