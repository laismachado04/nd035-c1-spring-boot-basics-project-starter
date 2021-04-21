package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.config.FileStorageException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteForm;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class NoteService {

    private NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Creating NoteService bean");
    }

    public List<NoteForm> getNotes() {
        return noteMapper.getAllNotes();
    }

    public int createNote(NoteForm noteForm) {
        try {
            noteMapper.addNote(noteForm);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + noteForm.getNoteTitle()
                    + ". Please try again!");
        }
        return 0;
    }

    public boolean checkIfNoteExists(String noteTitle) {
        return noteMapper.getNoteTitle(noteTitle) != null;
    }

}
