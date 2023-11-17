package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        Category category1 = new Category();
        category1.setId(1L);

        Category category2 = new Category();
        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2.setId(2L);
        assertThat(category1).isNotEqualTo(category2);

        category1.setId(null);
        assertThat(category1).isNotEqualTo(category2);
    }
}