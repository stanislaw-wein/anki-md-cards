package com.anki.md.ankimarkdown;

import com.anki.md.ankimarkdown.service.MarkdownService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class AnkiMarkdownApplication implements CommandLineRunner {
    private static final String FILE_DESTINATION = "src/main/resources/data/notesplanetext/%s.txt";
    private final MarkdownService markdownService;

    public static void main(String[] args) {
        SpringApplication.run(AnkiMarkdownApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        log.info("\n >>> Please enter the markdown file path");

        final String markdownFilePath = reader.readLine();
        final File markdownFile = new File(markdownFilePath);
        log.info("\n >>> The file [{}] will be converted to a Anki Plain Test", markdownFile.getName());

        final String notesPlaneText = markdownService.convertMarkdownToNotesPlaneText(markdownFile);
        log.info("\n >>> Conversion completed; Saving the result to a file");

        createNotesPlaneTextFile(markdownFile.getName(), notesPlaneText);
        log.info("\n >>> Execution finished");
    }

    private static void createNotesPlaneTextFile(final String fileName, final String content) throws IOException {
        final File fileOut = new File(String.format(FILE_DESTINATION, fileName));
        final FileWriter fw = new FileWriter(fileOut.getAbsoluteFile(), StandardCharsets.UTF_8);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
            log.info("\n >>> File location - {}", fileOut.getAbsolutePath());
        } catch (Exception ex) {
            log.error("\n >>> Error during file creation {}", ex.getMessage(), ex);
        }
    }
}
