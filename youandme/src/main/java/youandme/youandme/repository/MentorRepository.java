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

    public List<Mentor> findMatching(String school, Float grade, String subject){

        return em.createQuery("select m from Mentor m where m.school = :school and m.grade between :grade1 and :grade2 and m.subject = :subject", Mentor.class)
                .setParameter("school", school)
                .setParameter("grade1", grade -1)
                .setParameter("grade2", grade+1)

                //소숫점 단위로 영역화가 불가능? grade-0.5하면 안됨

                //앱에서 학교 : 서울대 / 연세대 / 고려대 / 중앙대 .. 이런식으로 리스트를 주고 거기서 선택하도록 하고
                //과목도 마찬가지로 : 건축학과 / 간호학과 / 컴퓨터공학과 .. 이런식으로 하면 찾기 편할듯
               .setParameter("subject", subject)
                .getResultList();

    }
}
