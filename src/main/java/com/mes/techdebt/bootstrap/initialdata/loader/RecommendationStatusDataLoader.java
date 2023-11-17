package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
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
@Order(value=8)
@Slf4j
public class RecommendationStatusDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.recommendationStatus}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<RecommendationStatusDTO> recommendationStatusLoadDataService;

    private List<RecommendationStatusDTO> recommendationStatusDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving RecommendationStatus data size: {}", recommendationStatusDTOData.size());
        recommendationStatusLoadDataService.saveAllWithSpecificId(recommendationStatusDTOData);
        log.debug("RecommendationStatus data is saved");
    }

    private void setData() throws Exception {
        recommendationStatusDTOData = getRecommendationStatusData();
        log.debug("Read RecommendationStatus data size: {}", recommendationStatusDTOData.size());
    }

    private List<RecommendationStatusDTO> getRecommendationStatusData() throws Exception {
        CsvReader<RecommendationStatusDTO> dataReader = new CsvReader<>();
        dataReader.setT(new RecommendationStatusDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("RecommendationStatusDTO data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
