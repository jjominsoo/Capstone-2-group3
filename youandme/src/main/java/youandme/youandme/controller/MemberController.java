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

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";

    }

    @CrossOrigin(origins = "*")
    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result, @RequestParam("uploadfile") MultipartFile multipartFile) throws IOException {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        zzBasicInfo basicInfo = new zzBasicInfo(memberForm.getID(), memberForm.getPassword(), memberForm.getRname(), memberForm.getSchool(), memberForm.getGrade(), memberForm.getSubject());//, memberForm.getFile());

        String filePath =  "C:\\zzImages\\";
        String fileName =  UUID.randomUUID().toString()+"_"+multipartFile.getOriginalFilename();

//        HttpHeaders header = new HttpHeaders();
//        Path imgPath = null;
//        imgPath = Paths.get(filePath + fileName);
//        header.add("Content-Type", multipartFile.getContentType());



//        InputStream inputStream = multipartFile.getInputStream();
//        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
//        int len = 0;
//        byte[] buf = new byte[1024];
//        while( (len = inputStream.read(buf)) != 0){
//            byteOutStream.write(buf,0,len);
//        }
//
//        byte[] fileArray = byteOutStream.toByteArray();
//        imageString[i] = new String(Base64.encodeBase64(fileArray));



        zzFiles files = new zzFiles(multipartFile.getOriginalFilename(), fileName, filePath);



        Path savePath = Paths.get(filePath + fileName);
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
