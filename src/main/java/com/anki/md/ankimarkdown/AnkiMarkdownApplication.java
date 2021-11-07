package com.anki.md.ankimarkdown;

import com.anki.md.ankimarkdown.service.MarkdownService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class AnkiMarkdownApplication implements CommandLineRunner {

    private final MarkdownService markdownService;

    public static void main(String[] args) {
        SpringApplication.run(AnkiMarkdownApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        final File file = new ClassPathResource("data/md/test_list.md").getFile();
        markdownService.convertMarkdownToNotesPlaneText(file);
    }
}
