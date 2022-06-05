package de.htwberlin.webtech.api.person;

import de.htwberlin.webtech.api.person.Gender;
import de.htwberlin.webtech.api.person.Person;
import de.htwberlin.webtech.api.person.PersonEntity;
import de.htwberlin.webtech.api.person.PersonRepository;
import de.htwberlin.webtech.api.person.PersonManipulationRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        List<PersonEntity> persons = personRepository.findAll();
        return persons.stream()
                .map(this::fromPersonEntityToPerson)
                .collect(Collectors.toList());
    }

    public Person findById(Long id) {
        var personEntity = personRepository.findById(id);
        return personEntity.map(this::fromPersonEntityToPerson).orElse(null);
    }

    public Person create(PersonManipulationRequest request) {
        var gender = Gender.valueOf(request.getGender());
        var personEntity = new PersonEntity(request.getFirstName(), request.getLastName(), request.isVaccinated(), gender);
        personEntity = personRepository.save(personEntity);
        return fromPersonEntityToPerson(personEntity);
    }

    public Person update(Long id, PersonManipulationRequest request) {
        var personEntityOptional = personRepository.findById(id);
        if (personEntityOptional.isEmpty()) return null;

        var personEntity = personEntityOptional.get();
        personEntity.setFirstName(request.getFirstName());
        personEntity.setLastName(request.getLastName());
        personEntity.setGender(Gender.valueOf(request.getGender()));
        personEntity.setVaccinated(request.isVaccinated());
        personEntity = personRepository.save(personEntity);

        return fromPersonEntityToPerson(personEntity);
    }

    public boolean deleteById(Long id) {
        if (!personRepository.existsById(id)) return false;
        personRepository.deleteById(id);
        return true;
    }

    private Person fromPersonEntityToPerson(PersonEntity pe) {
        var gender = pe.getGender() != null ? pe.getGender().name() : Gender.UNKNOWN.name(); //.name() change enum to String
        return new Person(
                pe.getId(),
                pe.getFirstName(),
                pe.getLastName(),
                gender,
                pe.isVaccinated()
        );
    }
}