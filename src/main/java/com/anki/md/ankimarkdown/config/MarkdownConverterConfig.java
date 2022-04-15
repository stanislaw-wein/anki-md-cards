package com.anki.md.ankimarkdown.config;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarkdownConverterConfig {
    private static final MutableDataSet options = new MutableDataSet()
            .set(HtmlRenderer.SOFT_BREAK, "<br />\n")
            .set(HtmlRenderer.MAX_BLANK_LINES, 1)
            .set(Parser.BLANK_LINES_IN_AST, true);

    @Bean
    public FlexmarkHtmlConverter htmlToMarkdownConverter() {
        return FlexmarkHtmlConverter.builder()
                                    .build();
    }

    @Bean
    public HtmlRenderer htmlRenderer() {
        return HtmlRenderer.builder(options)
                           .build();
    }

    @Bean
    public Parser markdownParser() {
        return Parser.builder(options)
                     .build();
    }

}
