package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        RecommendationStatus recommendationStatus1 = new RecommendationStatus();
        recommendationStatus1.setId(1L);

        RecommendationStatus recommendationStatus2 = new RecommendationStatus();
        recommendationStatus2.setId(recommendationStatus1.getId());
        assertThat(recommendationStatus1).isEqualTo(recommendationStatus2);

        recommendationStatus2.setId(2L);
        assertThat(recommendationStatus1).isNotEqualTo(recommendationStatus2);

        recommendationStatus1.setId(null);
        assertThat(recommendationStatus1).isNotEqualTo(recommendationStatus2);
    }
}