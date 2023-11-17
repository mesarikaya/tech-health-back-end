package com.mes.techdebt.domain;

import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TechDomainTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        TechDomain techDomain1 = new TechDomain();
        techDomain1.setId(1L);

        TechDomain techDomain2 = new TechDomain();
        techDomain2.setId(techDomain1.getId());
        assertThat(techDomain1).isEqualTo(techDomain2);

        techDomain2.setId(2L);
        assertThat(techDomain1).isNotEqualTo(techDomain2);

        techDomain1.setId(null);
        assertThat(techDomain1).isNotEqualTo(techDomain2);
    }
}