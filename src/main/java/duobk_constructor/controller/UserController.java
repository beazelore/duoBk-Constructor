package duobk_constructor.controller;

import duobk_constructor.model.User;
import duobk_constructor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    UserService userService;
    /**
     * Returns name of currently authenticated user
     * */
    @RequestMapping(value = "/currentName",method = RequestMethod.GET)
    public String user(OAuth2Authentication authentication) {
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        return (String)properties.get("name");
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
