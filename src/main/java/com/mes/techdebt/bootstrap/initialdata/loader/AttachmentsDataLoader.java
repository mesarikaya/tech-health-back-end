package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.AttachmentDTO;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Profile({"local-dev","dev", "stage", "prod"})
@Component
@Order(value=16)
@Slf4j
public class AttachmentsDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.attachments}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<AttachmentDTO> attachmentLoadDataService;

    private List<AttachmentDTO> attachmentDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving Attachment data size: {}", attachmentDTOData.size());
        attachmentLoadDataService.saveAllWithSpecificId(attachmentDTOData);
        log.debug("Attachment data is saved");
    }

    private void setData() throws Exception {
        attachmentDTOData = getAttachmentData();
        //log.debug("Attachment data: {}", attachmentDTOData);
        log.debug("Read Attachment data size: {}", attachmentDTOData.size());
    }

    private List<AttachmentDTO> getAttachmentData() throws Exception {
        CsvReader<AttachmentDTO> dataReader = new CsvReader<>();
        dataReader.setT(new AttachmentDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("AttachmentDTO data file found? {}", dataStream.available());
        return dataReader.readCsv(dataStream).readAll();
    }
}
