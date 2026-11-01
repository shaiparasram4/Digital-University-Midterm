package Business.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Stores unique people and supports lookups for account/profile management.
 * Midterm implementation: Shai Rashada-Parasram. */
public class PersonDirectory {
    private final List<Person> people = new ArrayList<>();

    public Person newPerson(String id) {
        return newPerson(id, id, "unknown@example.edu", Person.Category.STAFF, "Unassigned", "", 0);
    }

    public Person newPerson(String id, String name, String email, Person.Category type,
                            String department, String detail, int credits) {
        if (findPerson(id) != null) {
            throw new IllegalArgumentException("A person with that ID already exists.");
        }
        Person person = new Person(id, name, email, type, department, detail, credits);
        people.add(person);
        return person;
    }

    public Person findPerson(String id) {
        if (id == null) return null;
        for (Person person : people) {
            if (person.isMatch(id)) return person;
        }
        return null;
    }

    public boolean remove(String id) { return people.remove(findPerson(id)); }
    public List<Person> getPeople() { return Collections.unmodifiableList(people); }
}
