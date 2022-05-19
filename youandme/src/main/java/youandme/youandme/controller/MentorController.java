package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.*;
import youandme.youandme.service.MentorService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;


    private String getServerUrl(HttpServletRequest request) {
        return new StringBuffer("http://").append(request.getServerName()).append(":").append(request.getServerPort()).toString();
    }

    @GetMapping(value = "/mentors/new")
    public String createForm(Model model) {
        model.addAttribute("mentorForm", new MentorForm());
        return "mentors/createMentorForm";

    }

    @PostMapping("/mentors/new")
    public String create(HttpServletRequest request, @Valid MentorForm mentorForm, BindingResult result, @RequestParam("uploadProfile") MultipartFile profile, @RequestParam("uploadGraduationFile") MultipartFile graduation,  @RequestParam("uploadCompanyFile") MultipartFile company) throws IOException {
        if (result.hasErrors()) {
            return "mentors/createMentorForm";
        }

        Mentor mentor = new Mentor();

        BasicInfo basicInfo = new BasicInfo(mentorForm.getID(), mentorForm.getPassword(), mentorForm.getSchool(), mentorForm.getGrade(), mentorForm.getSubject());
        mentor.setName(mentorForm.getName());
        mentor.setCompany(mentorForm.getCompany());
        String serverUrl = getServerUrl(request);

        String ProfilePath =  serverUrl + "/images/";// + mentorForm.getID() +"/";
        String ProfileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
        Profiles profiles = new Profiles(profile.getOriginalFilename(), ProfileName, ProfilePath);

        Path saveProfilePath = Paths.get("./images/" + ProfileName);
        profile.transferTo(saveProfilePath);


        String GraduationPath =  serverUrl + "/graduation_certification/";//+ mentorForm.getID() +"/";
        String GraduationName =  UUID.randomUUID().toString()+"_"+graduation.getOriginalFilename();
        GraduationFiles graduationFiles  = new GraduationFiles(graduation.getOriginalFilename(), GraduationName, GraduationPath);

        Path saveGraduationPath = Paths.get("./graduation_certification/" + GraduationName);
        graduation.transferTo(saveGraduationPath);

        String CompanyPath =  serverUrl + "/company_certification/";//+ mentorForm.getID() +"/";
        String CompanyName =  UUID.randomUUID().toString()+"_"+company.getOriginalFilename();
        CompanyFiles companyFiles = new CompanyFiles(company.getOriginalFilename(), CompanyName, CompanyPath);

        Path saveCompanyPath = Paths.get("./company_certification/" + CompanyName);
        company.transferTo(saveCompanyPath);






//        List<String >fileOriNameList = new ArrayList<>();
//        List<String >fileNameList = new ArrayList<>();
//        List<String >filePathList = new ArrayList<>();

//        for(MultipartFile file : multipartFile2) {
//            System.out.println("file = " + file.getOriginalFilename());
//            String fileName2 = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//            //zzFiles2 files2 = new zzFilesList(file.getOriginalFilename(), fileName2, filePath2);
//            fileOriNameList.add(file.getOriginalFilename());
//            fileNameList.add(fileName2);
//            filePathList.add(filePath2);
//
//            //fileList2 = new zzFileList(file.getOriginalFilename(),fileName2,filePath2);
//            Path savePath2 = Paths.get("./certifications/" + fileName2);
//            file.transferTo(savePath2);
//            //mentor.setFiles2(files2);
//            //mentor.setFileList(fileList2);
//            //fileList2.show();
//        }
//        zzFileList fileList2 = new zzFileList(fileOriNameList,fileNameList,filePathList);
//        fileList2.show();
//        fileList2.getFileName3();

            //이러면 마지막꺼만 저장되는거자나
//        System.out.println("file = " + multipartFile2.get(0).getOriginalFilename());
//        String fileName2 =  UUID.randomUUID().toString()+"_"+multipartFile2.get(0).getOriginalFilename();
//        zzFiles2 files2 = new zzFiles2(multipartFile2.get(0).getOriginalFilename(),fileName2,filePath2);
//        Path savePath2 = Paths.get("./certifications/" + fileName2);
//        multipartFile2.get(0).transferTo(savePath2);
//        mentor.setFiles2(files2);


//        mentor.setName(mentorForm.getID());
        mentor.setBasicInfo(basicInfo);
        mentor.setProfiles(profiles);
        mentor.setGraduationFiles(graduationFiles);
        mentor.setCompanyFiles(companyFiles);


//        mentor.setFileList(fileList2);
        mentorService.join(mentor);

        return "redirect:/";

    }

    @GetMapping(value = "/mentors")
    public String list(Model model){
        List<Mentor> mentors = mentorService.findMentors();
        model.addAttribute("mentors",mentors);
        return "mentors/mentorList";
    }

    @ResponseBody
    @GetMapping(value = "/mentorList")
    public List<MobileMentor> mentorList(Model model){
        List<Mentor> members = mentorService.findMentors();

        List<MobileMentor> mobileMemberList = new ArrayList<>();
        for (Mentor member : members) {
            MobileMentor mobileMember = new MobileMentor();
            mobileMember.setIndex(member.getIndex());
            mobileMember.setID(member.getBasicInfo().getID());
            mobileMember.setPassword(member.getBasicInfo().getPassword());
            mobileMember.setName(member.getName());
            mobileMember.setSchool(member.getBasicInfo().getSchool());
            mobileMember.setGrade(member.getBasicInfo().getGrade());
            mobileMember.setSubject(member.getBasicInfo().getSubject());
            mobileMember.setProfileFilePath(member.getProfiles().getProfilePath()+member.getProfiles().getProfileName());
            mobileMember.setGraduationFilePath(member.getGraduationFiles().getGraduationFilePath()+member.getGraduationFiles().getGraduationFileName());
            mobileMember.setCompanyFilePath(member.getCompanyFiles().getCompanyFilePath()+member.getCompanyFiles().getCompanyFileName());
            mobileMemberList.add(mobileMember);
        }
        return mobileMemberList;
    }
}
