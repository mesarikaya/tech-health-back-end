package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.CostRangeDTO;
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
@Order(value=6)
@Slf4j
public class CostRangeDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.costRange}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<CostRangeDTO> costRangeLoadDataService;

    private List<CostRangeDTO> costRangeDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving ConstRange data size: {}", costRangeDTOData.size());
        costRangeLoadDataService.saveAllWithSpecificId(costRangeDTOData);
        log.debug("CostRange data is saved");
    }

    private void setData() throws Exception {
        costRangeDTOData = getCostRangeData();
        log.debug("Read CostRange data size: {}", costRangeDTOData.size());
    }

    private List<CostRangeDTO> getCostRangeData() throws Exception {
        CsvReader<CostRangeDTO> dataReader = new CsvReader<>();
        dataReader.setT(new CostRangeDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("CostRange data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
