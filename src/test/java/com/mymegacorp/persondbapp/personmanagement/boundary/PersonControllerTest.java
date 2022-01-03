package com.mymegacorp.persondbapp.personmanagement.boundary;

import com.mymegacorp.persondbapp.personmanagement.entity.PersonModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://127.0.0.1:" + port;
    }

    /**
     * Example of a simple integration test.
     * For more complex cases I'd use
     * <a href="https://citrusframework.org/">Citrus Framework</a>.
     */
    @Test
    void simpleIntegrationTest() {
        // at the beginning there's nothing in there
        assertThat(getAllPersons()).isEmpty();

        // uplaod CSV
        final List<PersonModel> csvTestPersons = uploadCsv("./test-person-data.csv");
        assertThat(csvTestPersons).hasSize(3);
        assertThat(
                csvTestPersons.stream().map(PersonModel::getFirstName).collect(Collectors.toSet())
        ).isEqualTo(Set.of("John", "Jane", "Jessica"));

        // re-read from DB and check persons have been indeed saved
        assertThat(
                getAllPersons().stream().map(PersonModel::getFirstName).collect(Collectors.toSet())
        ).isEqualTo(Set.of("John", "Jane", "Jessica"));

        // test get by ID
        final PersonModel csvJohn = csvTestPersons.get(0);
        final PersonModel john = restTemplate.getForObject(
                baseUrl + PersonController.URL_PREFIX + "/" + csvJohn.getId(),
                PersonModel.class
        );
        // this works because lombok generates "equals()"
        assertThat(john).isEqualTo(csvJohn);

        // POST a new person
        final PersonModel heidi = restTemplate.postForObject(
                baseUrl + PersonController.URL_PREFIX,
                PersonModel.builder()
                        .firstName("Heidi")
                        .lastName("Smith")
                        .streetAndHouseNumber("Great Ave. 123")
                        .zipCode("X-54321")
                        .city("City 17")
                        .dateOfBirth(LocalDate.of(1995, 5, 21))
                        .build(),
                PersonModel.class
        );
        assertThat(heidi).isNotNull();
        assertThat(heidi.getId()).isNotNull();
        assertThat(heidi.getId()).isPositive();
        assertThat(heidi.getFirstName()).isEqualTo("Heidi");
        assertThat(heidi.getLastName()).isEqualTo("Smith");
        assertThat(heidi.getCreationTimestamp()).isNotNull();

        // re-read from DB and check
        final List<PersonModel> csvPersonsAndHeidi = getAllPersons();
        assertThat(
                csvPersonsAndHeidi.stream().map(PersonModel::getFirstName).collect(Collectors.toSet())
        ).isEqualTo(Set.of("John", "Jane", "Jessica", "Heidi"));

        // delete persons
        csvPersonsAndHeidi.stream()
                .map(PersonModel::getId)
                .forEach(id -> restTemplate.delete(baseUrl + PersonController.URL_PREFIX + "/" + id));
        // re-read and confirm that DB is empty
        assertThat(getAllPersons()).isEmpty();
    }

    private List<PersonModel> uploadCsv(final String testCsvFilePath) {
        final LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>(
                Map.of("file", List.of(new ClassPathResource(testCsvFilePath)))
        );
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        final ResponseEntity<PersonModel[]> response = restTemplate.exchange(
                baseUrl + PersonController.URL_PREFIX + "/csv",
                HttpMethod.POST,
                request,
                PersonModel[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final PersonModel[] responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).isNotEmpty();
        return Arrays.stream(responseBody).toList();
    }

    private List<PersonModel> getAllPersons() {
        final PersonModel[] response = restTemplate.getForObject(
                baseUrl + PersonController.URL_PREFIX,
                PersonModel[].class
        );
        return Arrays.stream(response).toList();
    }
}