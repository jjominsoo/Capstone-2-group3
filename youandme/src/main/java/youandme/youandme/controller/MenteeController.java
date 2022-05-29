package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.Chat;
import youandme.youandme.domain.Mentee;
import youandme.youandme.domain.Mentor;
import youandme.youandme.domain.Profiles;
import youandme.youandme.repository.ChatRepository;
import youandme.youandme.service.ChatService;
import youandme.youandme.service.MenteeService;
import youandme.youandme.service.MentorService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.CookieManager;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MenteeController {

        @Autowired
        private final MenteeService menteeService;

        @Autowired
        private final MentorService mentorService;

        @Autowired
        private final ChatService chatService;

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
        public MobileMenteeJoinForm menteeJoin(HttpServletResponse response, @Valid MenteeJoinForm menteeJoinForm ){
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
            mobileMenteeJoinForm.setIndex(mentees.get(0).getIndex());
            mobileMenteeJoinForm.setName(mentees.get(0).getName());
            mobileMenteeJoinForm.setGrade(mentees.get(0).getGrade());
            mobileMenteeJoinForm.setSchool(mentees.get(0).getSchool());
            mobileMenteeJoinForm.setSubject(mentees.get(0).getSubject());
            mobileMenteeJoinForm.setProfileFilePath(mentees.get(0).getProfiles().getProfilePath()+mentees.get(0).getProfiles().getProfileName());
            mobileMenteeJoinForm.setStatus(true);

            Cookie idCookie = new Cookie("mentee_id", String.valueOf(mentees.get(0).getIndex()));
            response.addCookie(idCookie);
            System.out.println("idCookie = " + idCookie.getValue());
            return mobileMenteeJoinForm;

        }

        @ResponseBody
        @GetMapping("/mentees/join")
        public Chat list2(@CookieValue(name = "mentee_id", required = false) Long mentee_id, String mentor, Model model, String text){
//            public Chat list2(@CookieValue(name = "mentee_id", required = false) Cookie cookie, String mentor, Model model, String text){
//            Chat chat = new Chat();
//            Long mentee_id = Long.valueOf(cookie.getValue());
            Chat chat = new Chat();

            System.out.println("mentee_id = " + mentee_id);
            if(mentee_id == null){
                System.out.println("mentee id is null");
                return chat;
            }

            Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

            Mentee Sender = menteeService.findOne(mentee_id);
            Mentor Receiver = mentorService.findOne(mentor_id);

            if(Sender == null || Receiver == null){
                System.out.println("no user");
                return chat;
            }


            chat.setSender_index(Sender.getIndex());
            chat.setReceiver_index(Receiver.getIndex());
            chat.setText(text);
            chat.setDate(LocalDateTime.now());
            model.addAttribute("mentee",Sender);
            model.addAttribute("mentor",Receiver);
            System.out.println("mentee_id = " + mentee_id);
            chatService.save(chat);

            return chat;
        }

        @ResponseBody
        @PostMapping("/mentees/join/logout")
        public String logout(HttpServletResponse response){
            Cookie cookie = new Cookie("mentee_id", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "home";
        }


        @ResponseBody
        @GetMapping("/mentees/join/chat")
        public List<Chat> chat(@CookieValue(name = "mentee_id", required = false) Long mentee_id, Model model){

            List<Chat> chat = new ArrayList<>();


            if(mentee_id == null){
                System.out.println("mentee id is null");
                return chat;
            }

            List<Chat> whatISend = chatService.findSender(mentee_id);
            List<Chat> whatIReceived = chatService.findReceiver(mentee_id);

            System.out.println("whatISend = " + whatISend);
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
    //    @ResponseBody
    //    @PostMapping("/like")
    //    public
    //    // 멘티입장에서 멘토 좌우 드래그 할때
    //    // 멘티의 likelist에 해당 멘토의 index넘버를 넣는다.
    //

        @ResponseBody
        @PostMapping("/mentees/join/modify")
        public Mentee modifyMentee(@CookieValue(name = "mentee_id", required = false) Long mentee_id, HttpServletRequest request, @Valid MenteeForm menteeForm, BindingResult result, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile) throws IOException, NullPointerException{

            Mentee oldMentee = menteeService.findOne(mentee_id);
            Mentee newMentee = new Mentee();
            BeanUtils.copyProperties(oldMentee,newMentee);
            newMentee.setName(oldMentee.getName());
            return newMentee;

        }

        @ResponseBody
        @PostMapping("/mentorsMatchingList")
        public List<MobileMentorJoinForm> mentorMatchingJoin(Model model, @Valid MatchingForm matchingForm){
            List<Mentor> mentors = mentorService.findMatching(matchingForm.getSchool(), matchingForm.getGrade(), matchingForm.getSubject());

            List<MobileMentorJoinForm> mobileMentorJoinFormsList = new ArrayList<>();
            for(Mentor mentor : mentors){
                if(mentor.isPass()) {
                    MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
                    mobileMentorJoinForm.setIndex(mentor.getIndex());
                    mobileMentorJoinForm.setName(mentor.getName());
                    mobileMentorJoinForm.setSchool(mentor.getSchool());
                    mobileMentorJoinForm.setGrade(mentor.getGrade());
                    mobileMentorJoinForm.setSubject(mentor.getSubject());
                    mobileMentorJoinForm.setCompany(mentor.getCompany());
                    mobileMentorJoinForm.setProfileFilePath(mentor.getProfiles().getProfilePath()+mentor.getProfiles().getProfileName());
                    mobileMentorJoinForm.setPass(mentor.isPass());
                    mobileMentorJoinFormsList.add(mobileMentorJoinForm);
                }
            }
            return mobileMentorJoinFormsList;
        }
}

