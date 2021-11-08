package com.anki.md.ankimarkdown.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
    public static final String HTML_CARD_SEPARATOR = "<hr />";
    public static final String HTML_FIELD_SELECTOR = "blockquote";
    public static final String CARD_SEPARATOR = "\n";
    public static final String FIELD_SEPARATOR = "\t";
    public static final String MARKDOWN_LINE_BREAKERS = "[\\n\\r]+";
    public static final String EMPTY_LINES = "(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)";
    public static final String FILE_DESTINATION = "src/main/resources/data/notesplanetext/%s.txt";

    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public String convertMarkdownToNotesPlaneText(final File markdownFile) throws IOException {
        final Node markdownDocument = markdownParser.parse(new String(Files.readAllBytes(markdownFile.toPath())));
        final String htmlBlockquoteCards = htmlRenderer.render(markdownDocument);
        final String[] blockquoteCardsSplit = htmlBlockquoteCards.split(HTML_CARD_SEPARATOR);

        return Arrays.stream(blockquoteCardsSplit)
                .map(MarkdownService::convertBlockquotesToPlainTextCard)
                .collect(Collectors.joining(CARD_SEPARATOR));
    }

    public static void createNotesPlaneTextFile(final String fileName, final String content) throws IOException {
        final File fileout = new File(String.format(FILE_DESTINATION, fileName));
        final FileWriter fw = new FileWriter(fileout.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
        }
    }

    private static String convertBlockquotesToPlainTextCard(String item) {
        final Document doc = Jsoup.parseBodyFragment(item);
        final Elements ankiFields = doc.select(HTML_FIELD_SELECTOR);
        return ankiFields.stream()
                .map(field -> field.html().replaceAll(EMPTY_LINES, "").replaceAll(MARKDOWN_LINE_BREAKERS, ""))
                .collect(Collectors.joining(FIELD_SEPARATOR));
    }

}
