package sec.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sec.project.domain.User;
import sec.project.repository.UsersRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Service
public class UserValidation {

    UsersRepository users;

    @Autowired
    public UserValidation(UsersRepository users) {
        this.users = users;
    }

    public User isLogin(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(!cookie.getName().equals("session"))
                continue;
            
            try {
                long result = Long.parseLong(cookie.getValue());
                return users.find(result);
            }
            catch (NumberFormatException | SQLException ignored) {
                return null;
            }
        }
        return null;
    }
}
