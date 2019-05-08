package uk.gov.cshr.civilservant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.PurchaseOrder;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findFirstByCodeEquals(String code);
}
