package youandme.youandme.repository;

import youandme.youandme.domain.Mentee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenteeRepository {

    private final EntityManager em;

    public void save(Mentee mentee){
        em.persist(mentee);
    }

    public Mentee findOne(Long id){
        return em.find(Mentee.class, id);
    }

    public List<Mentee> findAll(){
       return em.createQuery("select m from Mentee m", Mentee.class)
                .getResultList();
    }

    public List<Mentee> findName(String name){
        return em.createQuery("select m from Mentee m where m.name = :name", Mentee.class)
                .setParameter("name", name)
                .getResultList();
    }
}
