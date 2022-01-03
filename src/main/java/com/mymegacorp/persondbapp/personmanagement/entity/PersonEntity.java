package com.mymegacorp.persondbapp.personmanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "t_person")
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class PersonEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    @NotNull
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NotNull
    @NotBlank
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotNull
    @NotBlank
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    @NotNull
    private LocalDate dateOfBirth;

    @Column(name = "street_and_house_number", nullable = false)
    @NotNull
    @NotBlank
    private String streetAndHouseNumber;

    @Column(name = "zip_code", nullable = false)
    @NotNull
    @NotBlank
    private String zipCode;

    @Column(name = "city", nullable = false)
    @NotNull
    @NotBlank
    private String city;

    @Column(name = "creation_timestamp", nullable = false)
    @NotNull
    private Instant creationTimestamp;
}
