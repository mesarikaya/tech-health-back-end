package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Attachment;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AttachmentDTOTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attachment.class);

        AttachmentDTO attachment1 = new AttachmentDTO();
        attachment1.setId(1L);

        AttachmentDTO attachment2 = new AttachmentDTO();
        attachment2.setId(attachment1.getId());
        assertThat(attachment1).isEqualTo(attachment2);

        attachment2.setId(2L);
        assertThat(attachment1).isNotEqualTo(attachment2);

        attachment1.setId(null);
        assertThat(attachment1).isNotEqualTo(attachment2);
    }
}