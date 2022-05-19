package youandme.youandme.service;

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


    private final MentorRepository memberRepository;

    //회원가입
    @Transactional
    public Long join(Mentor member){
        validateDuplicateMentor(member);
        memberRepository.save(member);
        return member.getIndex();
    }

    private void validateDuplicateMentor(Mentor member) {
        List<Mentor> findMentors = memberRepository.findName(member.getID());
        if(!findMentors.isEmpty()){
            throw new IllegalStateException("Already exsisting ID!");
        }
    }

    public List<Mentor> findMentors(){
        return memberRepository.findAll();
    }

    public Mentor findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }


}
