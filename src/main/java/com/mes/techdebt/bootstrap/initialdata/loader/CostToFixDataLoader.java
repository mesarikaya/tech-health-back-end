package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.CostToFixDTO;
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
@Order(value=15)
@Slf4j
public class CostToFixDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.costToFix}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<CostToFixDTO> costToFixLoadDataService;

    private List<CostToFixDTO> costToFixDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving CostToFix data size: {}", costToFixDTOData.size());
        costToFixLoadDataService.saveAllWithSpecificId(costToFixDTOData);
        log.debug("CostToFix data is saved");
    }

    private void setData() throws Exception {
        costToFixDTOData = getCostToFixData();
        log.debug("Read CostToFix data size: {}", costToFixDTOData.size());
    }

    private List<CostToFixDTO> getCostToFixData() throws Exception {
        CsvReader<CostToFixDTO> dataReader = new CsvReader<>();
        dataReader.setT(new CostToFixDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("CostToFix data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
