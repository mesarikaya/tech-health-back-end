package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
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
@Order(value=14)
@Slf4j
public class AssessmentResultDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.assessmentResult}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<AssessmentResultDTO> assessmentResultLoadDataService;

    private List<AssessmentResultDTO> assessmentResultDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving AssessmentResult data size: {}", assessmentResultDTOData.size());
        assessmentResultLoadDataService.saveAllWithSpecificId(assessmentResultDTOData);
        log.debug("AssessmentResult data is saved");
    }

    private void setData() throws Exception {
        assessmentResultDTOData = getAssessmentResultData();
        log.debug("Read AssessmentResult data size: {}", assessmentResultDTOData.size());
    }

    private List<AssessmentResultDTO> getAssessmentResultData() throws Exception {
        CsvReader<AssessmentResultDTO> dataReader = new CsvReader<>();
        dataReader.setT(new AssessmentResultDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("AssessmentResult data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
