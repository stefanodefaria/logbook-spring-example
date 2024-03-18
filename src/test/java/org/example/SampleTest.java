package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = Main.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleTest {
    @Autowired
    public TestRestTemplate restTemplate;


    @Test
    void test() throws URISyntaxException {
        RequestEntity<String> request = RequestEntity
                .post(new URI("/test/12345"))
                .accept(MediaType.ALL)
                .body("RequestBody1234");

        ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("data")).isEqualTo("ResponseData1234");
        assertThat(response.getHeaders().get("X-CustomHeader")).isNotNull();
        assertThat(response.getHeaders().get("X-CustomHeader").get(0)).isEqualTo("CustomHeaderValue");
    }

}
