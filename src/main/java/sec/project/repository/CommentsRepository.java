package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Comment;
import sec.project.domain.StoredFile;

public interface CommentsRepository extends JpaRepository<Comment, Long> {

}
