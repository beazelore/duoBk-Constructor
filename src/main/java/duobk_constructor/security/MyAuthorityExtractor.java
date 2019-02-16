package duobk_constructor.security;

import duobk_constructor.model.User;
import duobk_constructor.repository.UserRepository;
import duobk_constructor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class MyAuthorityExtractor implements AuthoritiesExtractor {
    @Value("${duobk.superadmins}")
    private String[] superAdmins;
    @Autowired
    UserService userService;
    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        String mail = (String) map.get("email");

        User user = userService.findByMail(mail);
        if(user == null){
            user = new User();
            user.setMail(mail);
            user.setUserType("ROLE_USER");
            for(int i=0; i < superAdmins.length;i++){
                if(mail.equals(superAdmins[i]))
                    user.setUserType("ROLE_USER,ROLE_ADMIN,ROLE_SUPERADMIN");
            }
            userService.save(user);
        }
        String authority = user.getUserType();
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        String[] stringAuthorities = user.getUserType().split(",");
        for(int i =0; i< stringAuthorities.length; i++)
            authorities.add(new MyGrantedAuthority(stringAuthorities[i]));
        return authorities;
    }
}
