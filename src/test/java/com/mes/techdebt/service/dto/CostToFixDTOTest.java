package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CostToFixDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        CostToFixDTO costToFix1 = new CostToFixDTO();
        costToFix1.setId(1L);

        CostToFixDTO costToFix2 = new CostToFixDTO();
        costToFix2.setId(costToFix1.getId());
        assertThat(costToFix1).isEqualTo(costToFix2);

        costToFix2.setId(2L);
        assertThat(costToFix1).isNotEqualTo(costToFix2);

        costToFix1.setId(null);
        assertThat(costToFix1).isNotEqualTo(costToFix2);
    }
}