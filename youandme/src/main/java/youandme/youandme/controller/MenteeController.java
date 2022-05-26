package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.Mentee;
import youandme.youandme.domain.Profiles;
import youandme.youandme.service.MenteeService;

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
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MenteeController {

    @Autowired
    private final MenteeService menteeService;


    private String getServerUrl(HttpServletRequest request) {
        return new StringBuffer("http://").append(request.getServerName()).append(":").append(request.getServerPort()).toString();
    }

//    @GetMapping("/login")
//    public String login(Model model){
//        model.addAttribute("menteeForm", new MenteeForm());
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String loginId(HttpServletRequest request){
//        Mentee mentee = new Mentee();
//        mentee.setID(request.getParameter("ID"));
//        mentee.BasicInfo.setPassword();
//        if(menteeService.login(mentee){
//            return "redirect:/";
//        }
//        return "login";
//    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/mentees/new")
    public String createForm(Model model) {
        model.addAttribute("menteeForm", new MenteeForm());
        return "mentees/createMenteeForm";

    }

    @CrossOrigin(origins = "*")
    @PostMapping("/mentees/new")
    public String create(HttpServletRequest request, @Valid MenteeForm menteeForm, BindingResult result, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile) throws IOException, NullPointerException{


        if (result.hasErrors()) {
            return "mentees/createMenteeForm";
        }
        Mentee mentee = new Mentee();
//        System.out.println("==============================================================================");
//        System.out.println("menteeFormName = " + menteeForm.getName());
//        System.out.println("menteeFormID = " + menteeForm.getID());
//        System.out.println("menteeFormPWD = " + menteeForm.getPassword());
//        System.out.println("menteeFormPN = " + menteeForm.getProfileName());
//        System.out.println("menteeFormPP = " + menteeForm.getProfilePath());
//        System.out.println("profile = " + profile);
        if(profile != null){

            mentee.setID(menteeForm.getID());
            mentee.setPassword(menteeForm.getPassword());
            mentee.setName(menteeForm.getName());
            mentee.setSchool(menteeForm.getSchool());
            mentee.setGrade(menteeForm.getGrade());
            mentee.setSubject(menteeForm.getSubject());

            String serverUrl = getServerUrl(request);
            String profilePath =  serverUrl + "/images/";
            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
            Profiles profiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);
            profile.transferTo(saveProfilePath);

            mentee.setProfiles(profiles);
            menteeService.join(mentee);

        }
        return "redirect:/";
    }

    @GetMapping(value = "/mentees")
    public String list(Model model){
        List<Mentee> mentees = menteeService.findMentees();
        model.addAttribute("mentees",mentees);
        return "mentees/menteeList";
    }

    @ResponseBody
    @GetMapping(value = "/menteeList")
    public List<MobileMentee> menteeList(Model model){

        List<Mentee> mentees = menteeService.findMentees();

        List<MobileMentee> mobileMenteeList = new ArrayList<>();
        for (Mentee mentee : mentees) {
            MobileMentee mobileMentee = new MobileMentee();
            mobileMentee.setIndex(mentee.getIndex());
            mobileMentee.setID(mentee.getID());
            mobileMentee.setPassword(mentee.getPassword());
            mobileMentee.setName(mentee.getName());
            mobileMentee.setSchool(mentee.getSchool());
            mobileMentee.setGrade(mentee.getGrade());
            mobileMentee.setSubject(mentee.getSubject());
            mobileMentee.setProfileFilePath(mentee.getProfiles().getProfilePath()+mentee.getProfiles().getProfileName());

            mobileMenteeList.add(mobileMentee);
        }
        return mobileMenteeList;
    }

//    @GetMapping(value = "/matching")
//    public String matching(Model model){
//        model.addAttribute("menteeForm", new MenteeForm());
//        return "matchings/createMatchingForm";
//    }
//
//    @PostMapping("/matching")
//    public String matchingList(HttpServletRequest request, @Valid MenteeForm menteeForm){
//        Mentor mentor = new Mentor();
//        System.out.println("menteeForm.getSchool() = " + menteeForm.getSchool());
//        System.out.println("menteeForm.getGrade() = " + menteeForm.getGrade());
//        System.out.println("menteeForm.getSubject() = " + menteeForm.getSubject());
//        return "matchings/matchingList";
//    }

    @ResponseBody
    @PostMapping("/mentees/join")
    public MobileMenteeJoinForm menteeJoin(HttpServletRequest request, @Valid MenteeJoinForm menteeJoinForm ){
        MobileMenteeJoinForm mobileMenteeJoinForm = new MobileMenteeJoinForm();
        List<Mentee> mentees = menteeService.findID(menteeJoinForm.getID());

        if(mentees.isEmpty()){
            System.out.println("no such ID");
            mobileMenteeJoinForm.setStatus(false);
            return mobileMenteeJoinForm;

        }
        else if(!mentees.get(0).getPassword().equals(menteeJoinForm.getPassword())){
            System.out.println("wrong password");
            mobileMenteeJoinForm.setStatus(false);
            return mobileMenteeJoinForm;
        }

        mobileMenteeJoinForm.setName(mentees.get(0).getName());
        mobileMenteeJoinForm.setGrade(mentees.get(0).getGrade());
        mobileMenteeJoinForm.setSchool(mentees.get(0).getSchool());
        mobileMenteeJoinForm.setSubject(mentees.get(0).getSubject());
        mobileMenteeJoinForm.setProfileFilePath(mentees.get(0).getProfiles().getProfilePath()+mentees.get(0).getProfiles().getProfileName());
        mobileMenteeJoinForm.setStatus(true);

        return mobileMenteeJoinForm;
    }
}
