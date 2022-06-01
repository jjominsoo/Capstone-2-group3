package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.*;
import youandme.youandme.service.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;


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

        @Autowired
        private final LikeService likeService;

//        @Autowired
//        private final LikeListService likeListService;

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

            return mobileMenteeJoinForm;

        }

        @ResponseBody
        @PostMapping("/mentees/join/like")
        public void Like(String mentee, String mentor){

            Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
            Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

            Like like = new Like();
            like.setMentee_index(mentee_id);
            like.setMentor_index(mentor_id);

            System.out.println("like.getMentee_index() = " + like.getMentee_index());
//            likeListService.save(likeList);
            likeService.save(like);


        }

        @ResponseBody
        @GetMapping("/mentees/join/like")
        public List<MobileMentorJoinForm> likeList(String mentee){
            Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
            List<Like> likeList = likeService.findLiked(mentee_id);
            List<MobileMentorJoinForm> mentorList = new ArrayList<>();

            for(Like like : likeList){
                MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
                Mentor likedMentor = mentorService.findOne(like.getMentor_index());
                mobileMentorJoinForm.setIndex(likedMentor.getIndex());
                mobileMentorJoinForm.setName (likedMentor.getName());
                mobileMentorJoinForm.setSchool (likedMentor.getSchool());
                mobileMentorJoinForm.setGrade (likedMentor.getGrade());
                mobileMentorJoinForm.setSubject (likedMentor.getSubject());
                mobileMentorJoinForm.setCompany (likedMentor.getCompany());
                mobileMentorJoinForm.setProfileFilePath (likedMentor.getProfiles().getProfilePath() + likedMentor.getProfiles().getProfileName());
                mobileMentorJoinForm.setPass (likedMentor.isPass());
                mobileMentorJoinForm.setShortIntroduce (likedMentor.getShortIntroduce());
                mobileMentorJoinForm.setLongIntroduce (likedMentor.getLongIntroduce());

                mentorList.add(mobileMentorJoinForm);
            }



            return mentorList;
        }

        @ResponseBody
        @PostMapping("/mentees/join/unlike")
        public List<MobileMentorJoinForm> Unlike(String mentee, String mentor){

            Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
            Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
            List<Like> unlikeList = likeService.findUnliked(mentee_id,mentor_id);
            for(Like unlike : unlikeList){
                likeService.unliked(unlike);
            }

            List<Like> likeList = likeService.findLiked(mentee_id);
            List<MobileMentorJoinForm> mentorList = new ArrayList<>();

            for(Like like : likeList){
                MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
                Mentor likedMentor = mentorService.findOne(like.getMentor_index());
                mobileMentorJoinForm.setIndex(likedMentor.getIndex());
                mobileMentorJoinForm.setName (likedMentor.getName());
                mobileMentorJoinForm.setSchool (likedMentor.getSchool());
                mobileMentorJoinForm.setGrade (likedMentor.getGrade());
                mobileMentorJoinForm.setSubject (likedMentor.getSubject());
                mobileMentorJoinForm.setCompany (likedMentor.getCompany());
                mobileMentorJoinForm.setProfileFilePath (likedMentor.getProfiles().getProfilePath() + likedMentor.getProfiles().getProfileName());
                mobileMentorJoinForm.setPass (likedMentor.isPass());
                mobileMentorJoinForm.setShortIntroduce (likedMentor.getShortIntroduce());
                mobileMentorJoinForm.setLongIntroduce (likedMentor.getLongIntroduce());

                mentorList.add(mobileMentorJoinForm);
            }
            return mentorList;
        }


//        @ResponseBody
//        @PostMapping("/mentees/join/chat")
//        public Chat list2(@CookieValue(name = "mentee_id", required = false) Long mentee_id, String mentor, Model model, String text){
//            Chat chat = new Chat();
//
//            System.out.println("mentee_id = " + mentee_id);
//            if(mentee_id == null){
//                System.out.println("mentee id is null");
//                return chat;
//            }
//
//            Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
//
//            Mentee Sender = menteeService.findOne(mentee_id);
//            Mentor Receiver = mentorService.findOne(mentor_id);
//
//            if(Sender == null || Receiver == null){
//                System.out.println("no user");
//                return chat;
//            }
//
//
//            chat.setSender_index(Sender.getIndex());
//            chat.setReceiver_index(Receiver.getIndex());
//            chat.setText(text);
//            chat.setDate(LocalDateTime.now());
//            model.addAttribute("mentee",Sender);
//            model.addAttribute("mentor",Receiver);
//            System.out.println("mentee_id = " + mentee_id);
//            chatService.save(chat);
//
//            return chat;
//        }

    @ResponseBody
    @PostMapping("/mentees/join/chat")
    public Chat list2( String mentee, String mentor, Model model, String text){
        Chat chat = new Chat();


        if(mentee == null){
            System.out.println("mentee id is null");
            return chat;
        }

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
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
        chatService.save(chat);

        return chat;
    }


//        @ResponseBody
//        @GetMapping("/mentees/join/chat")
//        public List<Chat> chat(@CookieValue(name = "mentee_id", required = false) Long mentee_id, Model model){
//
//            List<Chat> chat = new ArrayList<>();
//
//
//            if(mentee_id == null){
//                System.out.println("mentee id is null");
//                return chat;
//            }
//
//            List<Chat> whatISend = chatService.findSender(mentee_id);
//            List<Chat> whatIReceived = chatService.findReceiver(mentee_id);
//
//            System.out.println("whatISend = " + whatISend);
//            for (Chat chat1 : whatISend){
//                Chat chatting = new Chat();
//                chatting.setChat_num(chat1.getChat_num());
//                chatting.setSender_index(chat1.getSender_index());
//                chatting.setReceiver_index(chat1.getReceiver_index());
//                chatting.setText(chat1.getText());
//                chatting.setDate(chat1.getDate());
//                chat.add(chatting);
//            }
//
//            for (Chat chat2 : whatIReceived){
//                Chat chatting = new Chat();
//                chatting.setChat_num(chat2.getChat_num());
//                chatting.setSender_index(chat2.getSender_index());
//                chatting.setReceiver_index(chat2.getReceiver_index());
//                chatting.setText(chat2.getText());
//                chatting.setDate(chat2.getDate());
//                chat.add(chatting);
//            }
//            //나중에 객체만들자
//            return chat;
//        }




    @ResponseBody
    @GetMapping("/mentees/join/chat")
    public List<Chat> chat(String mentee, Model model){

        List<Chat> chat = new ArrayList<>();


        if(mentee == null){
            System.out.println("mentee id is null");
            return chat;
        }

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();

        List<Chat> whatISend = chatService.findSender(mentee_id);
        List<Chat> whatIReceived = chatService.findReceiver(mentee_id);
        ArrayList allMessage = new ArrayList<>();


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

        Comparator2 comp = new Comparator2();
        Collections.sort(chat, comp);

        System.out.println("chat = " + chat);
        //나중에 객체만들자
        return chat;
    }

    class Comparator2 implements Comparator<Chat>{
        @Override
        public int compare(Chat o1, Chat o2) {
            Long firstIndex = o1.getChat_num();
            Long secondIndex = o2.getChat_num();
            
            if(firstIndex > secondIndex){
                return 0;
            }
            else{
                return -1;
            }
        }
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
    @PostMapping("/mentees/join/modify")
    public Mentee modifyMentee(String mentee, HttpServletRequest request, @Valid MenteeForm menteeForm, BindingResult result, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile) throws IOException, NullPointerException{
        System.out.println("mentee = " + mentee);

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
        Mentee newMentee = new Mentee();

        if(profile != null){

            newMentee.setPassword(menteeForm.getPassword());
            newMentee.setName(menteeForm.getName());
            newMentee.setSchool(menteeForm.getSchool());
            newMentee.setGrade(menteeForm.getGrade());
            newMentee.setSubject(menteeForm.getSubject());

            String serverUrl = getServerUrl(request);
            String profilePath =  serverUrl + "/images/";
            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
            Profiles profiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);
            profile.transferTo(saveProfilePath);

            newMentee.setProfiles(profiles);
            menteeService.update(mentee_id, newMentee);
        }

        System.out.println("newMentee.getIndex() = " + newMentee.getName());

        return newMentee;

    }

    @ResponseBody
    @GetMapping("/mentorsMatchingList")
    public List<MobileMentorJoinForm> mentorMatchingJoin(Model model, @Valid MatchingForm matchingForm){
        List<Mentor> mentors = mentorService.findMatching(matchingForm.getSchool(), matchingForm.getGrade(), matchingForm.getSubject());


        List<MobileMentorJoinForm> mobileMentorJoinFormsList = new ArrayList<>();
        if(mentors.isEmpty()){
            return mobileMentorJoinFormsList;
        }

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

