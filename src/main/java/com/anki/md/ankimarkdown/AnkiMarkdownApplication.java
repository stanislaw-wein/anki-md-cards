package com.anki.md.ankimarkdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class AnkiMarkdownApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AnkiMarkdownApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        File file = new ClassPathResource("data/md/german_driving_test.md").getFile();
        Parser parser = Parser.builder().build();
        Node document = parser.parse(new String(Files.readAllBytes(file.toPath())));
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        final String[] split = html.split("<hr />");
        final String collect = Arrays
                .stream(split)
                .map(item -> item.replaceAll("[\\n\\r]+", "\t"))
                .collect(Collectors.joining("\n"));

        log.info(collect);

        File fileout = new File("src/main/resources/data/notesplanetext/filename.txt");
        FileWriter fw = new FileWriter(fileout.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(collect);
        bw.close();

    }
}
