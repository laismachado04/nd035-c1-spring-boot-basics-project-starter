package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.FileForm;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FileMapper {

    @Select("SELECT fileId, filename FROM FILES")
    List<FileForm> getAllFileNames();

    @Insert("INSERT INTO FILES (filename, contentType, fileSize, userId, fileData) VALUES(#{filename}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int addFile(FileForm fileForm);

    @Select("SELECT fileData FROM FILES where fileId = #{fileId}")
    Optional<FileForm> getFileDataById(Integer fileId);

    @Select("SELECT filename FROM FILES where fileId = #{fileId}")
    String getFilenameById(Integer fileId);

    @Select("SELECT filename FROM FILES where filename = #{filename}")
    String getFileName(String filename);

    @Delete("DELETE FROM FILES where fileId = #{fileId}")
    int deleteFile(Integer fileId);
}
