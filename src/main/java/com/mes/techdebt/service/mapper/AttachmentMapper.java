package com.mes.techdebt.service.mapper;

import com.cargill.techdebt.domain.*;
import com.cargill.techdebt.service.dto.*;
import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.Attachment;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
import com.mes.techdebt.service.dto.AttachmentDTO;
import com.mes.techdebt.service.dto.SiteDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Attachment} and its DTO {@link AttachmentDTO}.
 */
@Mapper(componentModel = "spring", uses=DateMapper.class)
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {
    @Mapping(target = "site", source = "site", qualifiedByName = "siteId")
    @Mapping(target = "assessmentCriteria", source = "assessmentCriteria", qualifiedByName = "assessmentCriteriaId")
    AttachmentDTO toDto(Attachment s);

    @Named("siteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SiteDTO toDtoSiteId(Site site);

    @Named("assessmentCriteriaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssessmentCriteriaDTO toDtoAssessmentCriteriaId(AssessmentCriteria assessmentCriteria);
}
