package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.AgencyToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyTokenRepository extends CrudRepository<AgencyToken, Long> {

    @Query("select new uk.gov.cshr.civilservant.domain.AgencyToken(a.id, a.token, a.capacity, a.capacityUsed) " +
            "from AgencyToken a " +
            "left join a.agencyDomains d " +
            "where d.domain = ?1 ")
    List<AgencyToken> findAllByDomain(String domain);

    @Query("select new uk.gov.cshr.civilservant.domain.AgencyToken(a.id, a.token, a.capacity, a.capacityUsed) " +
            "from AgencyToken a " +
            "left join a.agencyDomains d " +
            "where d.domain = ?1 " +
            "and a.token = ?2")
    Optional<AgencyToken> findByDomainAndToken(String domain, String token);

    @Query("select new uk.gov.cshr.civilservant.domain.AgencyToken(a.id, a.token, a.capacity, a.capacityUsed) " +
            "from AgencyToken a " +
            "left join a.agencyDomains d " +
            "left join OrganisationalUnit ou on ou.agencyToken.id = a.id  " +
            "where d.domain = ?1 " +
            "and a.token = ?2 " +
            "and ou.code = ?3")
    Optional<AgencyToken> findByDomainTokenAndCode(String domain, String token, String code);
}
