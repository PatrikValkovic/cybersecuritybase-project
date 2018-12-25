package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sec.project.domain.User;
import sec.project.repository.CommentsRepository;
import sec.project.services.Files;
import sec.project.services.UserValidation;

import javax.servlet.http.HttpServletRequest;


@Controller
public class HomeController {

    private UserValidation user;
    private Files files;

    @Autowired
    public HomeController(UserValidation user, Files files, CommentsRepository comments) {
        this.user = user;
        this.files = files;
    }

    @RequestMapping("/home")
    public String homepage(HttpServletRequest request,
                           Model model) {
        User u = user.isLogin(request);
        if (u == null)
            return "redirect:/login";

        model.addAttribute("files", files.findForUser(u.getId()));

        return "homepage";
    }

    @RequestMapping("*")
    public String all(HttpServletRequest request) {
        return "redirect:/home";
    }

}
