package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvestmentCriticalityDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        InvestmentCriticalityDTO investmentCriticality1 = new InvestmentCriticalityDTO();
        investmentCriticality1.setId(1L);

        InvestmentCriticalityDTO investmentCriticality2 = new InvestmentCriticalityDTO();
        investmentCriticality2.setId(investmentCriticality1.getId());
        assertThat(investmentCriticality1).isEqualTo(investmentCriticality2);

        investmentCriticality2.setId(2L);
        assertThat(investmentCriticality1).isNotEqualTo(investmentCriticality2);

        investmentCriticality1.setId(null);
        assertThat(investmentCriticality1).isNotEqualTo(investmentCriticality2);
    }
}