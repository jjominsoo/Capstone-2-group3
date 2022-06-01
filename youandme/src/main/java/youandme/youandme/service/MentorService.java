package youandme.youandme.service;

import org.springframework.data.jpa.repository.Modifying;
import youandme.youandme.domain.Mentor;
import youandme.youandme.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MentorService {


    private final MentorRepository mentorRepository;

    //회원가입
    @Transactional
    public Long join(Mentor mentor){
        validateDuplicateMentor(mentor);
        mentorRepository.save(mentor);
        return mentor.getIndex();
    }

    @Transactional
    public Mentor update(Long mentee_id, Mentor newMentor){
        Mentor findResult = mentorRepository.findOne(mentee_id);
        findResult.setPassword(newMentor.getPassword());
        findResult.setName(newMentor.getName());
        findResult.setSchool(newMentor.getSchool());
        findResult.setGrade(newMentor.getGrade());
        findResult.setSubject(newMentor.getSubject());
        findResult.setCompany(newMentor.getCompany());
        findResult.setProfiles(newMentor.getProfiles());
        findResult.setShortIntroduce(newMentor.getShortIntroduce());
        findResult.setLongIntroduce(newMentor.getLongIntroduce());
        return findResult;
    }

    @Modifying
    @Transactional
    public void resave(Mentor mentor){
        mentorRepository.pass(mentor);
    }


    private void validateDuplicateMentor(Mentor mentor) {
        List<Mentor> findMentors = mentorRepository.findID(mentor.getID());
        System.out.println("findMentors = " + findMentors);
        if(!findMentors.isEmpty()){
            throw new IllegalStateException("Already existing ID!");
        }
    }

    public List<Mentor> findMentors(){
        return mentorRepository.findAll();
    }

    public Mentor findOne(Long mentorId){
        return mentorRepository.findOne(mentorId);
    }

    public List<Mentor> findID(String mentorID){
        return mentorRepository.findID(mentorID);
    }

    public List<Mentor> findMatching(String school, Float grade, String subject){
        return mentorRepository.findMatching(school, grade, subject);
    }

}
