package uk.gov.cshr.civilservant.domain;

import javax.persistence.*;
import java.util.*;

@Entity
public class OrganisationalUnit implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    private OrganisationalUnit parent;

    @OneToMany(mappedBy = "parent")
    private Collection<OrganisationalUnit> subOrgs = Collections.emptySet();

    @Column(name = "payment_methods")
    private String paymentMethods;

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
        this.subOrgs.add(organisationalUnit.clone());
    }

    @Override
    protected OrganisationalUnit clone() {
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setId(this.id);
        organisationalUnit.setParent(this.parent);
        organisationalUnit.setName(this.name);
        organisationalUnit.setSubOrgs(this.subOrgs);
        organisationalUnit.setPaymentMethods(this.getPaymentMethods());

        return organisationalUnit;
    }
}
