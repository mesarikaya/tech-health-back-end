package com.mes.techdebt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * A Attachment.
 */
@Entity
@Table(name = "attachment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class Attachment implements Serializable {

    private static final long serialVersionUID = -8102384537878513091L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] data;

    @ManyToOne
    @JsonIgnoreProperties(value = { "investmentCriticality" }, allowSetters = true)
    private Site site;

    @ManyToOne
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private AssessmentCriteria assessmentCriteria;

    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    private Timestamp createDate;

    @Column(name = "created_by")
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "update_date", updatable = true)
    private Timestamp updateDate;

    @Column(name = "updated_by")
    private String updatedBy;

    public Attachment id(Long id) {
        this.setId(id);
        return this;
    }

    public Attachment fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public Attachment fileType(String fileType) {
        this.setFileType(fileType);
        return this;
    }

    public Attachment fileSize(Long fileSize) {
        this.setFileSize(fileSize);
        return this;
    }

    public Attachment data(byte[] data) {
        this.setData(data);
        return this;
    }

    public Attachment site(Site site) {
        this.setSite(site);
        return this;
    }


    public Attachment assessmentCriteria(AssessmentCriteria assessmentCriteria) {
        this.setAssessmentCriteria(assessmentCriteria);
        return this;
    }

    public Attachment createDate(Timestamp createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public Attachment createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public Attachment updateDate(Timestamp updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public Attachment updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        return id != null && id.equals(((Attachment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
