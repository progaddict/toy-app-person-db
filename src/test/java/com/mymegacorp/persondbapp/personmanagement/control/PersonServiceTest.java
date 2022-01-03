package com.mymegacorp.persondbapp.personmanagement.control;

import com.mymegacorp.persondbapp.personmanagement.entity.PersonEntity;
import com.mymegacorp.persondbapp.personmanagement.entity.PersonModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    @Mock
    private Clock clock;

    @Mock
    private PersonRepository personRepository;

    @Spy
    private PersonMapper mapper = Mappers.getMapper(PersonMapper.class);

    @InjectMocks
    private PersonService sut;

    @Test
    void addPerson() {
        // GIVEN
        final Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        Mockito.when(clock.instant()).thenReturn(yesterday);
        final PersonEntity entity = new PersonEntity();
        entity.setId(567L);
        entity.setCreationTimestamp(yesterday);
        Mockito.when(personRepository.save(Mockito.any())).thenReturn(entity);
        final PersonModel model = PersonModel.builder()
                .firstName("John")
                .lastName("Wick")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .streetAndHouseNumber("Teststr. 123")
                .zipCode("QXY-RT15")
                .city("Testtown")
                .build();
        // WHEN
        final PersonModel result = sut.addPerson(model);
        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getCreationTimestamp()).isEqualTo(entity.getCreationTimestamp());
        Mockito.verify(mapper).mapPersonModelToPersonEntity(model, clock);
        Mockito.verify(personRepository).save(Mockito.argThat(e -> {
            assertThat(e).isNotNull();
            assertThat(e.getId()).isNull();
            assertThat(e.getFirstName()).isEqualTo(model.getFirstName());
            assertThat(e.getLastName()).isEqualTo(model.getLastName());
            assertThat(e.getDateOfBirth()).isEqualTo(model.getDateOfBirth());
            assertThat(e.getStreetAndHouseNumber()).isEqualTo(model.getStreetAndHouseNumber());
            assertThat(e.getZipCode()).isEqualTo(model.getZipCode());
            assertThat(e.getCity()).isEqualTo(model.getCity());
            assertThat(e.getCreationTimestamp()).isEqualTo(yesterday);
            return true;
        }));
    }
}