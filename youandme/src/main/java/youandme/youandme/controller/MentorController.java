package youandme.youandme.controller;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.*;
import youandme.youandme.service.ChatService;
import youandme.youandme.service.MenteeService;
import youandme.youandme.service.MentorService;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class MentorController {

    @Autowired
    private final MentorService mentorService;

    @Autowired
    private final MenteeService menteeService;

    @Autowired
    private final ChatService chatService;

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

        mentor.setID(mentorForm.getID());
        mentor.setPassword(mentorForm.getPassword());

        mentor.setName(mentorForm.getName());
        mentor.setSchool(mentorForm.getSchool());
        mentor.setGrade(mentorForm.getGrade());
        mentor.setSubject(mentorForm.getSubject());
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


        mentor.setProfiles(profiles);
        mentor.setGraduationFiles(graduationFiles);
        mentor.setCompanyFiles(companyFiles);

        if(!mentorForm.getShortIntroduce().isEmpty()){
            mentor.setShortIntroduce(mentorForm.getShortIntroduce());
        }
        else {
            mentor.setShortIntroduce("안녕하세요 "+ mentorForm.getName() +"입니다");
        }

        if(!mentorForm.getLongIntroduce().isEmpty()){
            mentor.setLongIntroduce(mentorForm.getLongIntroduce());
        }
        else {
            mentor.setLongIntroduce("안녕하세요 "+mentorForm.getName() +"입니다");
        }

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
                MobileMentor mobileMentor = new MobileMentor();

                mobileMentor.setIndex(mentor.getIndex());
                mobileMentor.setID(mentor.getID());
                mobileMentor.setPassword(mentor.getPassword());
                mobileMentor.setName(mentor.getName());
                mobileMentor.setSchool(mentor.getSchool());
                mobileMentor.setGrade(mentor.getGrade());
                mobileMentor.setSubject(mentor.getSubject());
                mobileMentor.setCompany(mentor.getCompany());
                mobileMentor.setProfileFilePath(mentor.getProfiles().getProfilePath() + mentor.getProfiles().getProfileName());
                mobileMentor.setGraduationFilePath(mentor.getGraduationFiles().getGraduationFilePath() + mentor.getGraduationFiles().getGraduationFileName());
                mobileMentor.setCompanyFilePath(mentor.getCompanyFiles().getCompanyFilePath() + mentor.getCompanyFiles().getCompanyFileName());
                mobileMentor.setShortIntroduce(mentor.getShortIntroduce());
                mobileMentor.setLongIntroduce(mentor.getLongIntroduce());
                mobileMemberList.add(mobileMentor);
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
    public MobileMentorJoinForm mentorJoin(HttpServletResponse response, @Valid MentorJoinForm mentorJoinForm ){
        MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
        List<Mentor> mentors = mentorService.findID(mentorJoinForm.getID());

        if(mentors.isEmpty()){
            System.out.println("no such ID");
            mobileMentorJoinForm.setStatus(false);
            return mobileMentorJoinForm;

        }
        else if(!mentors.get(0).getPassword().equals(mentorJoinForm.getPassword())){
            System.out.println("wrong password");
            mobileMentorJoinForm.setStatus(false);
            return mobileMentorJoinForm;
        }

        mobileMentorJoinForm.setIndex(mentors.get(0).getIndex());
        mobileMentorJoinForm.setName(mentors.get(0).getName());
        mobileMentorJoinForm.setGrade(mentors.get(0).getGrade());
        mobileMentorJoinForm.setSchool(mentors.get(0).getSchool());
        mobileMentorJoinForm.setSubject(mentors.get(0).getSubject());
        mobileMentorJoinForm.setCompany(mentors.get(0).getCompany());
        mobileMentorJoinForm.setProfileFilePath(mentors.get(0).getProfiles().getProfilePath() + mentors.get(0).getProfiles().getProfileName());
        mobileMentorJoinForm.setStatus(true);
        mobileMentorJoinForm.setPass(mentors.get(0).isPass());
        mobileMentorJoinForm.setShortIntroduce(mentors.get(0).getShortIntroduce());
        mobileMentorJoinForm.setLongIntroduce(mentors.get(0).getLongIntroduce());

//        Cookie idCookie = new Cookie("mentor_id", String.valueOf(mentors.get(0).getIndex()));
//        response.addCookie(idCookie);
        return mobileMentorJoinForm;
    }

//    @ResponseBody
//    @PostMapping("/mentors/join/chat")
//    public Chat list2(@CookieValue(name = "mentor_id", required = false) Cookie cookie, Long mentee_id, Model model, String text){
//
//        //왜 갑자기 쿠키를 못받지?
//        Chat chat = new Chat();
//        Long mentor_id = Long.valueOf(cookie.getValue());
//        System.out.println("mentor_id = " + mentor_id);
//        if(mentor_id == null){
//            System.out.println("mentor id is null");
//            return chat;
//        }
//
//        Mentor Sender = mentorService.findOne(mentor_id);
//        Mentee Receiver = menteeService.findOne(mentee_id);
//
//        if(Sender == null || Receiver == null){
//            System.out.println("no user");
//            return chat;
//        }
//
//
//        chat.setSender_index(Sender.getIndex());
//        chat.setReceiver_index(Receiver.getIndex());
//        chat.setText(text);
//        chat.setDate(LocalDateTime.now());
//        model.addAttribute("mentor",Sender);
//        model.addAttribute("mentee",Receiver);
//        System.out.println("mentor_id = " + mentor_id);
//        chatService.save(chat);
//
//        return chat;
//    }

    @ResponseBody
    @PostMapping("/mentors/join/chat")
    public Chat list2(String mentor , String mentee, Model model, String text){

        //왜 갑자기 쿠키를 못받지?
        Chat chat = new Chat();

        if(mentor == null){
            System.out.println("mentor id is null");
            return chat;
        }

        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();

        Mentor Sender = mentorService.findOne(mentor_id);
        Mentee Receiver = menteeService.findOne(mentee_id);

        if(Sender == null || Receiver == null){
            System.out.println("no user");
            return chat;
        }


        chat.setSender_index(Sender.getIndex());
        chat.setReceiver_index(Receiver.getIndex());
        chat.setText(text);
        chat.setDate(LocalDateTime.now());
        model.addAttribute("mentor",Sender);
        model.addAttribute("mentee",Receiver);
        System.out.println("mentor_id = " + mentor_id);
        chatService.save(chat);

        return chat;
    }

    @ResponseBody
    @PostMapping("/mentors/join/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("mentor_id", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "home";
    }


    //위에선 모든 멘토들 리스트 보낸거고 이제는 조건에 맞는 멘토들 받자.

    //비록 기능자체는 mentee를 사용하지만 mentor에 관한 내용이므로 여기다 놨다.
//    @ResponseBody
//    @GetMapping("/mentors/join/chat")
//    public List<Chat> chat(@CookieValue(name = "mentor_id", required = false) Long mentor_id, Model model){
//
//        List<Chat> chat = new ArrayList<>();
//
//
//        if(mentor_id == null){
//            System.out.println("mentor id is null");
//            return chat;
//        }
//
//        List<Chat> whatISend = chatService.findSender(mentor_id);
//        List<Chat> whatIReceived = chatService.findReceiver(mentor_id);
//
//        for (Chat chat1 : whatISend){
//            Chat chatting = new Chat();
//            chatting.setChat_num(chat1.getChat_num());
//            chatting.setSender_index(chat1.getSender_index());
//            chatting.setReceiver_index(chat1.getReceiver_index());
//            chatting.setText(chat1.getText());
//            chatting.setDate(chat1.getDate());
//            chat.add(chatting);
//        }
//
//        for (Chat chat2 : whatIReceived){
//            Chat chatting = new Chat();
//            chatting.setChat_num(chat2.getChat_num());
//            chatting.setSender_index(chat2.getSender_index());
//            chatting.setReceiver_index(chat2.getReceiver_index());
//            chatting.setText(chat2.getText());
//            chatting.setDate(chat2.getDate());
//            chat.add(chatting);
//        }
//        //나중에 객체만들자
//        return chat;
//    }

    @ResponseBody
    @GetMapping("/mentors/join/chat")
    public List<Chat> chat(String mentor, Model model){

        List<Chat> chat = new ArrayList<>();


        if(mentor == null){
            System.out.println("mentor id is null");
            return chat;
        }

        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

        List<Chat> whatISend = chatService.findSender(mentor_id);
        List<Chat> whatIReceived = chatService.findReceiver(mentor_id);

        for (Chat chat1 : whatISend){
            Chat chatting = new Chat();
            chatting.setChat_num(chat1.getChat_num());
            chatting.setSender_index(chat1.getSender_index());
            chatting.setReceiver_index(chat1.getReceiver_index());
            chatting.setText(chat1.getText());
            chatting.setDate(chat1.getDate());
            chat.add(chatting);
        }

        for (Chat chat2 : whatIReceived){
            Chat chatting = new Chat();
            chatting.setChat_num(chat2.getChat_num());
            chatting.setSender_index(chat2.getSender_index());
            chatting.setReceiver_index(chat2.getReceiver_index());
            chatting.setText(chat2.getText());
            chatting.setDate(chat2.getDate());
            chat.add(chatting);
        }
        //나중에 객체만들자
        return chat;
    }





}
