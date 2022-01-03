package com.mymegacorp.persondbapp.personmanagement.control;

import com.mymegacorp.persondbapp.personmanagement.entity.PersonEntity;
import com.mymegacorp.persondbapp.personmanagement.entity.PersonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@lombok.RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PersonService {
    private final Clock clock;
    private final PersonRepository personRepository;
    private final PersonMapper mapper;

    @Transactional(readOnly = true)
    public List<PersonModel> findAll() {
        return personRepository.findAll()
                .stream()
                .map(mapper::mapPersonEntityToPersonModel)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PersonModel> findPerson(final long id) {
        return personRepository.findById(id)
                .map(mapper::mapPersonEntityToPersonModel);
    }

    @Transactional
    public List<PersonModel> addPersons(final Collection<PersonModel> persons) {
        final List<PersonEntity> entities = persons.stream()
                .map(model -> mapper.mapPersonModelToPersonEntity(model, clock))
                .toList();
        return personRepository.saveAll(entities)
                .stream()
                .map(mapper::mapPersonEntityToPersonModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public PersonModel addPerson(final PersonModel model) {
        final PersonEntity entity = personRepository.save(mapper.mapPersonModelToPersonEntity(model, clock));
        return mapper.mapPersonEntityToPersonModel(entity);
    }

    @Transactional
    public boolean deletePerson(final long id) {
        return personRepository.deleteAllHavingId(id) > 0;
    }
}
