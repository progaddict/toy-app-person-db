package com.mymegacorp.persondbapp.personmanagement.control;

import com.mymegacorp.persondbapp.TimeConfiguration;
import com.mymegacorp.persondbapp.personmanagement.entity.PersonModel;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class CsvPersonParser implements PersonParser<InputStream> {
    private static final String COL_FIRST_NAME = "firstName";
    private static final String COL_LAST_NAME = "lastName";
    private static final String COL_STREET_AND_HOUSE_NUMBER = "streetAndHouseNumber";
    private static final String COL_ZIPCODE = "zipCode";
    private static final String COL_CITY = "city";
    private static final String COL_DATE_OF_BIRTH = "dateOfBirth";
    private static final Set<String> COLS = Set.of(
            COL_FIRST_NAME,
            COL_LAST_NAME,
            COL_STREET_AND_HOUSE_NUMBER,
            COL_ZIPCODE,
            COL_CITY,
            COL_DATE_OF_BIRTH
    );
    private static final int COL_COUNT = COLS.size();

    @Override
    public List<PersonModel> apply(final InputStream inputStream) {
        try (
                final InputStreamReader isr = new InputStreamReader(inputStream);
                final BufferedReader br = new BufferedReader(isr);
                final CSVReader csvReader = new CSVReader(br)
        ) {
            final String[] header = csvReader.readNext();
            if (Objects.isNull(header)) {
                throw new RuntimeException("no header");
            }
            if (header.length != COL_COUNT) {
                throw new RuntimeException("wrong header: " + String.join(",", header));
            }
            final Set<String> missingCols = new HashSet<>(COLS);
            missingCols.removeAll(Set.of(header));
            if (!missingCols.isEmpty()) {
                throw new RuntimeException("missing columns: " + String.join(",", missingCols));
            }
            final List<PersonModel> result = new ArrayList<>();
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                final PersonModel.PersonModelBuilder b = PersonModel.builder();
                for (int i = 0; i < row.length; i++) {
                    final String colValue = row[i];
                    final String colName = header[i];
                    switch (colName) {
                        case COL_FIRST_NAME -> b.firstName(colValue);
                        case COL_LAST_NAME -> b.lastName(colValue);
                        case COL_STREET_AND_HOUSE_NUMBER -> b.streetAndHouseNumber(colValue);
                        case COL_ZIPCODE -> b.zipCode(colValue);
                        case COL_CITY -> b.city(colValue);
                        case COL_DATE_OF_BIRTH -> b.dateOfBirth(LocalDate.parse(colValue, TimeConfiguration.LOCAL_DATE_FORMATTER));
                    }
                }
                result.add(b.build());
            }
            return result;
        } catch (final IOException | CsvValidationException error) {
            LOG.error("failed to read CSV", error);
            throw new RuntimeException(error);
        }
    }
}
