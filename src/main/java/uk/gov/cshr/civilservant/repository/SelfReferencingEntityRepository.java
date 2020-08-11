package uk.gov.cshr.civilservant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.access.prepost.PreAuthorize;
import uk.gov.cshr.civilservant.domain.SelfReferencingEntity;

@NoRepositoryBean
public interface SelfReferencingEntityRepository<T extends SelfReferencingEntity>
    extends JpaRepository<T, Long> {
  List<T> findAllByOrderByNameAsc();

  @Override
  <S extends T> S save(S entity);

  @Override
  @PreAuthorize("isAuthenticated()")
  void deleteById(Long aLong);
}
