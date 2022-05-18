package youandme.youandme.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import youandme.youandme.domain.Mentor;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MentorRepository {

    private final EntityManager em;

    public void save(Mentor mentor){
        em.persist(mentor);
    }

    public Mentor findOne(Long id){
        return em.find(Mentor.class, id);
    }

    public List<Mentor> findAll(){
        return em.createQuery("select m from Mentor m", Mentor.class)
                .getResultList();
    }

    public List<Mentor> findName(String name){
        return em.createQuery("select m from Mentor m where m.name = :name", Mentor.class)
                .setParameter("name", name)
                .getResultList();
    }
}
