package youandme.youandme.service;

import youandme.youandme.domain.Mentee;
import youandme.youandme.repository.MenteeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MenteeService memberService;
    @Autowired
    MenteeRepository memberRepository;
    @Autowired EntityManager em;


    @Test
    public void 회원가입() throws Exception{
        //giver
        Mentee member = new Mentee();
        member.setName("MinSoo");

        //when
        Long savedId = memberService.join(member);

        //then
        //em.flush();
        assertEquals(member,memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복예외() throws Exception{
        //given
        Mentee member1 = new Mentee();
        member1.setName("what");

        Mentee member2 = new Mentee();
        member2.setName("what");

        //when
        memberService.join(member1);
        memberService.join(member2);

        //then
        fail("예외가 발생해야합니다");
    }

}