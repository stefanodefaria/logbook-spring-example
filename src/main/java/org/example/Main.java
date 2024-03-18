package org.example;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;
import org.zalando.logbook.core.DefaultHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import java.io.IOException;
import java.util.function.Predicate;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                      .sink(new DefaultSink(new JsonHttpLogFormatter(), new DefaultHttpLogWriter()))
                      .headerFilter(HeaderFilters.replaceCookies("JSESSIONID"::equalsIgnoreCase, "XXX"))
                      .headerFilter(HeaderFilters.replaceCookies("__Secure-Request-Token"::equalsIgnoreCase, "XXX"))
                      .build();
    }
}