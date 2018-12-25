package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sec.project.services.DatabaseConnection;

@Controller
public class HomeController {

    @RequestMapping("*")
    public String homepage() {
        return "homepage";
    }

}
