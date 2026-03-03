package dev.runixcloud.demo.controller;

import dev.runixcloud.demo.repository.KafkaMessageRepository;
import dev.runixcloud.demo.repository.NoteRepository;
import dev.runixcloud.demo.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;

@Controller
public class HomeController {

    private final NoteRepository noteRepo;
    private final KafkaMessageRepository kafkaRepo;
    private final StorageService storage;
    private final DataSource dataSource;

    public HomeController(NoteRepository noteRepo, KafkaMessageRepository kafkaRepo,
                          StorageService storage, DataSource dataSource) {
        this.noteRepo = noteRepo;
        this.kafkaRepo = kafkaRepo;
        this.storage = storage;
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Service status checks
        model.addAttribute("pgStatus", checkPostgres());
        model.addAttribute("s3Status", storage.isAvailable());
        model.addAttribute("kafkaStatus", checkKafka());

        // Counts
        model.addAttribute("noteCount", noteRepo.count());
        model.addAttribute("fileCount", storage.listFiles().size());
        model.addAttribute("kafkaCount", kafkaRepo.count());

        return "index";
    }

    private boolean checkPostgres() {
        try {
            dataSource.getConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkKafka() {
        try {
            return kafkaRepo.count() >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}
