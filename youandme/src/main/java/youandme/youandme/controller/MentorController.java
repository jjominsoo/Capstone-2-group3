package youandme.youandme.controller;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
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

        mentor.setName(mentorForm.getName());
        mentor.setID(mentorForm.getID());
        mentor.setCompany(mentorForm.getCompany());
        mentor.setBasicInfo(basicInfo);
        mentor.setProfiles(profiles);
        mentor.setGraduationFiles(graduationFiles);
        mentor.setCompanyFiles(companyFiles);

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
                mobileMember.setCompany(mentor.getCompany());
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
    public MobileMentorJoinForm mentorJoin(HttpServletRequest request, @Valid MentorJoinForm mentorJoinForm ){
        MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
        List<Mentor> mentors = mentorService.findID(mentorJoinForm.getID());

        if(mentors.isEmpty()){
            System.out.println("no such ID");
            mobileMentorJoinForm.setStatus(false);
            return mobileMentorJoinForm;

        }
        else if(!mentors.get(0).getBasicInfo().getPassword().equals(mentorJoinForm.getPassword())){
            System.out.println("wrong password");
            mobileMentorJoinForm.setStatus(false);
            return mobileMentorJoinForm;
        }

        mobileMentorJoinForm.setName(mentors.get(0).getName());
        mobileMentorJoinForm.setGrade(mentors.get(0).getBasicInfo().getGrade());
        mobileMentorJoinForm.setSchool(mentors.get(0).getBasicInfo().getSchool());
        mobileMentorJoinForm.setSubject(mentors.get(0).getBasicInfo().getSubject());
        mobileMentorJoinForm.setCompany(mentors.get(0).getCompany());
        mobileMentorJoinForm.setProfileFilePath(mentors.get(0).getProfiles().getProfilePath() + mentors.get(0).getProfiles().getProfileName());
        mobileMentorJoinForm.setStatus(true);
        mobileMentorJoinForm.setPass(mentors.get(0).isPass());

        return mobileMentorJoinForm;
    }

    //위에선 모든 멘토들 리스트 보낸거고 이제는 조건에 맞는 멘토들 받자.
    @ResponseBody
    @PostMapping("/mentorsMatchingList")
    public List<MobileMentorJoinForm> mentorMatchingJoin(Model model, @Valid MatchingForm matchingForm){
        List<Mentor> mentors = mentorService.findMatching(matchingForm.getSchool(), matchingForm.getGrade(), matchingForm.getSubject());
        List<MobileMentorJoinForm> mobileMentorJoinFormsList = new ArrayList<>();
        for(Mentor mentor : mentors){
            if(mentor.isPass()) {
                MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
                mobileMentorJoinForm.setName(mentor.getName());
                mobileMentorJoinForm.setSchool(mentor.getBasicInfo().getSchool());
                mobileMentorJoinForm.setGrade(mentor.getBasicInfo().getGrade());
                mobileMentorJoinForm.setSubject(mentor.getBasicInfo().getSubject());
                mobileMentorJoinForm.setCompany(mentor.getCompany());
                mobileMentorJoinForm.setProfileFilePath(mentor.getProfiles().getProfilePath()+mentor.getProfiles().getProfileName());
                mobileMentorJoinForm.setPass(mentor.isPass());
                mobileMentorJoinFormsList.add(mobileMentorJoinForm);
            }
        }
        return mobileMentorJoinFormsList;
    }





}
