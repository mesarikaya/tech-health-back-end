package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.TechDomainDTO;
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
@Order(value=7)
@Slf4j
public class TechDomainDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.techDomain}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<TechDomainDTO> techDomainLoadDataService;

    private List<TechDomainDTO> techDomainDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving TechDomain data size: {}", techDomainDTOData.size());
        techDomainLoadDataService.saveAllWithSpecificId(techDomainDTOData);
        log.debug("TechDomain data is saved");
    }

    private void setData() throws Exception {
        techDomainDTOData = getTechDomainData();
        log.debug("Read TechDomain data size: {}", techDomainDTOData.size());
    }

    private List<TechDomainDTO> getTechDomainData() throws Exception {
        CsvReader<TechDomainDTO> dataReader = new CsvReader<>();
        dataReader.setT(new TechDomainDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("TechDomainDTO data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
