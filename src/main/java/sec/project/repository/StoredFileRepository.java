package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.StoredFile;

import java.util.List;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {

}
