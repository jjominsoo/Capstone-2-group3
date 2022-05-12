package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.temp.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.zzBasicInfo;
import jpabook.jpashop.domain.zzFiles;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";

    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result){//, @RequestParam MultipartFile files){
        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        //Address adderss = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        zzBasicInfo basicInfo = new zzBasicInfo(memberForm.getID(), memberForm.getPassword(),memberForm.getRname(),memberForm.getSchool(), memberForm.getGrade(), memberForm.getSubject());//, memberForm.getFile());

        zzFiles file = new zzFiles();
        // 이거 protected 에서 public 으로 바뀌어서 그럼럼

        Member member = new Member();
        member.setName(memberForm.getID());
        //member.setAddress(adderss);
        member.setBasicInfo(basicInfo);
        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping(value = "/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);
        return "members/memberList";
    }
}
