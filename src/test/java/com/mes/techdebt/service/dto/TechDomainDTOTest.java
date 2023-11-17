package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TechDomainDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);

        TechDomainDTO techDomain1 = new TechDomainDTO();
        techDomain1.setId(1L);

        TechDomainDTO techDomain2 = new TechDomainDTO();
        techDomain2.setId(techDomain1.getId());
        assertThat(techDomain1).isEqualTo(techDomain2);

        techDomain2.setId(2L);
        assertThat(techDomain1).isNotEqualTo(techDomain2);

        techDomain1.setId(null);
        assertThat(techDomain1).isNotEqualTo(techDomain2);
    }
}