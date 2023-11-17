package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CostToFixTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        CostToFix costToFix1 = new CostToFix();
        costToFix1.setId(1L);

        CostToFix costToFix2 = new CostToFix();
        costToFix2.setId(costToFix1.getId());
        assertThat(costToFix1).isEqualTo(costToFix2);

        costToFix2.setId(2L);
        assertThat(costToFix1).isNotEqualTo(costToFix2);

        costToFix1.setId(null);
        assertThat(costToFix1).isNotEqualTo(costToFix2);
    }
}