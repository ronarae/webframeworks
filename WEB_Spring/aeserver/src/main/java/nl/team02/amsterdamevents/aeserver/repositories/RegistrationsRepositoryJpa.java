package nl.team02.amsterdamevents.aeserver.repositories;

import nl.team02.amsterdamevents.aeserver.models.AEvent;
import nl.team02.amsterdamevents.aeserver.models.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@Primary
public class RegistrationsRepositoryJpa {

    @Autowired
    private EntityManager entityManager;

    public Registration save(Registration registration) {
        return entityManager.merge(registration);
    }

    public List<Registration> findAll() {
        TypedQuery<Registration> query = this.entityManager.createQuery(
                "select e from Registration e", Registration.class);
        return query.getResultList();
    }

    public Registration findById(long id) {
        return entityManager.find(Registration.class, id);
    }

    public boolean deleteById(long id) {
        return false;
    }
}

