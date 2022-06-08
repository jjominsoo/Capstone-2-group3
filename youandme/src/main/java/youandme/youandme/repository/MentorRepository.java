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

    public List<Mentor> findMatching(String school, Float grade, String subject){
        float grade1 = (float) (grade - 0.5);
        float grade2 = (float) (grade + 0.5);
        return em.createQuery("select m from Mentor m where m.school = :school and m.grade between :grade1 and :grade2 and m.subject = :subject", Mentor.class)
                .setParameter("school", school)
                .setParameter("grade1", grade1)
                .setParameter("grade2", grade2)
                .setParameter("subject", subject)
                .getResultList();

    }

    public List<Mentor> findSchoolMatching(String school){
        return em.createQuery("select m from Mentor m where m.school = :school", Mentor.class)
                .setParameter("school", school)
                .getResultList();

    }

    public List<Mentor> findGradeMatching(Float grade){
        float grade1 = (float) (grade - 0.5);
        float grade2 = (float) (grade + 0.5);
        return em.createQuery("select m from Mentor m where m.grade between :grade1 and :grade2", Mentor.class)
                .setParameter("grade1", grade1)
                .setParameter("grade2", grade2)
                .getResultList();

    }

    public List<Mentor> findSubjectMatching(String subject){
        return em.createQuery("select m from Mentor m where m.subject = :subject", Mentor.class)
                .setParameter("subject", subject)
                .getResultList();

    }

}
