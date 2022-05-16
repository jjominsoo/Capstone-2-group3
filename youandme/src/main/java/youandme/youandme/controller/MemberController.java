package youandme.youandme.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.Member;
import youandme.youandme.domain.zzBasicInfo;
import youandme.youandme.domain.zzFiles;
import youandme.youandme.exception.NotEnoughStockException;
import youandme.youandme.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MemberController {

    private final MemberService memberService;


    private String getServerUrl(HttpServletRequest request) {
        return new StringBuffer("http://").append(request.getServerName()).append(":").append(request.getServerPort()).toString();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";

    }

    @CrossOrigin(origins = "*")
    @PostMapping("/members/new")
    public String create(HttpServletRequest request, @Valid MemberForm memberForm, BindingResult result, @RequestParam("uploadfile") MultipartFile multipartFile) throws IOException {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }


        zzBasicInfo basicInfo = new zzBasicInfo(memberForm.getID(), memberForm.getPassword(), memberForm.getRname(), memberForm.getSchool(), memberForm.getGrade(), memberForm.getSubject());//, memberForm.getFile());
        String serverUrl = getServerUrl(request);
        String filePath =  serverUrl + "/images/";
        String fileName =  UUID.randomUUID().toString()+"_"+multipartFile.getOriginalFilename();

        zzFiles files = new zzFiles(multipartFile.getOriginalFilename(), fileName, filePath);


        Path savePath = Paths.get("./images/" + fileName);
        multipartFile.transferTo(savePath);


        Member member = new Member();
        member.setName(memberForm.getID());
        member.setBasicInfo(basicInfo);
        member.setFiles(files);
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
