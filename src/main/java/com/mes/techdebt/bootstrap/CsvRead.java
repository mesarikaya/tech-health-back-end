package com.mes.techdebt.bootstrap;

import com.fasterxml.jackson.databind.MappingIterator;

import java.io.InputStream;

public interface CsvRead<T>{
    public MappingIterator<T> readCsv(InputStream dataStream) throws Exception;
}
