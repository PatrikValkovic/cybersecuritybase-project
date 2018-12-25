package sec.project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile extends AbstractPersistable<Long> {

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

}
