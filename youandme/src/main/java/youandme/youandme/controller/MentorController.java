package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.*;
import youandme.youandme.service.MentorService;

import javax.persistence.EntityManager;
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

        BasicInfo basicInfo = new BasicInfo( mentorForm.getPassword(), mentorForm.getSchool(), mentorForm.getGrade(), mentorForm.getSubject());
        mentor.setName(mentorForm.getName());
        mentor.setID(mentorForm.getID());
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
        List<Mentor> mentors = mentorService.findMentors();

        List<MobileMentor> mobileMemberList = new ArrayList<>();
        for (Mentor mentor : mentors) {
            if(mentor.isPass()) {
                MobileMentor mobileMember = new MobileMentor();
                mobileMember.setIndex(mentor.getIndex());
                mobileMember.setID(mentor.getID());
                mobileMember.setPassword(mentor.getBasicInfo().getPassword());
                mobileMember.setName(mentor.getName());
                mobileMember.setSchool(mentor.getBasicInfo().getSchool());
                mobileMember.setGrade(mentor.getBasicInfo().getGrade());
                mobileMember.setSubject(mentor.getBasicInfo().getSubject());
                mobileMember.setProfileFilePath(mentor.getProfiles().getProfilePath() + mentor.getProfiles().getProfileName());
                mobileMember.setGraduationFilePath(mentor.getGraduationFiles().getGraduationFilePath() + mentor.getGraduationFiles().getGraduationFileName());
                mobileMember.setCompanyFilePath(mentor.getCompanyFiles().getCompanyFilePath() + mentor.getCompanyFiles().getCompanyFileName());
                mobileMemberList.add(mobileMember);
            }
        }
        return mobileMemberList;
    }

    @GetMapping(value = "mentors/{mentor_id}/edit")
    public String pass(@PathVariable("mentor_id") Long mentorId){
        Mentor mentor = mentorService.findOne(mentorId);
        if(!mentor.isPass()){
            mentor.setPass(true);
        }
        else{
            mentor.setPass(false);
        }
        mentorService.resave(mentor);
        return "redirect:/mentors";
    }

    @ResponseBody
    @PostMapping("/mentors/join")
    public MentorForm mentorJoin(HttpServletRequest request, @Valid MentorJoinForm mentorJoinForm ){
        MentorForm mentorForm = new MentorForm();
        List<Mentor> mentors = mentorService.findID(mentorJoinForm.getID());

        if(mentors.isEmpty()){
//            return false;
            System.out.println("no such ID");
            mentorForm.setStatus(false);
            return mentorForm;

        }
        else if(!mentors.get(0).getBasicInfo().getPassword().equals(mentorJoinForm.getPassword())){
//            return false;
            System.out.println("wrong password");
            mentorForm.setStatus(false);
            return mentorForm;
        }

        mentorForm.setID(mentors.get(0).getID());
        mentorForm.setPassword(mentors.get(0).getBasicInfo().getPassword());
        mentorForm.setName(mentors.get(0).getName());
        mentorForm.setGrade(mentors.get(0).getBasicInfo().getGrade());
        mentorForm.setSchool(mentors.get(0).getBasicInfo().getSchool());
        mentorForm.setSubject(mentors.get(0).getBasicInfo().getSubject());
        mentorForm.setProfileName(mentors.get(0).getProfiles().getProfileName());
        mentorForm.setProfilePath(mentors.get(0).getProfiles().getProfilePath());
//        mentorForm.setProfilePath(mentors.get(0).getGraduationFiles().getGraduationFileName());
//        mentorForm.setProfilePath(mentors.get(0).getGraduationFiles().getGraduationFilePath());
//        mentorForm.setProfilePath(mentors.get(0).getCompanyFiles().getCompanyFileName());
//        mentorForm.setProfilePath(mentors.get(0).getCompanyFiles().getCompanyFilePath());
        mentorForm.setStatus(true);
        mentorForm.setPass(true);

        return mentorForm;
    }
}
