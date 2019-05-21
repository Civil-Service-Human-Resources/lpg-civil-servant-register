package uk.gov.cshr.civilservant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.domain.PurchaseOrder;
import uk.gov.cshr.civilservant.repository.PurchaseOrderRepository;

@RestController
@RequestMapping("/purchaseOrders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @GetMapping("/{code}")
    public ResponseEntity<PurchaseOrder> get(@PathVariable String code) {
        return purchaseOrderRepository.findFirstByCodeEquals(code)
                .map(po -> ResponseEntity.ok(po))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
