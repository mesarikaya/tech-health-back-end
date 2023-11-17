package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        CategoryDTO category1 = new CategoryDTO();
        category1.setId(1L);

        CategoryDTO category2 = new CategoryDTO();
        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2.setId(2L);
        assertThat(category1).isNotEqualTo(category2);

        category1.setId(null);
        assertThat(category1).isNotEqualTo(category2);
    }
}