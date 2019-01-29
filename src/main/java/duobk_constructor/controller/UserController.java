package duobk_constructor.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public Principal user(Principal principal) {
        return principal;
    }
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public String user() {
        //String userDetails =  (String) authentication.getPrincipal();
        //System.out.println(userDetails);
        return new String("data");
    }
}
