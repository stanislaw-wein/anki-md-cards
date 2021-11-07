package com.anki.md.ankimarkdown.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The Service converts Markdown to Notes Plane Text
 */
@Service
@AllArgsConstructor
@Slf4j
public class MarkdownService {
    private static final String HTML_CARD_SEPARATOR = "<hr />";
    private static final String ANKI_CARD_SEPARATOR = "\n";
    private static final String MARKDOWN_LINE_BREAKERS = "[\\n\\r]+";
    private static final String TAB = "\t";
    private static final String EMPTY_LINES = "(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)";
    public static final String FILE_DESTINATION = "src/main/resources/data/notesplanetext/%s.txt";

    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public String convertMarkdownToNotesPlaneText(final File markdownFile) throws IOException {
        final String fileName = markdownFile.getName();
        final Node document = markdownParser.parse(new String(Files.readAllBytes(markdownFile.toPath())));
        final String htmlCards = htmlRenderer.render(document);

        final String[] htmlCardsSplit = htmlCards.split(HTML_CARD_SEPARATOR);

        final String notesPlaneText = Arrays
                .stream(htmlCardsSplit)
                .map(item -> item
                        .replaceAll(EMPTY_LINES, "")
                        .replaceAll(MARKDOWN_LINE_BREAKERS, TAB))
                .collect(Collectors.joining(ANKI_CARD_SEPARATOR));

        log.info(notesPlaneText);

        createNotesPlaneTextFile(fileName, notesPlaneText);

        return notesPlaneText;
    }

    private void createNotesPlaneTextFile(final String fileName, final String content) throws IOException {
        final File fileout = new File(String.format(FILE_DESTINATION, fileName));
        final FileWriter fw = new FileWriter(fileout.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
        }
    }
}
