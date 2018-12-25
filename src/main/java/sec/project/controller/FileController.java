package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sec.project.domain.Comment;
import sec.project.domain.StoredFile;
import sec.project.domain.User;
import sec.project.repository.CommentsRepository;
import sec.project.services.Files;
import sec.project.services.UserValidation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;

@Controller
public class FileController {

    private Files files;
    private CommentsRepository comments;
    private UserValidation user;

    @Autowired
    public FileController(Files files, CommentsRepository comments, UserValidation user) {
        this.files = files;
        this.comments = comments;
        this.user = user;
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public String fileUpload(HttpServletRequest request,
                             @RequestParam("file") MultipartFile file) throws NoSuchAlgorithmException, IOException {

        User u = user.isLogin(request);
        if (u == null)
            return "redirect:/login";

        StoredFile f = StoredFile.handleUploadedFile(file);
        f.setUsersId(u.getId());

        files.save(f);

        return "redirect:/home";
    }

    @RequestMapping(value = "/file/{hash}", method = RequestMethod.GET)
    public String showFile(@PathVariable("hash") String hash,
                           Model model){
        StoredFile f = files.findByHash(hash);
        if(f == null)
            return "redirect:/home";

        model.addAttribute("file", f);
        return "file";
    }


    @RequestMapping(value = "/download/{hash}", method = RequestMethod.GET)
    public void getFile(@PathVariable("hash") String hash,
                        HttpServletResponse response) throws IOException {
        StoredFile f = files.findByHash(hash);
        if(f == null)
        {
            response.sendRedirect("/home");
            return;
        }
        response.setContentType(f.getFileType());

        f.copyToStream(response.getOutputStream());
        response.flushBuffer();
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public String addComment(@RequestParam("file") String file,
                             @RequestParam("content") String content){

        StoredFile f = files.findByHash(file);
        if(f == null)
            return "redirect:/home";

        Comment c = new Comment();
        c.setFile(f);
        c.setContent(content);

        comments.save(c);

        return "redirect:/file/" + f.getHash();
    }
}
