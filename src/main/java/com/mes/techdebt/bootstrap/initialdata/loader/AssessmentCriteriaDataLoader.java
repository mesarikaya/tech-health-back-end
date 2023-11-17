package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
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
@Order(value=13)
@Slf4j
public class AssessmentCriteriaDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.assessmentCriteria}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<AssessmentCriteriaDTO> assessmentCriteriaLoadDataService;
    private List<AssessmentCriteriaDTO> assessmentCriteriaDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving AssessmentCriteria data size: {}", assessmentCriteriaDTOData.size());
        assessmentCriteriaLoadDataService.saveAllWithSpecificId(assessmentCriteriaDTOData);
        log.debug("AssessmentCriteria data is saved");
    }

    private void setData() throws Exception {
        assessmentCriteriaDTOData = getAssessmentCriteriaData();
        log.debug("Read AssessmentCriteria data size: {}", assessmentCriteriaDTOData.size());
    }

    private List<AssessmentCriteriaDTO> getAssessmentCriteriaData() throws Exception {
        CsvReader<AssessmentCriteriaDTO> dataReader = new CsvReader<>();
        dataReader.setT(new AssessmentCriteriaDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("AssessmentCriteria data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
