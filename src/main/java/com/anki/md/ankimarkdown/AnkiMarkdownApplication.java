package com.anki.md.ankimarkdown;

import com.anki.md.ankimarkdown.service.MarkdownService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        log.info("\n >>> Please enter the markdown file path");
        final String markdownFilePath = reader.readLine();
        final File file = new File(markdownFilePath);
        log.info("\n >>> The file [{}] will be converted to a Anki Plain Test", file.getName());

        final String notesPlaneText = markdownService.convertMarkdownToNotesPlaneText(file);
        MarkdownService.createNotesPlaneTextFile(file.getName(), notesPlaneText);

    }
}
