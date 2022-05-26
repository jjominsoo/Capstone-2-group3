package youandme.youandme.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import youandme.youandme.domain.Mentor;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MentorRepository {

    private final EntityManager em;

    public void save(Mentor mentor){
        em.persist(mentor);
    }

    public void pass(Mentor mentor){
        em.createQuery("update Mentor m set m.pass=true where m.index = :index")
                .setParameter("index", mentor.getIndex());
    }
    public Mentor findOne(Long id){
        return em.find(Mentor.class, id);
    }

    public List<Mentor> findAll(){
        return em.createQuery("select m from Mentor m", Mentor.class)
                .getResultList();
    }

    public List<Mentor> findID(String ID){
        return em.createQuery("select m from Mentor m where m.ID = :ID", Mentor.class)
                .setParameter("ID", ID)
                .getResultList();
    }

    public List<Mentor> findMatching(String school, String grade, String subject){

        return em.createQuery("select m from Mentor m where m.basicInfo.school = :school and m.basicInfo.grade = :grade and m.basicInfo.subject = :subject", Mentor.class)
                .setParameter("school", school)
                .setParameter("grade", grade)
                .setParameter("subject", subject)
                .getResultList();

    }
}
