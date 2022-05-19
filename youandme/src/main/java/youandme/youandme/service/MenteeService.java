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

    private void validateDuplicateMentee(Mentee mentee) {
        List<Mentee> findMentees = menteeRepository.findName(mentee.getBasicInfo().getID());
        if(!findMentees.isEmpty()){
            throw new IllegalStateException("Already Exsisting ID!");
        }
    }

    public List<Mentee> findMentees(){
        return menteeRepository.findAll();
    }

    public Mentee findOne(Long menteeId){
        return menteeRepository.findOne(menteeId);
    }


}
