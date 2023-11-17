package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.service.dto.TechAreaDTO;
import com.mes.techdebt.domain.Category;
import com.mes.techdebt.service.dto.CategoryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @Mapping(target = "techArea", source = "techArea", qualifiedByName = "techAreaId")
    CategoryDTO toDto(Category s);

    @Named("techAreaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "domain", source = "domain")
    @Mapping(target = "isActive", source = "isActive")
    TechAreaDTO toDtoTechAreaId(TechArea techArea);
}
