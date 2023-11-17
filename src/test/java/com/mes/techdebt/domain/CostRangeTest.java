package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CostRangeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        CostRange costRange1 = new CostRange();
        costRange1.setId(1L);

        CostRange costRange2 = new CostRange();
        costRange2.setId(costRange1.getId());
        assertThat(costRange1).isEqualTo(costRange2);

        costRange2.setId(2L);
        assertThat(costRange1).isNotEqualTo(costRange2);

        costRange1.setId(null);
        assertThat(costRange1).isNotEqualTo(costRange2);
    }
}