package dev.runixcloud.demo.controller;

import dev.runixcloud.demo.repository.KafkaMessageRepository;
import dev.runixcloud.demo.service.KafkaProducerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducerService producer;
    private final KafkaMessageRepository repo;

    public KafkaController(KafkaProducerService producer, KafkaMessageRepository repo) {
        this.producer = producer;
        this.repo = repo;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("messages", repo.findTop50ByOrderByTimestampDesc());
        return "kafka";
    }

    @PostMapping("/send")
    public String send(@RequestParam String message, RedirectAttributes flash) {
        producer.send(message);
        flash.addFlashAttribute("success",
                "Message sent to Kafka! The consumer will pick it up and persist it to PostgreSQL.");
        return "redirect:/kafka";
    }
}
