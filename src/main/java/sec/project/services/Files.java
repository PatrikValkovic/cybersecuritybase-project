package sec.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sec.project.domain.StoredFile;
import sec.project.repository.StoredFileRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Files {

    private StoredFileRepository files;

    @Autowired
    public Files(StoredFileRepository files) {
        this.files = files;
    }

    public StoredFile findByHash(String hash){
        return this.files.findAll()
                .stream()
                .filter(f -> f.getHash().equals(hash))
                .findFirst()
                .orElse(null);
    }

    public List<StoredFile> findForUser(long userId){
        return this.files.findAll()
                .stream()
                .filter(f -> f.getUsersId() == userId)
                .collect(Collectors.toList());
    }

    public void save(StoredFile f){
        this.files.save(f);
    }

    public void delete(StoredFile f){
        this.files.delete(f);
    }

}
