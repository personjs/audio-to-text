package com.yunostove.fileprocessoro.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    public void init();
    public void save(MultipartFile file);
    public Resource load(String filename);
    public void deleteAll();
    public void delete(String filename);
    public Stream<Path> loadAll();
    public String getFileFromName(String filename);
}
