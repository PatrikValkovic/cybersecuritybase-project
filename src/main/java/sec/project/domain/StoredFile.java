package sec.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class StoredFile extends AbstractPersistable<Long> {

    public StoredFile(long usersId, String stored, String name, String hash, String fileType) {
        this.usersId = usersId;
        this.stored = stored;
        this.name = name;
        this.hash = hash;
        this.fileType = fileType;
    }

    @Getter
    @Setter
    private long usersId;

    @Getter
    @Setter
    private String stored;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String hash;

    @Getter
    @Setter
    private String fileType;

    @Getter
    @Setter
    @OneToMany(mappedBy = "file")
    List<Comment> comments = new ArrayList<>();

    public void copyToStream(OutputStream str) throws IOException {
        Path path = getPathToFile();
        try(InputStream in = new FileInputStream(path.toString())){
            byte[] buffer = new byte[1024];
            int read = 0;
            while((read = in.read(buffer)) > 0){
                str.write(buffer, 0, read);
            }
        }
    }

    public static StoredFile handleUploadedFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String name = file.getOriginalFilename();
        String filetype = file.getContentType();

        //copy to tmp location
        File tmp = File.createTempFile("cybersecurity", "file", new File("."));
        try(OutputStream str = new FileOutputStream(tmp)){
            str.write(file.getBytes());
        }

        //get hash
        byte[] digest;
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream str = new FileInputStream(tmp)) {
            digest = md.digest(IOUtils.readFully(str, -1, true));
        }
        StringBuilder hashBuilder = new StringBuilder();
        for (byte b : digest) {
            hashBuilder.append(String.format("%02x", b));
        }
        String hash = hashBuilder.toString();

        //copy to proper location
        String fileSeparator = System.getProperty("file.separator");
        String pathname = "." + fileSeparator + "storage" + fileSeparator + hash;
        File fl = new File(pathname);
        java.nio.file.Files.move(
                Paths.get(tmp.getAbsolutePath()),
                Paths.get(fl.getAbsolutePath()),
                StandardCopyOption.REPLACE_EXISTING);

        StoredFile f = new StoredFile(
                0,
                pathname,
                name,
                hash.toString(),
                filetype
        );

        return f;
    }

    private Path getPathToFile() {
        return Paths.get(".", "storage", hash);
    }

}
