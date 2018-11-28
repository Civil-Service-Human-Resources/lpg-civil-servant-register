package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.Profession;

import java.util.BitSet;
import java.util.List;

@Repository
@RepositoryRestResource
public interface ProfessionRepository extends JpaRepository<Profession, Long> {
    List<Profession> findAllByOrderByNameAsc();
}
