package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.logbook.BodyReplacer;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.BodyFilters;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.core.ResponseFilters;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nullable;

import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                      .condition(exclude(
                              requestTo("/**/actuator/**"),
                              requestTo("/not-found")
                      ))
                      .sink(new DefaultSink(new JsonHttpLogFormatter(), new DefaultHttpLogWriter()))
                      .responseFilter(ResponseFilters.replaceBody(new UnzipBodyReplacer()))
                      .bodyFilter(BodyFilters.truncate(4096))
                      .headerFilter(HeaderFilters.defaultValue())
                      .headerFilter(HeaderFilters.replaceCookies("JSESSIONID"::equalsIgnoreCase, "XXX"))
                      .headerFilter(HeaderFilters.replaceCookies("__Secure-Request-Token"::equalsIgnoreCase, "XXX"))
                      .build();
    }

    @Slf4j
    static class UnzipBodyReplacer implements BodyReplacer<HttpResponse> {

        @Nullable
        @Override
        public String replace(HttpResponse httpResponse) {
            if ("gzip".equalsIgnoreCase(httpResponse.getHeaders().getFirst(CONTENT_ENCODING))) {
                try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(httpResponse.getBody()));
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = gis.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    return outputStream.toString();
                } catch (IOException e) {
                    log.error("Error decompressing body", e);
                }
            }

            try {
                return httpResponse.getBodyAsString();
            } catch (IOException e) {
                log.error("Error extracting non-zipped body", e);
                return "<UnzipBodyReplacer error>";
            }
        }
    }

}