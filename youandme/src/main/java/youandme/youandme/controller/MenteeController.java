package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.Mentee;
import youandme.youandme.domain.BasicInfo;
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

    private final MenteeService menteeService;


    private String getServerUrl(HttpServletRequest request) {
        return new StringBuffer("http://").append(request.getServerName()).append(":").append(request.getServerPort()).toString();
    }

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
        System.out.println("==============================================================================");
        System.out.println("menteeFormName = " + menteeForm.getName());
        System.out.println("menteeFormID = " + menteeForm.getID());
        System.out.println("menteeFormPWD = " + menteeForm.getPassword());
        System.out.println("menteeFormPN = " + menteeForm.getProfileName());
        System.out.println("menteeFormPP = " + menteeForm.getProfilePath());
        System.out.println("profileOrigin = " + profile.getOriginalFilename());
        System.out.println("==============================================================================");


        BasicInfo basicInfo = new BasicInfo(menteeForm.getPassword(), menteeForm.getSchool(), menteeForm.getGrade(), menteeForm.getSubject());
        mentee.setID(menteeForm.getID());
        mentee.setName(menteeForm.getName());
        String serverUrl = getServerUrl(request);
        String profilePath =  serverUrl + "/images/";
        String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();

        Profiles profiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);


        Path saveProfilePath = Paths.get("./images/" + profileName);
        profile.transferTo(saveProfilePath);



        //mentee.setID(menteeForm.getID());
        mentee.setBasicInfo(basicInfo);
        mentee.setProfiles(profiles);
        menteeService.join(mentee);
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
    public List<MobileMetee> menteeList(Model model){

        List<Mentee> mentees = menteeService.findMentees();

        List<MobileMetee> mobileMenteeList = new ArrayList<>();
        for (Mentee mentee : mentees) {
            MobileMetee mobileMentee = new MobileMetee();
            mobileMentee.setIndex(mentee.getIndex());
            mobileMentee.setID(mentee.getID());
            mobileMentee.setPassword(mentee.getBasicInfo().getPassword());
            mobileMentee.setName(mentee.getName());
            mobileMentee.setSchool(mentee.getBasicInfo().getSchool());
            mobileMentee.setGrade(mentee.getBasicInfo().getGrade());
            mobileMentee.setSubject(mentee.getBasicInfo().getSubject());
            mobileMentee.setProfileFilePath(mentee.getProfiles().getProfilePath()+mentee.getProfiles().getProfileName());

            mobileMenteeList.add(mobileMentee);
        }
        return mobileMenteeList;
    }
}
