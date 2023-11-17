package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TechAreaDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        TechAreaDTO techArea1 = new TechAreaDTO();
        techArea1.setId(1L);

        TechAreaDTO techArea2 = new TechAreaDTO();
        techArea2.setId(techArea1.getId());
        assertThat(techArea1).isEqualTo(techArea2);

        techArea2.setId(2L);
        assertThat(techArea1).isNotEqualTo(techArea2);

        techArea1.setId(null);
        assertThat(techArea1).isNotEqualTo(techArea2);
    }
}