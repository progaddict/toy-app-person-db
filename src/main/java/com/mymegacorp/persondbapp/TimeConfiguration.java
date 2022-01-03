package com.mymegacorp.persondbapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Configuration
public class TimeConfiguration {
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    
    @Bean
    public Clock clock() {
        return new MyClockWithSpecificPrecision(Clock.systemDefaultZone());
    }

    private static class MyClockWithSpecificPrecision extends Clock {
        private final Clock delegate;

        private MyClockWithSpecificPrecision(
                final Clock delegate
        ) {
            this.delegate = Objects.requireNonNull(delegate);
        }

        @Override
        public ZoneId getZone() {
            return delegate.getZone();
        }

        @Override
        public Clock withZone(final ZoneId zone) {
            return delegate.withZone(zone);
        }

        /**
         * Truncate {@link Instant} to seconds
         * thus fixing discrepancy
         * between "Java world" and "SQL world".
         * TIMESTAMP SQL type has default precision
         * of 6 fractional seconds in H2.
         * Other DBs may have other default precisions.
         * The application's premise is that a DB
         * must support at least seconds
         * (without fractional part) as a minimal precision
         * for a TIMESTAMP SQL type.
         */
        @Override
        public Instant instant() {
            // https://stackoverflow.com/a/69582204
            return delegate.instant()
                    .plusNanos(500)
                    .truncatedTo(ChronoUnit.SECONDS);
        }
    }
}
