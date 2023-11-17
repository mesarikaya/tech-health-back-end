package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentResultDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        AssessmentResultDTO assessmentResult1 = new AssessmentResultDTO();
        assessmentResult1.setId(1L);

        AssessmentResultDTO assessmentResult2 = new AssessmentResultDTO();
        assessmentResult2.setId(assessmentResult1.getId());
        assertThat(assessmentResult1).isEqualTo(assessmentResult2);

        assessmentResult2.setId(2L);
        assertThat(assessmentResult1).isNotEqualTo(assessmentResult2);

        assessmentResult1.setId(null);
        assertThat(assessmentResult1).isNotEqualTo(assessmentResult2);
    }
}