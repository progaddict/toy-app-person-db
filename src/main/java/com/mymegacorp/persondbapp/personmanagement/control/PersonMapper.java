package com.mymegacorp.persondbapp.personmanagement.control;

import com.mymegacorp.persondbapp.personmanagement.entity.PersonEntity;
import com.mymegacorp.persondbapp.personmanagement.entity.PersonModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Clock;

@Mapper
public interface PersonMapper {
    PersonModel mapPersonEntityToPersonModel(final PersonEntity model);

    @Mapping(
            target = "creationTimestamp",
            expression = "java( clock.instant() )"
    )
    PersonEntity mapPersonModelToPersonEntity(final PersonModel model, final Clock clock);
}
