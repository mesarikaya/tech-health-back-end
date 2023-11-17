package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvestmentCriticalityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        InvestmentCriticality investmentCriticality1 = new InvestmentCriticality();
        investmentCriticality1.setId(1L);

        InvestmentCriticality investmentCriticality2 = new InvestmentCriticality();
        investmentCriticality2.setId(investmentCriticality1.getId());
        assertThat(investmentCriticality1).isEqualTo(investmentCriticality2);

        investmentCriticality2.setId(2L);
        assertThat(investmentCriticality1).isNotEqualTo(investmentCriticality2);

        investmentCriticality1.setId(null);
        assertThat(investmentCriticality1).isNotEqualTo(investmentCriticality2);
    }
}