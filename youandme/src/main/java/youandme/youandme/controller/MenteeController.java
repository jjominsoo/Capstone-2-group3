package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

        mentee.setID(menteeForm.getID());

        mentee.setName(menteeForm.getName());
        mentee.setSchool(menteeForm.getSchool());
        mentee.setGrade(menteeForm.getGrade());
        mentee.setSubject(menteeForm.getSubject());

        if(!profile.isEmpty()){

            String serverUrl = getServerUrl(request);
            String profilePath =  serverUrl + "/images/";
            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
            Profiles profiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);
            profile.transferTo(saveProfilePath);

            mentee.setProfiles(profiles);
        }
        else{
            System.out.println("There is no Profile! Setting basic image..");
            ClassPathResource resource = new ClassPathResource("templates/img/noImage.jpg");
            String serverUrl = getServerUrl(request);
            String profilePath =  serverUrl + "/images/";
            String profileName =  UUID.randomUUID().toString()+"_"+resource.getFilename();
            Profiles profiles = new Profiles(resource.getFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);

            //!!!!!하고 싶엇던거 프로필 입력 안하면 우리가 정해놓은 이미지가 들어가도록.
            profile.transferTo(saveProfilePath);

            mentee.setProfiles(profiles);
        }
        menteeService.join(mentee);

        MenteeHash hash = new MenteeHash();

        String hashedPassword = hash.hashPassword(mentee.getIndex().toString(), menteeForm.getPassword());
        mentee.setPassword(hashedPassword);
        menteeService.update(mentee.getIndex(),mentee);
        return "home";
    }

    class MenteeHash{

        public String hashPassword(String mentee_index, String insertPassword) {
            byte[] salt = mentee_index.getBytes();
            byte[] a = insertPassword.getBytes();
            byte[] bytes = new byte[a.length + salt.length];

            System.arraycopy(a,0,bytes,0,a.length);
            System.arraycopy(salt,0,bytes,a.length,salt.length);

            String hashedPassword = null;
            try{
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(bytes);
                byte[] byteData = md.digest();
                StringBuffer sb = new StringBuffer();
                for(int i = 0 ; i < byteData.length ; i++){
                    sb.append(Integer.toString((byteData[i]&0xFF) + 256, 16).substring(1));
                }
                hashedPassword = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return hashedPassword;
        }
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

        MenteeHash hash = new MenteeHash();
        String hashPassword = hash.hashPassword(mentees.get(0).getIndex().toString(), menteeJoinForm.getPassword());

        if(mentees.isEmpty()){
            System.out.println("no such ID");
            mobileMenteeJoinForm.setStatus(false);
            return mobileMenteeJoinForm;

        }

        else if(!mentees.get(0).getPassword().equals(hashPassword)){
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
    @PostMapping("/mentees/join/modifyFile")
    public Mentee modifyMenteeFile(String mentee, HttpServletRequest request, @Valid MenteeModifyForm menteeModifyForm, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile) throws IOException, NullPointerException{
        System.out.println("mentee = " + mentee);

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
        Mentee oldMentee = menteeService.findID(mentee).get(0);
        Mentee newMentee = new Mentee();
        String serverUrl = getServerUrl(request);

        newMentee.setIndex(mentee_id);
        newMentee.setID(mentee);
        newMentee.setPassword(oldMentee.getPassword());
        newMentee.setName(oldMentee.getName());
        newMentee.setSchool(oldMentee.getSchool());
        newMentee.setGrade(oldMentee.getGrade());
        newMentee.setSubject(oldMentee.getSubject());

        String oldProfileName = menteeService.findID(mentee).get(0).getProfiles().getProfileName();
        String oldProfilePath = menteeService.findID(mentee).get(0).getProfiles().getProfilePath();
        String oldProfileOriName = menteeService.findID(mentee).get(0).getProfiles().getProfileOriName();
        if(!profile.isEmpty()){

            System.out.println("There is new Profile!");

            Path filePath = Paths.get("./images/" + oldProfileName);
            System.out.println("filePath = " + filePath);
            Files.delete(filePath);


            String profilePath =  serverUrl + "/images/";
            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
            Profiles newProfiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);
            profile.transferTo(saveProfilePath);

            newMentee.setProfiles(newProfiles);

        }
        else{
            System.out.println("There is no Profile!");


            Profiles oldProfiles = new Profiles(oldProfileOriName,oldProfileName, oldProfilePath);
            newMentee.setProfiles(oldProfiles);
        }

        menteeService.update(mentee_id, newMentee);

        return newMentee;

    }

    @ResponseBody
    @PostMapping("/mentees/join/modifyInfo")
    public Mentee modifyMenteeInfo(String mentee, HttpServletRequest request, @Valid MenteeModifyForm menteeModifyForm, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile) throws IOException, NullPointerException{
        System.out.println("mentee = " + mentee);

        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();
        Mentee oldMentee = menteeService.findID(mentee).get(0);
        Mentee newMentee = new Mentee();

        newMentee.setIndex(mentee_id);
        newMentee.setID(mentee);

        if(!menteeModifyForm.getPassword().isEmpty()){
            newMentee.setPassword(menteeModifyForm.getPassword());
        }
        else {
            String oldPassword = menteeService.findID(mentee).get(0).getPassword();
            newMentee.setPassword(oldPassword);
        }

        if(!menteeModifyForm.getName().isEmpty()){
            newMentee.setName(menteeModifyForm.getName());
        }
        else {
            String oldName = menteeService.findID(mentee).get(0).getName();
            newMentee.setName(oldName);
        }

        if(!menteeModifyForm.getSchool().isEmpty()){
            newMentee.setSchool(menteeModifyForm.getSchool());
        }
        else {
            String oldSchool = menteeService.findID(mentee).get(0).getSchool();
            newMentee.setSchool(oldSchool);
        }

        if(menteeModifyForm.getGrade() != null){
            newMentee.setGrade(menteeModifyForm.getGrade());
        }
        else {
            Float oldGrade = menteeService.findID(mentee).get(0).getGrade();
            newMentee.setGrade(oldGrade);
        }

        if(!menteeModifyForm.getSubject().isEmpty()){
            newMentee.setSubject(menteeModifyForm.getSubject());
        }
        else {
            String oldSubject = menteeService.findID(mentee).get(0).getSubject();
            newMentee.setSubject(oldSubject);
        }


        newMentee.setProfiles(oldMentee.getProfiles());

        menteeService.update(mentee_id,newMentee);

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

    @ResponseBody
    @GetMapping("/mentorsSchoolMatchingList")
    public List<MobileMentorJoinForm> mentorSchoolMatchingList(@Valid MatchingForm matchingForm){
        List<Mentor> mentors = mentorService.findSchoolMatching(matchingForm.getSchool());

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

    @ResponseBody
    @GetMapping("/mentorsGradeMatchingList")
    public List<MobileMentorJoinForm> mentorGradeMatchingList(@Valid MatchingForm matchingForm){
        List<Mentor> mentors = mentorService.findGradeMatching(matchingForm.getGrade());

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

    @ResponseBody
    @GetMapping("/mentorsSubjectMatchingList")
    public List<MobileMentorJoinForm> mentorSubjectMatchingList(@Valid MatchingForm matchingForm){
        List<Mentor> mentors = mentorService.findSubjectMatching(matchingForm.getSubject());

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
    //==============================================================================================================
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

