package com.mymegacorp.persondbapp.personmanagement.boundary;

import com.mymegacorp.persondbapp.personmanagement.control.PersonParser;
import com.mymegacorp.persondbapp.personmanagement.control.PersonService;
import com.mymegacorp.persondbapp.personmanagement.entity.PersonModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(PersonController.URL_PREFIX)
@lombok.RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PersonController {
    public static final String URL_PREFIX = "/api/v1/person";

    private final PersonParser<InputStream> csvPersonParser;
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonModel>> findAll() {
        return ResponseEntity.ok(personService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonModel> findPerson(
            @NotNull @PathVariable("id") final Long id
    ) {
        return personService.findPerson(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonModel> addPerson(
            @NotNull @Valid @RequestBody final PersonModel person
    ) {
        return ResponseEntity.ok(personService.addPerson(person));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(
            @NotNull @PathVariable("id") final Long id
    ) {
        if (personService.deletePerson(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/csv")
    public ResponseEntity<List<PersonModel>> uploadCsv(
            final @NotNull @RequestParam("file") MultipartFile file
    ) {
        LOG.debug("received CSV");
        try {
            final List<PersonModel> persons = csvPersonParser.apply(file.getInputStream());
            LOG.debug("parsed CSV, saving: {}", persons);
            return ResponseEntity.ok(personService.addPersons(persons));
        } catch (final IOException error) {
            LOG.error("failed to read CSV file", error);
            return ResponseEntity.internalServerError().build();
        }
    }
}
