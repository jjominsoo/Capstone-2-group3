package youandme.youandme.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.Member;
import youandme.youandme.domain.zzBasicInfo;
import youandme.youandme.domain.zzFiles;
import youandme.youandme.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@RestController
public class uploadFile {

    private final MemberService memberService;

    public uploadFile(MemberService memberService) {
        this.memberService = memberService;
    }

    private String getServerUrl(HttpServletRequest request) {
        return new StringBuffer("http://").append(request.getServerName()).append(":").append(request.getServerPort()).toString();
    }


    @GetMapping
    public ResponseEntity<String> uploadFile(HttpServletRequest request, @RequestParam("ID") String id,@RequestParam("Password") String password, @RequestParam("Name") String name,
                                             @RequestParam("School") String school,@RequestParam("Grade") String grade,@RequestParam("Subject") String subject,
                                             MultipartFile file) throws IllegalStateException, IOException {


        zzBasicInfo basicInfo = new zzBasicInfo(id,password,name,school,grade,subject);
        String serverUrl = getServerUrl(request);
        String filePath =  serverUrl + "/images/";
        String fileName =  UUID.randomUUID().toString()+"_"+file.getOriginalFilename();

        zzFiles files = new zzFiles(file.getOriginalFilename(),fileName,filePath);

        Path savePath = Paths.get("./images/" + fileName);
        file.transferTo(savePath);

        Member member = new Member();
        member.setBasicInfo(basicInfo);
        member.setFiles(files);
        memberService.join(member);


        return new ResponseEntity<>(" ID = " + id + "/ Password = " + password + "/ Name = " + name +
                "/ School = " + school + "/ Grade = " + grade + "/ Subject = " + subject +
                "/ File original name = " + file.getOriginalFilename(), HttpStatus.OK);
    }
}