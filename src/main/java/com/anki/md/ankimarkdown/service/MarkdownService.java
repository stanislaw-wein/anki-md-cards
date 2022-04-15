package com.anki.md.ankimarkdown.service;

import com.anki.md.ankimarkdown.exception.ValidationException;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.TextNode;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The Service converts Markdown to Notes Plane Text
 */
@Service
@AllArgsConstructor
@Slf4j
public class MarkdownService {
    public static final String HTML_CARD_SEPARATOR_SELECTOR = "<hr />";
    public static final String PLANE_TEXT_CARD_SEPARATOR = "\n";
    public static final String PLANE_TEXT_FIELD_SEPARATOR = "\t";
    public static final String MARKDOWN_LINE_BREAKERS = "[\\n\\r]+";
    public static final String EMPTY_LINES = "(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)";
    public static final int FIRST_CARD = 0;

    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public String convertMarkdownToNotesPlaneText(final File markdownFile) throws IOException {
        final com.vladsch.flexmark.util.ast.Node markdownDocument =
                markdownParser.parse(new String(Files.readAllBytes(markdownFile.toPath())));
        final String htmlCards = htmlRenderer.render(markdownDocument);
        final String[] htmlCardsArray = htmlCards.split(HTML_CARD_SEPARATOR_SELECTOR);

        validateHtmlCards(htmlCardsArray);

        return Arrays
                .stream(htmlCardsArray)
                .map(MarkdownService::convertHtmlCardToPlainTextCard)
                .collect(Collectors.joining(PLANE_TEXT_CARD_SEPARATOR));
    }

    private static String convertHtmlCardToPlainTextCard(final String htmlCard) {
        return Jsoup.parseBodyFragment(htmlCard).body().childNodes()
                    .stream()
                    .filter(node -> !(node instanceof TextNode textNode && textNode.isBlank())) // blank line is a field separator
                    .map(node -> node.toString()
                                     .replaceAll(EMPTY_LINES, "")
                                     .replaceAll(MARKDOWN_LINE_BREAKERS, ""))
                    .collect(Collectors.joining(PLANE_TEXT_FIELD_SEPARATOR));
    }

    private static void validateHtmlCards(final String[] htmlCards) {
        if (htmlCards == null || htmlCards.length == 0) {
            log.error("\n >> No cards detected");
            throw new ValidationException("No cards detected");
        }
        final long expectedNumberOfFields = getNumberOfFields(htmlCards[FIRST_CARD]);
        log.info("\n >> Number of cards is {}; Number of fields in the card is: {}", htmlCards.length, expectedNumberOfFields);

        IntStream.range(0, htmlCards.length)
                 .forEach(index -> {
                     long currentCardNumberOfFields = getNumberOfFields(htmlCards[index]);
                     if (index == htmlCards.length - 1 && htmlCards[index].equalsIgnoreCase(PLANE_TEXT_CARD_SEPARATOR)) {
                         log.error("\n >> The Markdown file must not end with a card separator. Please remove the separator and try again.");
                         throw new ValidationException("The Markdown file must not end with a card separator. Please remove the separator and try again.");
                     }
                     if (currentCardNumberOfFields != expectedNumberOfFields) {
                         log.warn("\n >> This card has an incorrect number of fields: {}, expectedNumberOfFields: {}, {}", currentCardNumberOfFields,
                                 expectedNumberOfFields,
                                 htmlCards[index]);
                     }
                 });
    }

    private static long getNumberOfFields(final String htmlCard) {
        return Jsoup.parseBodyFragment(htmlCard).body().childNodes()
                    .stream()
                    .filter(node -> node instanceof TextNode textNode && textNode.isBlank())
                    .count();
    }

}
