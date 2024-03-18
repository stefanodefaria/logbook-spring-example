package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SampleController {

    @PostMapping(path = "/test/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sampleEndpoint() {

        return ResponseEntity.status(HttpStatus.OK)
                             .header("X-CustomHeader", "CustomHeaderValue")
                             .body("{\"data\": \"ResponseData1234\"}");
    }
}
