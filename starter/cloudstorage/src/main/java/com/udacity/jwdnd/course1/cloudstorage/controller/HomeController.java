package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.FileForm;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping
public class HomeController {

    private FileService fileService;
    private NoteService noteService;

    public HomeController(FileService fileService, UserMapper userMapper, FileMapper fileMapper, NoteService noteService, NoteMapper noteMapper) {
        this.fileService = fileService;
        this.userMapper = userMapper;
        this.fileMapper = fileMapper;
        this.noteService = noteService;
        this.noteMapper = noteMapper;
    }

    @Autowired
    private final UserMapper userMapper;
    private final FileMapper fileMapper;
    private final NoteMapper noteMapper;

    @GetMapping("/home")
    public String homepage(Model model) {
        List<FileForm> listFiles = this.fileService.getFiles();
        model.addAttribute("listFiles", listFiles);

        List<NoteForm> listNotes = this.noteService.getNotes();
        model.addAttribute("listNotes", listNotes);

        return "home";
    }

    @PostMapping("/home")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile multipartFile, RedirectAttributes redirectAttributes,
                             Authentication authentication) throws Exception {

        String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        FileForm fileForm = new FileForm();
        if (!fileService.checkIfFileExists(filename)) {
            fileForm.setFilename(filename);
            fileForm.setContentType("file");
            fileForm.setFileData(multipartFile.getBytes());
            fileForm.setFileSize(String.valueOf(multipartFile.getSize()));
            fileForm.setUserId(userMapper.getUserId(authentication.getName()));

            this.fileService.uploadFile(fileForm);

            redirectAttributes.addFlashAttribute("uploadFileSuccess", true);

        } else redirectAttributes.addFlashAttribute("uploadFileError", "A file with the same name has already been uploaded!");

        return "redirect:/home";
    }

    @GetMapping("/download")
    public void downloadFile(@Param("fileId") Integer fileId, HttpServletResponse response) throws Exception {
        Optional<FileForm> result = fileMapper.getFileDataById(fileId);

        if (result.isEmpty()) {
            throw new Exception("Could not find the file with fileId: " + fileId);
        }

        FileForm file = result.get();

        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileMapper.getFilenameById(fileId);

        response.setHeader(headerKey, headerValue);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(file.getFileData());
        outputStream.close();
    }

    @GetMapping("/deleteFile/{fileId}")
    public String deleteFile(@PathVariable("fileId") Integer fileId, Model model) {
        FileForm fileForm = fileMapper.getFileDataById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + fileId));
        model.addAttribute("deleteMsg", true);
        fileMapper.deleteFile(fileId);
        return "redirect:/home";
    }

    @PostMapping("/home/notes")
    public String createNote(@ModelAttribute NoteForm noteForm, Model model,
                             Authentication authentication) throws Exception {

        String noteTitle = noteForm.getNoteTitle();
        String noteDescription = noteForm.getNoteDescription();

        if (!noteService.checkIfNoteExists(noteTitle))
            noteForm.setNoteTitle(noteTitle);
            noteForm.setNoteDescription(noteDescription);
            noteForm.setUserId(userMapper.getUserId(authentication.getName()));

            this.noteService.createNote(noteForm);

        model.addAttribute("noteSuccess", true);

            return "redirect:/home";
        }

    @GetMapping("/deleteNote/{noteId}")
    public String deleteNote(@PathVariable("noteId") Integer noteId, Model model) {
        NoteForm noteForm = noteMapper.getNoteById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid noteId:" + noteId));
        model.addAttribute("deleteNoteMsg", true);
        noteMapper.deleteNote(noteId);
        return "redirect:/home";
    }

    @RequestMapping(value="/updateNote/{noteId}", method = {RequestMethod.PUT, RequestMethod.GET})
    public String updateNote(@PathVariable("noteId") Integer noteId, Model model) {
        NoteForm noteForm = noteMapper.getNoteById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid noteId:" + noteId));
        model.addAttribute("updateNoteMsg", true);
        noteMapper.updateNote(noteId);
        return "redirect:/home";
    }

}
