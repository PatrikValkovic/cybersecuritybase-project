package sec.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends AbstractPersistable<Long> {


    @Getter
    @Setter
    @ManyToOne
    private StoredFile file;

    @Getter
    @Setter
    private String content;

}
