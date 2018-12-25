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
import sun.misc.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Controller
public class HomeController {

    private UserValidation user;
    private Files files;
    private CommentsRepository comments;

    @Autowired
    public HomeController(UserValidation user, Files files, CommentsRepository comments) {
        this.user = user;
        this.files = files;
        this.comments = comments;
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


    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public String fileUpload(HttpServletRequest request,
                             @RequestParam("file") MultipartFile file) throws NoSuchAlgorithmException, IOException {

        User u = user.isLogin(request);
        if (u == null)
            return "redirect:/login";

        String name = file.getOriginalFilename();
        String filetype = file.getContentType();

        File tmp = File.createTempFile("cybersecurity", "file", new File("."));
        try(OutputStream str = new FileOutputStream(tmp)){
            str.write(file.getBytes());
        }

        byte[] digest = null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream str = new FileInputStream(tmp)) {
            digest = md.digest(IOUtils.readFully(str, -1, true));
        }
        StringBuilder hashBuilder = new StringBuilder();
        for (byte b : digest) {
            hashBuilder.append(String.format("%02x", b));
        }
        String hash = hashBuilder.toString();

        String fileSeparator = System.getProperty("file.separator");
        String pathname = "." + fileSeparator + "storage" + fileSeparator + hash;
        File fl = new File(pathname);
        java.nio.file.Files.move(
                Paths.get(tmp.getAbsolutePath()),
                Paths.get(fl.getAbsolutePath()),
                StandardCopyOption.REPLACE_EXISTING);

        StoredFile f = new StoredFile(
                u.getId(),
                pathname,
                name,
                hash.toString(),
                filetype
        );

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

        Path path = Paths.get(".", "storage", hash);
        try(InputStream in = new FileInputStream(path.toString())){
            byte[] buffer = new byte[1024];
            int read = 0;
            while((read = in.read(buffer)) > 0){
                response.getOutputStream().write(buffer, 0, read);
            }
        }
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
