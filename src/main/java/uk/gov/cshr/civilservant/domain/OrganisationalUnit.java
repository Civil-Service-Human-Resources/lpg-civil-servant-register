package uk.gov.cshr.civilservant.domain;

import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configurable
@Entity
public class OrganisationalUnit extends SelfReferencingEntity<OrganisationalUnit> {
    @Column(unique = true, nullable = false, length = 10)
    private String code;

    @Column(unique = true, nullable = false, length = 20)
    private String abbreviation;

    @Column(name = "payment_methods")
    private String paymentMethods;

    public OrganisationalUnit(OrganisationalUnit organisationalUnit) {
        this.id = organisationalUnit.getId();
        this.code = organisationalUnit.getCode();
        this.name = organisationalUnit.getName();
        this.parent = organisationalUnit.getParent();
        this.children = organisationalUnit.getChildren();
        this.abbreviation = organisationalUnit.getAbbreviation();
        this.setPaymentMethods(organisationalUnit.getPaymentMethods());
    }

    public OrganisationalUnit() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public OrganisationalUnit getParent() {
        return parent;
    }

    @Override
    public void setParent(OrganisationalUnit parent) {
        this.parent = parent;
    }

    @Override
    public List<OrganisationalUnit> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void setChildren(List<OrganisationalUnit> children) {
        this.children = Collections.unmodifiableList(children);
    }

    public List<String> getPaymentMethods() {
        if (null == paymentMethods || paymentMethods.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(paymentMethods.split(",")));
    }

    public void setPaymentMethods(List<String> paymentMethods) {
        this.paymentMethods = String.join(",", paymentMethods);
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public boolean hasParent() {
        return getParent() != null;
    }

    @Override
    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }
}
