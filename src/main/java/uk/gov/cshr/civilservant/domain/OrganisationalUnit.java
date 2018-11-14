package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configurable
@Entity
public class OrganisationalUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String code;

    @Column(unique = true, nullable = false, length = 20)
    private String abbreviation;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private OrganisationalUnit parent;

    @OneToMany(mappedBy = "parent")
    @JsonBackReference
    private Collection<OrganisationalUnit> subOrgs = Collections.emptySet();

    @Column(name = "payment_methods")
    private String paymentMethods;

    public OrganisationalUnit(OrganisationalUnit organisationalUnit) {
        this.id = organisationalUnit.getId();
        this.code = organisationalUnit.getCode();
        this.name = organisationalUnit.getName();
        this.parent = organisationalUnit.getParent();
        this.subOrgs = organisationalUnit.getSubOrgs();
        this.setPaymentMethods(organisationalUnit.getPaymentMethods());
    }

    public OrganisationalUnit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganisationalUnit getParent() {
        return parent;
    }

    public void setParent(OrganisationalUnit parent) {
        this.parent = parent;
    }

    public Collection<OrganisationalUnit> getSubOrgs() {
        return Collections.unmodifiableCollection(subOrgs);
    }

    public void setSubOrgs(Collection<OrganisationalUnit> subOrgs) {
        this.subOrgs = Collections.unmodifiableCollection(subOrgs);
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

    public void addtoSubOrgs(OrganisationalUnit organisationalUnit) {
        this.subOrgs.add(new OrganisationalUnit(organisationalUnit));
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    public boolean hasSubOrgs() {
        return !getSubOrgs().isEmpty();
    }

}
