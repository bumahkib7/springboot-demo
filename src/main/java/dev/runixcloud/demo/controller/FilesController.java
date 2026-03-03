package dev.runixcloud.demo.controller;

import dev.runixcloud.demo.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/files")
public class FilesController {

    private final StorageService storage;

    public FilesController(StorageService storage) {
        this.storage = storage;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("files", storage.listFiles());
        model.addAttribute("available", storage.isAvailable());
        return "files";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file, RedirectAttributes flash) {
        try {
            String key = storage.upload(file);
            flash.addFlashAttribute("success", "Uploaded to S3 as: " + key);
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/files";
    }

    @PostMapping("/{key}/delete")
    public String delete(@PathVariable String key, RedirectAttributes flash) {
        storage.delete(key);
        flash.addFlashAttribute("success", "Deleted from S3.");
        return "redirect:/files";
    }
}
