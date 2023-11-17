package com.mes.techdebt.bootstrap.initialdata.loader;

import com.mes.techdebt.bootstrap.CsvReader;
import com.mes.techdebt.bootstrap.initialdata.services.GenericLoadDataService;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
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
@Order(value=9)
@Slf4j
public class InvestmentCriticalityDataLoader implements CommandLineRunner {

    private String csvFileBase = "classpath:db/data/";

    @Autowired
    @Qualifier("webApplicationContext")
    private ResourceLoader resourceLoader;

    @Value("${test_data.investmentCriticality}")
    private String dataFile;

    @Autowired
    private GenericLoadDataService<InvestmentCriticalityDTO> investmentCriticalityLoadDataService;

    private List<InvestmentCriticalityDTO> investmentCriticalityDTOData;

    @Override
    public void run(String... args) throws Exception {
        setData();
        populateDatabase();
    }

    @Synchronized
    public void populateDatabase() {
        log.debug("Saving InvestmentCriticality data size: {}", investmentCriticalityDTOData.size());
        investmentCriticalityLoadDataService.saveAllWithSpecificId(investmentCriticalityDTOData);
        log.debug("InvestmentCriticality data is saved");
    }

    private void setData() throws Exception {
        investmentCriticalityDTOData = getInvestmentCriticalityData();
        log.debug("Read InvestmentCriticality data size: {}", investmentCriticalityDTOData.size());
    }

    private List<InvestmentCriticalityDTO> getInvestmentCriticalityData() throws Exception {
        CsvReader<InvestmentCriticalityDTO> dataReader = new CsvReader<>();
        dataReader.setT(new InvestmentCriticalityDTO());
        Resource resource = resourceLoader.getResource(csvFileBase + dataFile);
        InputStream dataStream = resource.getInputStream();
        log.debug("InvestmentCriticalityDTO data file found? {}", dataStream.available());
        return dataReader
                .readCsv(dataStream).readAll();
    }
}
