package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.NoteForm;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NoteMapper {

    @Select("SELECT noteId, noteTitle, noteDescription FROM NOTES")
    List<NoteForm> getAllNotes();

    @Insert("INSERT INTO NOTES (noteTitle, noteDescription, userId) VALUES(#{noteTitle}, #{noteDescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    int addNote(NoteForm notesForm);

    @Select("SELECT * FROM NOTES where noteId = #{noteId}")
    Optional<NoteForm> getNoteById(Integer noteId);

    @Select("SELECT noteTitle FROM NOTES where noteTitle = #{noteTitle}")
    String getNoteTitle(String noteTitle);

    @Delete("DELETE FROM NOTES where noteId = #{noteId}")
    int deleteNote(Integer noteId);

    @Update("UPDATE NOTES set noteTitle = #{noteTitle}, noteDescription = #{noteDescription} where noteId = #{noteId}")
    int updateNote(Integer noteId);
}
