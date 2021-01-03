package com.upstox.common.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.common.FileReader;
import com.upstox.exception.UpstoxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileReaderJsonOneLinerImpl<T> implements FileReader<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderJsonOneLinerImpl.class);
    private File file;
    private final Class<T> entityClass;
    private final ObjectMapper mapper = new ObjectMapper();
    private String fileName;

    public FileReaderJsonOneLinerImpl(String fileName, Class<T> entityClass) {
        this.fileName = fileName;
        this.entityClass = entityClass;
    }

    public FileReaderJsonOneLinerImpl(File file, Class<T> entityClass) {
        this.file = file;
        this.entityClass = entityClass;
    }

    @Override
    public Stream<T> readAll() {
        LOGGER.info("Reading Trades data from file trades.json");
        Predicate<String> isEmpty = x->x.isEmpty();
        try {
            URL resource = FileReaderJsonOneLinerImpl.class.getClassLoader()
                    .getResource(fileName);
            if (null == resource) {
                throw new FileNotFoundException("File not found");
            }

            return Files.readAllLines(Paths.get(resource.toURI()), StandardCharsets.UTF_8)
                    .stream().filter(isEmpty.negate()).map(this::parse);
        } catch (Exception ex) {
            LOGGER.error("Failed to read file:  {} {} {}", file, entityClass, ex);
            throw new UpstoxException("Failed to read file: " + file, entityClass, ex);
        }
    }

    private T parse(String json) {
        try {
            return mapper.readValue(json, entityClass);
        } catch (Exception ex) {
            //TODO : Depends upon use case whether to fail all or continue if one of the line is not JSON
            LOGGER.error("Failed to parse json: {} {} {}", json, entityClass, ex);
            throw new UpstoxException("Failed to parse json: " + json, entityClass, ex);
        }
    }

    @Override
    public Stream<T> readAllOutside() {
        LOGGER.info("Reading Trades data");
        Predicate<String> isEmpty = x->x.isEmpty();
        try {
            return Files.lines(file.toPath(), StandardCharsets.UTF_8)
                    .filter(isEmpty.negate()).map(this::parse);
        } catch (Exception ex) {
            LOGGER.error("Failed to read file:  {} {} {}", file, entityClass, ex);
            throw new UpstoxException("Failed to read file: " + file, entityClass, ex);
        }
    }
}
