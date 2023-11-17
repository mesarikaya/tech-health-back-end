package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.SiteDTO;
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
@Order(value=11)
@Slf4j
public class SiteDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.site}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<SiteDTO> siteLoadDataService;

    private List<SiteDTO> siteDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving Site data size: {}", siteDTOData.size());
        siteLoadDataService.saveAllWithSpecificId(siteDTOData);
        log.debug("Site data is saved");
    }

    private void setData() throws Exception {
        siteDTOData = getSiteData();
        //log.debug("Site data: {}", siteDTOData);
        log.debug("Read Site data size: {}", siteDTOData.size());
    }

    private List<SiteDTO> getSiteData() throws Exception {
        CsvReader<SiteDTO> dataReader = new CsvReader<>();
        dataReader.setT(new SiteDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("SiteDTO data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
