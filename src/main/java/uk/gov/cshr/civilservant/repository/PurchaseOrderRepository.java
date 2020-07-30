package uk.gov.cshr.civilservant.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cshr.civilservant.domain.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Long> {
  Optional<PurchaseOrder> findFirstByCodeEquals(String code);
}
