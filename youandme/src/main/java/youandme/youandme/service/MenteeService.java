package youandme.youandme.service;

import youandme.youandme.domain.Mentee;
import youandme.youandme.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenteeService {


    private final MenteeRepository menteeRepository;

    //회원가입
    @Transactional
    public Long join(Mentee mentee){
        validateDuplicateMentee(mentee);
        menteeRepository.save(mentee);
        return mentee.getIndex();
    }

    @Transactional
    public Mentee update(Long mentee_id, Mentee newMentee){
        Mentee findResult = menteeRepository.findOne(mentee_id);
        findResult.setPassword(newMentee.getPassword());
        findResult.setName(newMentee.getName());
        findResult.setSchool(newMentee.getSchool());
        findResult.setGrade(newMentee.getGrade());
        findResult.setSubject(newMentee.getSubject());
        findResult.setProfiles(newMentee.getProfiles());

        return findResult;
    }

    private void validateDuplicateMentee(Mentee mentee) {
        List<Mentee> findMentees = menteeRepository.findID(mentee.getID());
        if(!findMentees.isEmpty()){
            throw new IllegalStateException("Already Exsisting ID!");
        }
    }

    public List<Mentee> findMentees(){
        return menteeRepository.findAll();
    }

    public Mentee findOne(Long mentee_index){
        return menteeRepository.findOne(mentee_index);
    }

    public List<Mentee> findID(String menteeID){
        return menteeRepository.findID(menteeID);
    }

//    public boolean login(Mentee menteeId) {
//        Mentee findMentee = menteeRepository.findOne(mentee.getIndex());
//        System.out.println("findMentee = " + findMentee);
//        if(findMentee == null){
//            System.out.println("mentee is null!");
//            return false;
//        }
//
//        if(!findMentee.getBasicInfo().getPassword().equals(mentee.getBasicInfo().getPassword())){
//            System.out.println("not right password");
//            return false;
//        }
//
//        return true;
//    }

}
