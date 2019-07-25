package uk.gov.cshr.civilservant.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.cshr.civilservant.domain.SelfReferencingEntity;

import java.util.List;

@NoRepositoryBean
public interface SelfReferencingEntityRepository<T extends SelfReferencingEntity> extends JpaRepository<T, Long> {
    List<T> findAllByOrderByNameAsc();

    @Override
    @CacheEvict(value = {"organisationalUnitsTree", "organisationalUnitsFlat"}, allEntries = true)
    <S extends T> S save(S entity);
}
