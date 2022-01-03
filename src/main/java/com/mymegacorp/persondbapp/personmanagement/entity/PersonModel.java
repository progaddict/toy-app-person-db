package com.mymegacorp.persondbapp.personmanagement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mymegacorp.persondbapp.TimeConfiguration;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;

@lombok.Data
@lombok.Builder
@Jacksonized
public class PersonModel {
    private Long id;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @NotBlank
    private String streetAndHouseNumber;

    @NotNull
    @NotBlank
    private String zipCode;

    @NotNull
    @NotBlank
    private String city;

    @NotNull
    @JsonFormat(pattern = TimeConfiguration.DATE_PATTERN)
    @DateTimeFormat(pattern = TimeConfiguration.DATE_PATTERN)
    private LocalDate dateOfBirth;

    private Instant creationTimestamp;
}
