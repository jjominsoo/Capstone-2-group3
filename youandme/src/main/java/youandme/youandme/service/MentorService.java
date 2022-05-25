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

    public List<Mentor> findID(String menteeID){
        return mentorRepository.findID(menteeID);
    }
}
