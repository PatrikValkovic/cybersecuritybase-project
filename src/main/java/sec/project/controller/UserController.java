package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.User;
import sec.project.repository.UsersRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Controller
public class UserController {

    private UsersRepository repo;

    @Autowired
    public UserController(UsersRepository repo) {
        this.repo = repo;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginForm(HttpServletResponse resp,
                            @RequestParam String name,
                            @RequestParam String password) throws SQLException {
        User u = repo.find(name, password);
        if (u == null)
            return "redirect:/login";

        resp.addCookie(new Cookie("session", Long.toString(u.getId())));
        return "redirect:/";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerForm(@RequestParam String name,
                               @RequestParam String password,
                               @RequestParam String password_repeat) throws SQLException {
        if (!password.equals(password_repeat))
            return "redirect:/register";
        User u = new User(0, name, password);
        repo.insert(u);
        return "redirect:/login";
    }
}
