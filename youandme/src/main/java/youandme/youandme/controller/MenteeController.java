package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import youandme.youandme.domain.*;
import youandme.youandme.form.*;
import youandme.youandme.service.*;

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

    private String getServerUrl(HttpServletRequest request) {
        return new StringBuffer("http://").append(request.getServerName()).append(":").append(request.getServerPort()).toString();
    }

    /*=========================회원가입====================================*/
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
        return "home";
    }
    //=====================================================================================

    //============================멘티리스트 출력(관리자페이지)=================================
    @GetMapping(value = "/mentees")
    public String list(Model model){
        List<Mentee> mentees = menteeService.findMentees();
        model.addAttribute("mentees",mentees);
        return "mentees/menteeList";
    }
    //=====================================================================================
    @ResponseBody
    @GetMapping(value = "/menteeList")
    public List<MobileMentee> menteeList(Model model){

        List<Mentee> mentees = menteeService.findMentees();

        List<MobileMentee> mobileMenteeList = new ArrayList<>();
        for (Mentee mentee : mentees) {
            MobileMentee mobileMentee = new MobileMentee();
            mobileMentee.setIndex(mentee.getIndex());
            mobileMentee.setID(mentee.getID());
            mobileMentee.setName(mentee.getName());
            mobileMentee.setSchool(mentee.getSchool());
            mobileMentee.setGrade(mentee.getGrade());
            mobileMentee.setSubject(mentee.getSubject());
            mobileMentee.setProfileFilePath(mentee.getProfiles().getProfilePath()+mentee.getProfiles().getProfileName());
            mobileMenteeList.add(mobileMentee);
        }
        return mobileMenteeList;
    }


    //===================================멘티로그인 (앱)==================================================
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
    //=====================================================================================

    //=====================================멘티가 멘토를 좋아요/싫어요 했을 때================================================
    @ResponseBody
    @PostMapping("/mentees/join/like")
    public void like(String mentee, String mentor){

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

        Like like = new Like();
        like.setMentee_index(mentee_id);
        like.setMentor_index(mentor_id);

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
            mobileMentorJoinForm.setID(likedMentor.getID());
            mobileMentorJoinForm.setName (likedMentor.getName());
            mobileMentorJoinForm.setSchool (likedMentor.getSchool());
            mobileMentorJoinForm.setGrade (likedMentor.getGrade());
            mobileMentorJoinForm.setSubject (likedMentor.getSubject());
            mobileMentorJoinForm.setCompany (likedMentor.getCompany());
            mobileMentorJoinForm.setProfileFilePath (likedMentor.getProfiles().getProfilePath() + likedMentor.getProfiles().getProfileName());
            mobileMentorJoinForm.setPass (likedMentor.isPass());
            if(!likedMentor.getShortIntroduce().isEmpty()){
                mobileMentorJoinForm.setShortIntroduce(likedMentor.getShortIntroduce());
            }
            else {
                mobileMentorJoinForm.setShortIntroduce("안녕하세요 "+ likedMentor.getName() +"입니다");
            }

            if(!likedMentor.getLongIntroduce().isEmpty()){
                mobileMentorJoinForm.setLongIntroduce(likedMentor.getLongIntroduce());
            }
            else {
                mobileMentorJoinForm.setLongIntroduce("안녕하세요 "+likedMentor.getName() +"입니다");
            }
            mentorList.add(mobileMentorJoinForm);
        }
        return mentorList;
    }

    @ResponseBody
    @PostMapping("/mentees/join/unlike")
    public List<MobileMentorJoinForm> unlike(String mentee, String mentor){

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
            mobileMentorJoinForm.setID(likedMentor.getID());
            mobileMentorJoinForm.setName (likedMentor.getName());
            mobileMentorJoinForm.setSchool (likedMentor.getSchool());
            mobileMentorJoinForm.setGrade (likedMentor.getGrade());
            mobileMentorJoinForm.setSubject (likedMentor.getSubject());
            mobileMentorJoinForm.setCompany (likedMentor.getCompany());
            mobileMentorJoinForm.setProfileFilePath (likedMentor.getProfiles().getProfilePath() + likedMentor.getProfiles().getProfileName());
            mobileMentorJoinForm.setPass (likedMentor.isPass());
            if(!likedMentor.getShortIntroduce().isEmpty()){
                mobileMentorJoinForm.setShortIntroduce(likedMentor.getShortIntroduce());
            }
            else {
                mobileMentorJoinForm.setShortIntroduce("안녕하세요 "+ likedMentor.getName() +"입니다");
            }

            if(!likedMentor.getLongIntroduce().isEmpty()){
                mobileMentorJoinForm.setLongIntroduce(likedMentor.getLongIntroduce());
            }
            else {
                mobileMentorJoinForm.setLongIntroduce("안녕하세요 "+likedMentor.getName() +"입니다");
            }
            mentorList.add(mobileMentorJoinForm);
        }
        return mentorList;
    }

    //=====================================================================================

    //=====================================채팅기능 (앱)================================================


    @ResponseBody
    @PostMapping("/mentees/join/chat")
    public Chat sendToMentor( String mentee, String mentor, String text){
        Chat chat = new Chat();

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

        Mentee Sender = menteeService.findOne(mentee_id);
        Mentor Receiver = mentorService.findOne(mentor_id);

        if(Receiver == null){
            System.out.println("no mentor to take message");
            return chat;
        }

        chat.setSender_index(Sender.getIndex());
        chat.setReceiver_index(Receiver.getIndex());
        chat.setText(text);
        chat.setDate(LocalDateTime.now());
        chatService.save(chat);

        return chat;
    }


    @ResponseBody
    @GetMapping("/mentees/join/chat")
    public List<Chat> chattingList(String mentee){

        List<Chat> chat = new ArrayList<>();

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();

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

        Comparator2 comp = new Comparator2();
        Collections.sort(chat, comp);
        // 이 date를 단순한걸로 바꾸자 (날짜시간만 나오게)
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
    //=====================================================================================

    //=======================================정보수정 (앱)==============================================
    @ResponseBody
    @PostMapping("/mentees/join/modify")
    public Mentee modifyMentee(String mentee, HttpServletRequest request, @Valid MenteeModifyForm menteeModifyForm, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile) throws IOException, NullPointerException{
        System.out.println("mentee = " + mentee);

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
        Mentee newMentee = new Mentee();
        System.out.println("profile.getOriginalFilename() = " + profile.getOriginalFilename());
        if(!profile.isEmpty()){
            System.out.println("There is new Profile!");
            newMentee.setID(mentee);
            newMentee.setPassword(menteeModifyForm.getPassword());
            newMentee.setName(menteeModifyForm.getName());
            newMentee.setSchool(menteeModifyForm.getSchool());
            newMentee.setGrade(menteeModifyForm.getGrade());
            newMentee.setSubject(menteeModifyForm.getSubject());


            String serverUrl = getServerUrl(request);
            String profilePath =  serverUrl + "/images/";
            System.out.println("profile.toString() = " + profile.toString());
            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
            Profiles newProfiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);
            profile.transferTo(saveProfilePath);

            newMentee.setProfiles(newProfiles);
            menteeService.update(mentee_id, newMentee);
        }
        else{
            System.out.println("There is no Profile!");
            String oldProfileName = menteeService.findID(mentee).get(0).getProfiles().getProfileName();
            String oldProfilePath = menteeService.findID(mentee).get(0).getProfiles().getProfilePath();
            String oldProfileOriName = menteeService.findID(mentee).get(0).getProfiles().getProfileOriName();


            newMentee.setPassword(menteeModifyForm.getPassword());
            newMentee.setName(menteeModifyForm.getName());
            newMentee.setSchool(menteeModifyForm.getSchool());
            newMentee.setGrade(menteeModifyForm.getGrade());
            newMentee.setSubject(menteeModifyForm.getSubject());
            Profiles oldProfiles = new Profiles(oldProfileOriName,oldProfileName, oldProfilePath);
            newMentee.setProfiles(oldProfiles);
        }

        return newMentee;

    }
    //=====================================================================================
    //=====================================추천 멘토 리스트 (앱)================================================
    @ResponseBody
    @GetMapping("/mentorsMatchingList")
    public List<MobileMentorJoinForm> mentorMatchingList(@Valid MatchingForm matchingForm){
        List<Mentor> mentors = mentorService.findMatching(matchingForm.getSchool(), matchingForm.getGrade(), matchingForm.getSubject());

        List<MobileMentorJoinForm> mobileMentorJoinFormsList = new ArrayList<>();
        if(mentors.isEmpty()){
            return mobileMentorJoinFormsList;
        }

        for(Mentor mentor : mentors){
            if(mentor.isPass()) {
                MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
                mobileMentorJoinForm.setIndex(mentor.getIndex());
                mobileMentorJoinForm.setID(mentor.getID());
                mobileMentorJoinForm.setName(mentor.getName());
                mobileMentorJoinForm.setSchool(mentor.getSchool());
                mobileMentorJoinForm.setGrade(mentor.getGrade());
                mobileMentorJoinForm.setSubject(mentor.getSubject());
                mobileMentorJoinForm.setCompany(mentor.getCompany());
                mobileMentorJoinForm.setProfileFilePath(mentor.getProfiles().getProfilePath()+mentor.getProfiles().getProfileName());
                mobileMentorJoinForm.setPass(mentor.isPass());
                if(!mentor.getShortIntroduce().isEmpty()){
                    mobileMentorJoinForm.setShortIntroduce(mentor.getShortIntroduce());
                }
                else {
                    mobileMentorJoinForm.setShortIntroduce("안녕하세요 "+ mentor.getName() +"입니다");
                }

                if(!mentor.getLongIntroduce().isEmpty()){
                    mobileMentorJoinForm.setLongIntroduce(mentor.getLongIntroduce());
                }
                else {
                    mobileMentorJoinForm.setLongIntroduce("안녕하세요 "+mentor.getName() +"입니다");
                }
                mobileMentorJoinFormsList.add(mobileMentorJoinForm);
            }
        }
        return mobileMentorJoinFormsList;
    }
    //=====================================================================================
    //========================================좋아요 누른 멘토들 리스트 (앱)=============================================
    @ResponseBody
    @GetMapping("/mentees/likeList")
    public List<MobileMentorJoinForm> menteeLikeList(String mentee){
        List<MobileMentorJoinForm> mentorLikeList= new ArrayList<>();
        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();

        List<Like> whoILiked = likeService.findLike(mentee_id);
        for(Like like : whoILiked){
            MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
            Mentor mentor = mentorService.findOne(like.getMentor_index());
            mobileMentorJoinForm.setIndex(mentor.getIndex());
            mobileMentorJoinForm.setID(mentor.getID());
            mobileMentorJoinForm.setName(mentor.getName());
            mobileMentorJoinForm.setSchool(mentor.getSchool());
            mobileMentorJoinForm.setGrade(mentor.getGrade());
            mobileMentorJoinForm.setSubject(mentor.getSubject());
            mobileMentorJoinForm.setCompany(mentor.getCompany());
            mobileMentorJoinForm.setProfileFilePath(mentor.getProfiles().getProfilePath()+mentor.getProfiles().getProfileName());
            mobileMentorJoinForm.setPass(mentor.isPass());
            if(!mentor.getShortIntroduce().isEmpty()){
                mobileMentorJoinForm.setShortIntroduce(mentor.getShortIntroduce());
            }
            else {
                mobileMentorJoinForm.setShortIntroduce("안녕하세요 "+ mentor.getName() +"입니다");
            }

            if(!mentor.getLongIntroduce().isEmpty()){
                mobileMentorJoinForm.setLongIntroduce(mentor.getLongIntroduce());
            }
            else {
                mobileMentorJoinForm.setLongIntroduce("안녕하세요 "+mentor.getName() +"입니다");
            }
            mentorLikeList.add(mobileMentorJoinForm);
        }
        return mentorLikeList;
    }

    //=====================================================================================
}

