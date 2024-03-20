package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
public class SampleController {

    @Value("classpath:test-gzipped-file.gz")
    Resource gzippedFile;

    private byte[] gzippedBytes;

    @PostConstruct
    public void init() throws IOException {
        gzippedBytes = new byte[(int) gzippedFile.getFile().length()];
        try (FileInputStream inputStream = new FileInputStream(gzippedFile.getFile())) {
            inputStream.read(gzippedBytes);
        }
    }

    @GetMapping(path = "/any/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> any() {

        return ResponseEntity.status(HttpStatus.OK)
                             .header("X-CustomHeader", "CustomHeaderValue")
                             .body("{\"data\": \"ResponseData1234\"}");
    }

    @PostMapping(path = "/uncompressed-ok", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uncompressedEndpoint() {

        return ResponseEntity.status(HttpStatus.OK)
                             .header("X-CustomHeader", "CustomHeaderValue")
                             .body("{\"data\": \"ResponseData1234\"}");
    }

    @PostMapping(path = "/uncompressed-bad-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uncompressedBadRequestEndpoint() {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .header("X-CustomHeader", "CustomHeaderValue")
                             .body("{\"data\": \"ResponseData1234\"}");
    }

    @GetMapping(path = "/gzipped-ok", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> gzippedExample() {

        return ResponseEntity.status(HttpStatus.OK)
                             .header("content-encoding", "gzip")
                             .body(gzippedBytes);

    }

    @GetMapping(path = "/gzipped-bad-request", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> gzippedBadRequestExample() {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .header("content-encoding", "gzip")
                             .body(gzippedBytes);

    }

    @GetMapping(path = "/gzipped-fake", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> gzippedFakeExample() {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .header("content-encoding", "gzip")
                             .body("not gzipped!".getBytes());

    }
}
