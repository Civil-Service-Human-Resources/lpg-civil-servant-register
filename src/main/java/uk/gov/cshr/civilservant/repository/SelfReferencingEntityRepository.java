package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.cshr.civilservant.domain.SelfReferencingEntity;

import java.util.List;

@NoRepositoryBean
public interface SelfReferencingEntityRepository<T extends SelfReferencingEntity> extends JpaRepository<T, Long> {
    List<T> findAllByOrderByNameAsc();
}
