package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationStatusDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        RecommendationStatusDTO recommendationStatus1 = new RecommendationStatusDTO();
        recommendationStatus1.setId(1L);

        RecommendationStatusDTO recommendationStatus2 = new RecommendationStatusDTO();
        recommendationStatus2.setId(recommendationStatus1.getId());
        assertThat(recommendationStatus1).isEqualTo(recommendationStatus2);

        recommendationStatus2.setId(2L);
        assertThat(recommendationStatus1).isNotEqualTo(recommendationStatus2);

        recommendationStatus1.setId(null);
        assertThat(recommendationStatus1).isNotEqualTo(recommendationStatus2);
    }
}