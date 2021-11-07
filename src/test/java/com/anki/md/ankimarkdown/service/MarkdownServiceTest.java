package com.anki.md.ankimarkdown.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

import static com.anki.md.ankimarkdown.service.MarkdownService.CARD_SEPARATOR;
import static com.anki.md.ankimarkdown.service.MarkdownService.FIELD_SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkdownServiceTest {

    private MarkdownService markdownService;

    @BeforeEach
    public void setUp() {
        this.markdownService = new MarkdownService(Parser.builder().build(), HtmlRenderer.builder().build());
    }

    @Test
    void whenTwoValidCards_thenReturnCorrectNumberOfFieldAndCardSeparators() throws IOException {
        final File file = new ClassPathResource("data/test_data_01.md").getFile();

        final String result = this.markdownService.convertMarkdownToNotesPlaneText(file);

        final int numberOfCardSeparators = StringUtils.countOccurrencesOf(result, CARD_SEPARATOR);
        final int numberOfFieldSeparators = StringUtils.countOccurrencesOf(result, FIELD_SEPARATOR);
        assertEquals(1, numberOfCardSeparators);
        assertEquals(6, numberOfFieldSeparators);
    }
}