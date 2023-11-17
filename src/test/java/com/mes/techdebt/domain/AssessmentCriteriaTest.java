package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentCriteriaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        AssessmentCriteria assessmentCriteria1 = new AssessmentCriteria();
        assessmentCriteria1.setId(1L);

        AssessmentCriteria assessmentCriteria2 = new AssessmentCriteria();
        assessmentCriteria2.setId(assessmentCriteria1.getId());
        assertThat(assessmentCriteria1).isEqualTo(assessmentCriteria2);

        assessmentCriteria2.setId(2L);
        assertThat(assessmentCriteria1).isNotEqualTo(assessmentCriteria2);

        assessmentCriteria1.setId(null);
        assertThat(assessmentCriteria1).isNotEqualTo(assessmentCriteria2);
    }
}