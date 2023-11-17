package com.mes.techdebt.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Site.
 */
@Entity
@Table(name = "site")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class Site implements Serializable {
    private static final long serialVersionUID = -6928846099619586218L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mdm_site_id")
    private Long mdmSiteId;

    @Column(name = "mdm_site_name")
    private String mdmSiteName;

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name= "region")
    private String region;

    @Column(name= "enterprise")
    private String enterprise;

    @Column(name= "business_group")
    private String businessGroup;

    @Column(name= "reporting_unit")
    private String reportingUnit;

    @Column(name= "address")
    private String address;

    @Column(name= "country")
    private String country;

    @Column(name= "country_code")
    private String countryCode;

    @Column(name= "city")
    private String city;

    @Column(name= "state")
    private String state;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne
    private InvestmentCriticality investmentCriticality;

    public Site id(Long id) {
        this.setId(id);
        return this;
    }

    public Site name(String name) {
        this.setName(name);
        return this;
    }

    public Site mdmSiteId(Long mdmSiteId) {
        this.setMdmSiteId(mdmSiteId);
        return this;
    }

    public Site mdmSiteName(String mdmSiteName) {
        this.setMdmSiteName(mdmSiteName);
        return this;
    }

    public Site comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public Site isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public Site region(String region) {
        this.setRegion(region);
        return this;
    }

    public Site enterprise(String enterprise) {
        this.setEnterprise(enterprise);
        return this;
    }

    public Site businessGroup(String businessGroup) {
        this.setBusinessGroup(businessGroup);
        return this;
    }

    public Site reportingUnit(String reportingUnit) {
        this.setReportingUnit(reportingUnit);
        return this;
    }

    public Site address(String address) {
        this.setAddress(address);
        return this;
    }

    public Site country(String country) {
        this.setCountry(country);
        return this;
    }

    public Site countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public Site city(String city) {
        this.setCity(city);
        return this;
    }

    public Site state(String state) {
        this.setState(state);
        return this;
    }

    public Site latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public Site longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public Site investmentCriticality(InvestmentCriticality investmentCriticality) {
        this.setInvestmentCriticality(investmentCriticality);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Site)) {
            return false;
        }
        return id != null && id.equals(((Site) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
