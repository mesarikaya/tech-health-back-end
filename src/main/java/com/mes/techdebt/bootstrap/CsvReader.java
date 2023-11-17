package com.mes.techdebt.bootstrap;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.InputStream;

public class CsvReader<T> implements CsvRead<T>{

    private static final CsvMapper csvMapper = new CsvMapper();
    private T t;

    public void setT(Object o) {
        this.t = (T) o;
    }

    @Override
    public MappingIterator<T> readCsv(InputStream csvFileSteam) throws Exception {
        // create a schema for the object and order the columns to get the proper inputs
        CsvSchema csvSchema = csvMapper
                .schemaFor(t.getClass())
                .withHeader()
                .withColumnSeparator(';')
                .withColumnReordering(true);

        // Read the values and ignore the unknown columns
        return csvMapper
                .enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
                .disable(CsvParser.Feature.FAIL_ON_MISSING_HEADER_COLUMNS)
                .readerFor(t.getClass())
                .with(csvSchema)
                .readValues(csvFileSteam);
    }
}
