package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentCriteriaDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        AssessmentCriteriaDTO assessmentCriteria1 = new AssessmentCriteriaDTO();
        assessmentCriteria1.setId(1L);

        AssessmentCriteriaDTO assessmentCriteria2 = new AssessmentCriteriaDTO();
        assessmentCriteria2.setId(assessmentCriteria1.getId());
        assertThat(assessmentCriteria1).isEqualTo(assessmentCriteria2);

        assessmentCriteria2.setId(2L);
        assertThat(assessmentCriteria1).isNotEqualTo(assessmentCriteria2);

        assessmentCriteria1.setId(null);
        assertThat(assessmentCriteria1).isNotEqualTo(assessmentCriteria2);
    }
}