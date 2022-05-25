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

    public Mentee findOne(Long index){
        System.out.println("em.find(Mentee.class, index) = " + em.find(Mentee.class, index));
        return em.find(Mentee.class, index);
    }

    public List<Mentee> findAll(){
       return em.createQuery("select m from Mentee m", Mentee.class)
                .getResultList();
    }

    public List<Mentee> findID(String ID){
        return em.createQuery("select m from Mentee m where m.ID = :ID", Mentee.class)
                .setParameter("ID", ID)
                .getResultList();
    }
}
