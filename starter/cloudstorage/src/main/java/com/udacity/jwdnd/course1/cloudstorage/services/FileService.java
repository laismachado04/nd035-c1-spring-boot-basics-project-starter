package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.config.FileStorageException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.FileForm;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class FileService {

    private FileMapper fileMapper;

    public FileService(FileMapper fileMapper) { this.fileMapper = fileMapper; }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Creating FileService bean");
    }

    public int uploadFile(FileForm fileForm) {

        try {
            fileMapper.addFile(fileForm);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + fileForm.getFilename()
                    + ". Please try again!");
        }
        return 0;
    }

    public List<FileForm> getFiles() {
        return fileMapper.getAllFileNames();
    }

    public boolean checkIfFileExists(String filename) {
        return fileMapper.getFileName(filename) != null;
    }

}
