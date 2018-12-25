package sec.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
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

}
