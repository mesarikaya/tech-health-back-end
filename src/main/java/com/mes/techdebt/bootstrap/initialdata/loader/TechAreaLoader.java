package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.TechAreaDTO;
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
@Order(value=10)
@Slf4j
public class TechAreaLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.techArea}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<TechAreaDTO> techAreaLoadDataService;

    private List<TechAreaDTO> techAreaDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving TechArea data size: {}", techAreaDTOData.size());
        techAreaLoadDataService.saveAllWithSpecificId(techAreaDTOData);
        log.debug("TechArea data is saved");
    }

    private void setData() throws Exception {
        techAreaDTOData = getTechAreaData();
        log.debug("Read TeachArea data size: {}", techAreaDTOData.size());
    }

    private List<TechAreaDTO> getTechAreaData() throws Exception {
        CsvReader<TechAreaDTO> dataReader = new CsvReader<>();
        dataReader.setT(new TechAreaDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("TechAreaDTO data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
