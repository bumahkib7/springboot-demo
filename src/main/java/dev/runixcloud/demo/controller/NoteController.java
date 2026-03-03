package dev.runixcloud.demo.controller;

import dev.runixcloud.demo.model.Note;
import dev.runixcloud.demo.repository.NoteRepository;
import dev.runixcloud.demo.service.StorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository repo;
    private final StorageService storage;

    public NoteController(NoteRepository repo, StorageService storage) {
        this.repo = repo;
        this.storage = storage;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("notes", repo.findAllByOrderByCreatedAtDesc());
        return "notes";
    }

    @PostMapping
    public String create(@RequestParam String title,
                         @RequestParam String content,
                         @RequestParam(required = false) MultipartFile file,
                         RedirectAttributes flash) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);

        if (file != null && !file.isEmpty()) {
            try {
                String key = storage.upload(file);
                note.setAttachmentKey(key);
                note.setAttachmentName(file.getOriginalFilename());
            } catch (Exception e) {
                flash.addFlashAttribute("error", "File upload failed: " + e.getMessage());
            }
        }

        repo.save(note);
        flash.addFlashAttribute("success", "Note created! (saved to PostgreSQL" +
                (note.getAttachmentKey() != null ? " + file uploaded to S3)" : ")"));
        return "redirect:/notes";
    }

    @GetMapping("/{id}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        Note note = repo.findById(id).orElseThrow();
        byte[] data = storage.download(note.getAttachmentKey());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + note.getAttachmentName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        Note note = repo.findById(id).orElseThrow();
        if (note.getAttachmentKey() != null) {
            storage.delete(note.getAttachmentKey());
        }
        repo.delete(note);
        flash.addFlashAttribute("success", "Note deleted.");
        return "redirect:/notes";
    }
}
