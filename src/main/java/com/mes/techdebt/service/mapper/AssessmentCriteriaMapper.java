package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.service.dto.CategoryDTO;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link AssessmentCriteria} and its DTO {@link AssessmentCriteriaDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssessmentCriteriaMapper extends EntityMapper<AssessmentCriteriaDTO, AssessmentCriteria> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    AssessmentCriteriaDTO toDto(AssessmentCriteria s);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "techArea", source = "techArea")
    CategoryDTO toDtoCategoryId(Category category);
}
