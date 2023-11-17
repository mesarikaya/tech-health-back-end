package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.CategoryDTO;
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
@Order(value=12)
@Slf4j
public class CategoryDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";
    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.category}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<CategoryDTO> categoryLoadDataService;

    private List<CategoryDTO> categoryDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving Category data size: {}", categoryDTOData.size());
        categoryLoadDataService.saveAllWithSpecificId(categoryDTOData);
        log.debug("Category data is saved");
    }

    private void setData() throws Exception {
        categoryDTOData = getCategoryData();
        log.debug("Read Category data size: {}", categoryDTOData.size());
    }

    private List<CategoryDTO> getCategoryData() throws Exception {
        CsvReader<CategoryDTO> dataReader = new CsvReader<>();
        dataReader.setT(new CategoryDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("Category data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
