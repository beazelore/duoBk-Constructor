package duobk_constructor.security;

import duobk_constructor.model.User;
import duobk_constructor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class MyAuthorityExtractor implements AuthoritiesExtractor {
    @Autowired
    UserRepository userRepository;
    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        String mail = (String) map.get("email");
        User user = userRepository.findByMail(mail);
        String authority = user.getUserType();
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new MyGrantedAuthority(authority));
        return authorities;
    }
}
