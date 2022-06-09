package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequiredArgsConstructor
public class MentorController {

    @Autowired
    private final MentorService mentorService;

    @Autowired
    private final MenteeService menteeService;

    @Autowired
    private final ChatService chatService;

    @Autowired
    private final LikeService likeService;

    @Autowired
    private final AdminService adminService;

    private String getServerUrl(HttpServletRequest request) {
        return new StringBuffer("http://").append(request.getServerName()).append(":").append(request.getServerPort()).toString();
    }
    //========================================회원가입 (관리자페이지)=============================================
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
        mentor.setName(mentorForm.getName());
        mentor.setSchool(mentorForm.getSchool());
        mentor.setGrade(mentorForm.getGrade());
        mentor.setSubject(mentorForm.getSubject());
        mentor.setCompany(mentorForm.getCompany());


        String serverUrl = getServerUrl(request);

        if(!profile.isEmpty()) {
            String ProfilePath = serverUrl + "/images/";// + mentorForm.getID() +"/";
            String ProfileName = UUID.randomUUID().toString() + "_" + profile.getOriginalFilename();
            Profiles profiles = new Profiles(profile.getOriginalFilename(), ProfileName, ProfilePath);
            Path saveProfilePath = Paths.get("./images/" + ProfileName);
            profile.transferTo(saveProfilePath);
            mentor.setProfiles(profiles);
        }
        else{
            System.out.println("There is no Profile! Setting basic image..");
            ClassPathResource resource = new ClassPathResource("templates/img/noImage.jpg");
            String profilePath =  serverUrl + "/images/";
            String profileName =  UUID.randomUUID().toString()+"_"+resource.getFilename();
            Profiles profiles = new Profiles(resource.getFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);
            profile.transferTo(saveProfilePath);
            mentor.setProfiles(profiles);
        }
        if(!graduation.isEmpty()) {
            String GraduationPath = serverUrl + "/graduation_certification/";//+ mentorForm.getID() +"/";
            String GraduationName = UUID.randomUUID().toString() + "_" + graduation.getOriginalFilename();
            GraduationFiles graduationFiles = new GraduationFiles(graduation.getOriginalFilename(), GraduationName, GraduationPath);
            Path saveGraduationPath = Paths.get("./graduation_certification/" + GraduationName);
            graduation.transferTo(saveGraduationPath);
            mentor.setGraduationFiles(graduationFiles);
        }
        else{
            System.out.println("There is no Profile! Setting basic image..");
            ClassPathResource resource = new ClassPathResource("templates/img/noImage.jpg");
            String GraduationPath = serverUrl + "/graduation_certification/";//+ mentorForm.getID() +"/";
            String GraduationName = UUID.randomUUID().toString() + "_" +resource.getFilename();
            GraduationFiles graduationFiles = new GraduationFiles(resource.getFilename(), GraduationName, GraduationPath);
            Path saveGraduationPath = Paths.get("./graduation_certification/" + GraduationName);
            profile.transferTo(saveGraduationPath);
            mentor.setGraduationFiles(graduationFiles);
        }

        if(!company.isEmpty()) {
            String CompanyPath = serverUrl + "/company_certification/";//+ mentorForm.getID() +"/";
            String CompanyName = UUID.randomUUID().toString() + "_" + company.getOriginalFilename();
            CompanyFiles companyFiles = new CompanyFiles(company.getOriginalFilename(), CompanyName, CompanyPath);
            Path saveCompanyPath = Paths.get("./company_certification/" + CompanyName);
            company.transferTo(saveCompanyPath);
            mentor.setCompanyFiles(companyFiles);
        }
        else{
            System.out.println("There is no Profile! Setting basic image..");
            ClassPathResource resource = new ClassPathResource("templates/img/noImage.jpg");
            String CompanyPath = serverUrl + "/company_certification/";//+ mentorForm.getID() +"/";
            String CompanyName = UUID.randomUUID().toString() + "_" + resource.getFilename();
            CompanyFiles companyFiles = new CompanyFiles(resource.getFilename(), CompanyName, CompanyPath);
            Path saveCompanyPath = Paths.get("./company_certification/" + CompanyName);
            company.transferTo(saveCompanyPath);
            mentor.setCompanyFiles(companyFiles);
        }





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
        MentorHash hash = new MentorHash();
        String hashedPassword = hash.hashPassword(mentor.getIndex().toString(), mentorForm.getPassword());
        mentor.setPassword(hashedPassword);
        mentorService.update(mentor.getIndex(),mentor);
        return "home";

    }
    class MentorHash{

        public String hashPassword(String mentor_index, String insertPassword) {
            byte[] salt = mentor_index.getBytes();
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

    ////====================================멘토들 리스트 (관리자페이지) =================================================

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

    //=====================================================================================

    //========================================멘토 승인 (관리자페이지)=============================================

    @GetMapping(value = "mentors/{mentor_id}/pass")
    public String pass(@PathVariable("mentor_id") Long mentorId){
        Mentor mentor = mentorService.findOne(mentorId);
        if(!mentor.isPass()){
            mentor.setPass(true);
        }
        else{
            mentor.setPass(false);
        }
        mentorService.pass(mentor);
        return "redirect:/mentors";
    }

    @ResponseBody
    @PostMapping("/mentors/join/adminChat")
    public Chat adminChat(String mentor,String text){

        Chat chat = new Chat();

        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

        Mentor Receiver = mentorService.findOne(mentor_id);

        if(Receiver == null){
            System.out.println("no mentor to take message");
            return chat;
        }

        chat.setSender_index(1L);
        chat.setReceiver_index(Receiver.getIndex());
        chat.setText(text);
        chat.setDate(LocalDateTime.now());
        chatService.save(chat);

        return chat;
    }
    //=====================================================================================

    //======================================멘토 로그인 (앱)===============================================

    @ResponseBody
    @PostMapping("/mentors/join")
    public MobileMentorJoinForm mentorJoin(HttpServletResponse response, @Valid MentorJoinForm mentorJoinForm ){
        MobileMentorJoinForm mobileMentorJoinForm = new MobileMentorJoinForm();
        List<Mentor> mentors = mentorService.findID(mentorJoinForm.getID());

        MentorHash hash = new MentorHash();
        String hashPassword = hash.hashPassword(mentors.get(0).getIndex().toString(), mentorJoinForm.getPassword());

        if(mentors.isEmpty()){
            System.out.println("no such ID");
            mobileMentorJoinForm.setStatus(false);
            return mobileMentorJoinForm;

        }
        else if(!mentors.get(0).getPassword().equals(hashPassword)){
            System.out.println("wrong password");
            mobileMentorJoinForm.setStatus(false);
            return mobileMentorJoinForm;
        }

        mobileMentorJoinForm.setIndex(mentors.get(0).getIndex());
        mobileMentorJoinForm.setID(mentors.get(0).getID());
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

        return mobileMentorJoinForm;
    }

    //=====================================================================================
    //=======================================채팅 (앱)==============================================


    @ResponseBody
    @PostMapping("/mentors/join/chat")
    public Chat sendtoMentee(String mentor , String mentee, Model model, String text){

        Chat chat = new Chat();

        if(mentor == null){
            System.out.println("mentor id is null");
            return chat;
        }

        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
        Long mentee_id = menteeService.findID(mentee).get(0).getIndex();

        Mentor Sender = mentorService.findOne(mentor_id);
        Mentee Receiver = menteeService.findOne(mentee_id);

        if(Receiver == null){
            System.out.println("no mentee to take message");
            return chat;
        }

        chat.setSender_index(Sender.getIndex());
        chat.setReceiver_index(Receiver.getIndex());
        chat.setText(text);
        chat.setDate(LocalDateTime.now());
        model.addAttribute("mentor",Sender);
        model.addAttribute("mentee",Receiver);
        //!!!모델 필요성?
        chatService.save(chat);

        return chat;
    }

    @ResponseBody
    @GetMapping("/mentors/join/chat")
    public List<Chat> chat(String mentor, Model model){

        List<Chat> chat = new ArrayList<>();

        if(mentor == null){
            System.out.println("mentor id is null");
            return chat;
        }//어차피 로그인된 사람의 아이디 받는거라 null값이 안나올텐데?

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

        Comparator2 comp = new Comparator2();
        Collections.sort(chat,comp);
        //date를 간단한걸로
        return chat;
    }
    class Comparator2 implements Comparator<Chat> {
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

    //======================================정보수정 (앱)===============================================


//    @ResponseBody
//    @PostMapping("/mentors/join/modify")
//    public Mentor modifyMentor(String mentor , HttpServletRequest request, @Valid MentorModifyForm mentorModifyForm, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile, @RequestParam("uploadGraduationFile") MultipartFile graduation,  @RequestParam("uploadCompanyFile") MultipartFile company) throws IOException, NullPointerException{
//        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
//        Mentor newMentor = new Mentor();
//
//        if(!profile.isEmpty()){
//            System.out.println("There is new Profile!");
//            newMentor.setPassword(mentorModifyForm.getPassword());
//            newMentor.setName(mentorModifyForm.getName());
//            newMentor.setSchool(mentorModifyForm.getSchool());
//            newMentor.setGrade(mentorModifyForm.getGrade());
//            newMentor.setSubject(mentorModifyForm.getSubject());
//            newMentor.setCompany(mentorModifyForm.getCompany());
//            if(!mentorModifyForm.getShortIntroduce().isEmpty()){
//                newMentor.setShortIntroduce(mentorModifyForm.getShortIntroduce());
//            }
//            else {
//                newMentor.setShortIntroduce("안녕하세요 "+ mentorModifyForm.getName() +"입니다");
//            }
//
//            if(!mentorModifyForm.getLongIntroduce().isEmpty()){
//                newMentor.setLongIntroduce(mentorModifyForm.getLongIntroduce());
//            }
//            else {
//                newMentor.setLongIntroduce("안녕하세요 "+mentorModifyForm.getName() +"입니다");
//            }
//
//            String serverUrl = getServerUrl(request);
//            String profilePath =  serverUrl + "/images/";
//            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
//            Profiles profiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
//            Path saveProfilePath = Paths.get("./images/" + profileName);
//            profile.transferTo(saveProfilePath);
//            newMentor.setProfiles(profiles);
//
//            if(!graduation.isEmpty()){
//                System.out.println("There is new Graduation file!");
//                String GraduationPath =  serverUrl + "/graduation_certification/";
//                String GraduationName =  UUID.randomUUID().toString()+"_"+graduation.getOriginalFilename();
//                GraduationFiles graduationFiles  = new GraduationFiles(graduation.getOriginalFilename(), GraduationName, GraduationPath);
//                Path saveGraduationPath = Paths.get("./graduation_certification/" + GraduationName);
//                graduation.transferTo(saveGraduationPath);
//                newMentor.setGraduationFiles(graduationFiles);
//
//            }
//            else{
//                System.out.println("There is no Graduation file!");
//                String oldGraduationName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileName();
//                String oldGraduationPath = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFilePath();
//                String oldGraduationOriName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileOriName();
//
//                GraduationFiles oldGraduationFile = new GraduationFiles(oldGraduationOriName,oldGraduationName,oldGraduationPath);
//                newMentor.setGraduationFiles(oldGraduationFile);
//            }
//
//            if(!company.isEmpty()){
//                System.out.println("There is new Company file!");
//                String CompanyPath =  serverUrl + "/company_certification/";
//                String CompanyName =  UUID.randomUUID().toString()+"_"+company.getOriginalFilename();
//                CompanyFiles companyFiles = new CompanyFiles(company.getOriginalFilename(), CompanyName, CompanyPath);
//                Path saveCompanyPath = Paths.get("./company_certification/" + CompanyName);
//                company.transferTo(saveCompanyPath);
//                newMentor.setCompanyFiles(companyFiles);
//            }
//            else{
//                System.out.println("There is no Company file!");
//                String oldCompanyName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileName();
//                String oldCompanyPath = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFilePath();
//                String oldCompanyOriName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileOriName();
//
//                CompanyFiles oldCompanyFile = new CompanyFiles(oldCompanyOriName,oldCompanyName,oldCompanyPath);
//                newMentor.setCompanyFiles(oldCompanyFile);
//            }
//
//            mentorService.update(mentor_id, newMentor);
//        }
//        else{
//            System.out.println("There is no Profile!");
//            newMentor.setPassword(mentorModifyForm.getPassword());
//            newMentor.setName(mentorModifyForm.getName());
//            newMentor.setSchool(mentorModifyForm.getSchool());
//            newMentor.setGrade(mentorModifyForm.getGrade());
//            newMentor.setSubject(mentorModifyForm.getSubject());
//            newMentor.setCompany(mentorModifyForm.getCompany());
//            if(!mentorModifyForm.getShortIntroduce().isEmpty()){
//                newMentor.setShortIntroduce(mentorModifyForm.getShortIntroduce());
//            }
//            else {
//                newMentor.setShortIntroduce("안녕하세요 "+ mentorModifyForm.getName() +"입니다");
//            }
//
//            if(!mentorModifyForm.getLongIntroduce().isEmpty()){
//                newMentor.setLongIntroduce(mentorModifyForm.getLongIntroduce());
//            }
//            else {
//                newMentor.setLongIntroduce("안녕하세요 "+mentorModifyForm.getName() +"입니다");
//            }
//            String oldProfileName = mentorService.findID(mentor).get(0).getProfiles().getProfileName();
//            String oldProfilePath = mentorService.findID(mentor).get(0).getProfiles().getProfilePath();
//            String oldProfileOriName = mentorService.findID(mentor).get(0).getProfiles().getProfileOriName();
//            Profiles oldProfiles = new Profiles(oldProfileOriName, oldProfileName, oldProfilePath);
//            newMentor.setProfiles(oldProfiles);
//
//            String serverUrl = getServerUrl(request);
//
//            if(!graduation.isEmpty()){
//                System.out.println("There is new Graduation file!");
//                String GraduationPath =  serverUrl + "/graduation_certification/";
//                String GraduationName =  UUID.randomUUID().toString()+"_"+graduation.getOriginalFilename();
//                GraduationFiles graduationFiles  = new GraduationFiles(graduation.getOriginalFilename(), GraduationName, GraduationPath);
//                Path saveGraduationPath = Paths.get("./graduation_certification/" + GraduationName);
//                graduation.transferTo(saveGraduationPath);
//                newMentor.setGraduationFiles(graduationFiles);
//            }
//            else{
//                System.out.println("There is no Graduation file!");
//                String oldGraduationName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileName();
//                String oldGraduationPath = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFilePath();
//                String oldGraduationOriName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileOriName();
//
//                GraduationFiles oldGraduationFile = new GraduationFiles(oldGraduationOriName,oldGraduationName,oldGraduationPath);
//                newMentor.setGraduationFiles(oldGraduationFile);
//            }
//
//            if(!company.isEmpty()){
//                System.out.println("There is new Company file!");
//                String CompanyPath =  serverUrl + "/company_certification/";
//                String CompanyName =  UUID.randomUUID().toString()+"_"+company.getOriginalFilename();
//                CompanyFiles companyFiles = new CompanyFiles(company.getOriginalFilename(), CompanyName, CompanyPath);
//                Path saveCompanyPath = Paths.get("./company_certification/" + CompanyName);
//                company.transferTo(saveCompanyPath);
//                newMentor.setCompanyFiles(companyFiles);
//            }
//            else{
//                System.out.println("There is no Company file!");
//                String oldCompanyName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileName();
//                String oldCompanyPath = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFilePath();
//                String oldCompanyOriName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileOriName();
//
//                CompanyFiles oldCompanyFile = new CompanyFiles(oldCompanyOriName,oldCompanyName,oldCompanyPath);
//                newMentor.setCompanyFiles(oldCompanyFile);
//            }
//            mentorService.update(mentor_id, newMentor);
//        }
//        // 증명서도 다시 받을 수 있게 할까??
//
//        return newMentor;
//
//    }

//    @ResponseBody
//    @PostMapping("/mentors/join/modifyFile")
//    public Mentor modifyMentorFile(String mentor , HttpServletRequest request, @Valid MentorModifyForm mentorModifyForm, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile, @RequestParam("uploadGraduationFile") MultipartFile graduation,  @RequestParam("uploadCompanyFile") MultipartFile company) throws IOException, NullPointerException{
//        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
//        Mentor oldMentor = mentorService.findID(mentor).get(0);
//        Mentor newMentor = new Mentor();
//        String serverUrl = getServerUrl(request);
//
//        newMentor.setIndex(mentor_id);
//        newMentor.setID(mentor);
//        newMentor.setPassword(oldMentor.getPassword());
//        newMentor.setName(oldMentor.getName());
//        newMentor.setSchool(oldMentor.getSchool());
//        newMentor.setGrade(oldMentor.getGrade());
//        newMentor.setSubject(oldMentor.getSubject());
//        newMentor.setCompany(oldMentor.getCompany());
//        newMentor.setShortIntroduce(oldMentor.getShortIntroduce());
//        newMentor.setLongIntroduce(oldMentor.getLongIntroduce());
//
//        String oldProfileName = mentorService.findID(mentor).get(0).getProfiles().getProfileName();
//        String oldProfilePath = mentorService.findID(mentor).get(0).getProfiles().getProfilePath();
//        String oldProfileOriName = mentorService.findID(mentor).get(0).getProfiles().getProfileOriName();se
//
//        String oldGraduationName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileName();
//        String oldGraduationPath = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFilePath();
//        String oldGraduationOriName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileOriName();
//
//        String oldCompanyName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileName();
//        String oldCompanyPath = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFilePath();
//        String oldCompanyOriName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileOriName();
//
//        if(!profile.isEmpty()){
//            System.out.println("There is new Profile!");
//
//            Path filePath = Paths.get("./images/" + oldProfileName);
//            System.out.println("filePath = " + filePath);
//            Files.delete(filePath);
//
//            String profilePath =  serverUrl + "/images/";
//            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
//            Profiles profiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
//            Path saveProfilePath = Paths.get("./images/" + profileName);
//            profile.transferTo(saveProfilePath);
//            newMentor.setProfiles(profiles);
//
//        }
//        else{
//            System.out.println("There is no Profile!");
//
//            Profiles oldProfiles = new Profiles(oldProfileOriName, oldProfileName, oldProfilePath);
//            newMentor.setProfiles(oldProfiles);
//
//        }
//
//        if(!graduation.isEmpty()){
//            System.out.println("There is new Graduation file!");
//
//            Path filePath = Paths.get("./graduation_certification/" + oldGraduationName);
//            System.out.println("filePath = " + filePath);
//            Files.delete(filePath);
//
//            String GraduationPath =  serverUrl + "/graduation_certification/";
//            String GraduationName =  UUID.randomUUID().toString()+"_"+graduation.getOriginalFilename();
//            GraduationFiles graduationFiles  = new GraduationFiles(graduation.getOriginalFilename(), GraduationName, GraduationPath);
//            Path saveGraduationPath = Paths.get("./graduation_certification/" + GraduationName);
//            graduation.transferTo(saveGraduationPath);
//            newMentor.setGraduationFiles(graduationFiles);
//        }
//        else{
//            System.out.println("There is no Graduation file!");
//
//
//            GraduationFiles oldGraduationFile = new GraduationFiles(oldGraduationOriName,oldGraduationName,oldGraduationPath);
//            newMentor.setGraduationFiles(oldGraduationFile);
//        }
//
//        if(!company.isEmpty()){
//            System.out.println("There is new Company file!");
//
//            Path filePath = Paths.get("./company_certification/" + oldCompanyName);
//            System.out.println("filePath = " + filePath);
//            Files.delete(filePath);
//
//            String CompanyPath =  serverUrl + "/company_certification/";
//            String CompanyName =  UUID.randomUUID().toString()+"_"+company.getOriginalFilename();
//            CompanyFiles companyFiles = new CompanyFiles(company.getOriginalFilename(), CompanyName, CompanyPath);
//            Path saveCompanyPath = Paths.get("./company_certification/" + CompanyName);
//            company.transferTo(saveCompanyPath);
//            newMentor.setCompanyFiles(companyFiles);
//        }
//        else{
//            System.out.println("There is no Company file!");
//
//
//            CompanyFiles oldCompanyFile = new CompanyFiles(oldCompanyOriName,oldCompanyName,oldCompanyPath);
//            newMentor.setCompanyFiles(oldCompanyFile);
//        }
//
//        mentorService.update(mentor_id, newMentor);
//
//        return newMentor;
//
//    }

    @ResponseBody
    @PostMapping("/mentors/join/modifyProfile")
    public Mentor modifyMentorProfile(String mentor , HttpServletRequest request, @Valid MentorModifyForm mentorModifyForm, @RequestParam(value = "uploadProfile", required = false) MultipartFile profile) throws IOException, NullPointerException{
        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
        Mentor oldMentor = mentorService.findID(mentor).get(0);
        Mentor newMentor = new Mentor();
        String serverUrl = getServerUrl(request);

        newMentor.setIndex(mentor_id);
        newMentor.setID(mentor);
        newMentor.setPassword(oldMentor.getPassword());
        newMentor.setName(oldMentor.getName());
        newMentor.setSchool(oldMentor.getSchool());
        newMentor.setGrade(oldMentor.getGrade());
        newMentor.setSubject(oldMentor.getSubject());
        newMentor.setCompany(oldMentor.getCompany());
        newMentor.setShortIntroduce(oldMentor.getShortIntroduce());
        newMentor.setLongIntroduce(oldMentor.getLongIntroduce());

        String oldProfileName = mentorService.findID(mentor).get(0).getProfiles().getProfileName();
        String oldProfilePath = mentorService.findID(mentor).get(0).getProfiles().getProfilePath();
        String oldProfileOriName = mentorService.findID(mentor).get(0).getProfiles().getProfileOriName();

        newMentor.setGraduationFiles(oldMentor.getGraduationFiles());
        newMentor.setCompanyFiles(oldMentor.getCompanyFiles());


        if(!profile.isEmpty()){
            System.out.println("There is new Profile!");

            Path filePath = Paths.get("./images/" + oldProfileName);
            System.out.println("filePath = " + filePath);
            Files.delete(filePath);

            String profilePath =  serverUrl + "/images/";
            String profileName =  UUID.randomUUID().toString()+"_"+profile.getOriginalFilename();
            Profiles profiles = new Profiles(profile.getOriginalFilename(), profileName, profilePath);
            Path saveProfilePath = Paths.get("./images/" + profileName);
            profile.transferTo(saveProfilePath);
            newMentor.setProfiles(profiles);

        }
        else{
            System.out.println("There is no Profile!");

            Profiles oldProfiles = new Profiles(oldProfileOriName, oldProfileName, oldProfilePath);
            newMentor.setProfiles(oldProfiles);

        }

        mentorService.update(mentor_id, newMentor);

        return newMentor;

    }

    @ResponseBody
    @PostMapping("/mentors/join/modifyGraduationFile")
    public Mentor modifyMentorGraduationFile(String mentor , HttpServletRequest request, @Valid MentorModifyForm mentorModifyForm, @RequestParam("uploadGraduationFile") MultipartFile graduation) throws IOException, NullPointerException{
        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
        Mentor oldMentor = mentorService.findID(mentor).get(0);
        Mentor newMentor = new Mentor();
        String serverUrl = getServerUrl(request);

        newMentor.setIndex(mentor_id);
        newMentor.setID(mentor);
        newMentor.setPassword(oldMentor.getPassword());
        newMentor.setName(oldMentor.getName());
        newMentor.setSchool(oldMentor.getSchool());
        newMentor.setGrade(oldMentor.getGrade());
        newMentor.setSubject(oldMentor.getSubject());
        newMentor.setCompany(oldMentor.getCompany());
        newMentor.setShortIntroduce(oldMentor.getShortIntroduce());
        newMentor.setLongIntroduce(oldMentor.getLongIntroduce());

        String oldGraduationName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileName();
        String oldGraduationPath = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFilePath();
        String oldGraduationOriName = mentorService.findID(mentor).get(0).getGraduationFiles().getGraduationFileOriName();

        newMentor.setProfiles(oldMentor.getProfiles());
        newMentor.setCompanyFiles(oldMentor.getCompanyFiles());

        if(!graduation.isEmpty()){
            System.out.println("There is new Graduation file!");

            Path filePath = Paths.get("./graduation_certification/" + oldGraduationName);
            System.out.println("filePath = " + filePath);
            Files.delete(filePath);

            String GraduationPath =  serverUrl + "/graduation_certification/";
            String GraduationName =  UUID.randomUUID().toString()+"_"+graduation.getOriginalFilename();
            GraduationFiles graduationFiles  = new GraduationFiles(graduation.getOriginalFilename(), GraduationName, GraduationPath);
            Path saveGraduationPath = Paths.get("./graduation_certification/" + GraduationName);
            graduation.transferTo(saveGraduationPath);
            newMentor.setGraduationFiles(graduationFiles);
        }
        else{
            System.out.println("There is no Graduation file!");


            GraduationFiles oldGraduationFile = new GraduationFiles(oldGraduationOriName,oldGraduationName,oldGraduationPath);
            newMentor.setGraduationFiles(oldGraduationFile);
        }


        mentorService.update(mentor_id, newMentor);

        return newMentor;

    }

    @ResponseBody
    @PostMapping("/mentors/join/modifyCompanyFile")
    public Mentor modifyMentorCompanyFile(String mentor , HttpServletRequest request, @Valid MentorModifyForm mentorModifyForm,  @RequestParam("uploadCompanyFile") MultipartFile company) throws IOException, NullPointerException{
        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();
        Mentor oldMentor = mentorService.findID(mentor).get(0);
        Mentor newMentor = new Mentor();
        String serverUrl = getServerUrl(request);

        newMentor.setIndex(mentor_id);
        newMentor.setID(mentor);
        newMentor.setPassword(oldMentor.getPassword());
        newMentor.setName(oldMentor.getName());
        newMentor.setSchool(oldMentor.getSchool());
        newMentor.setGrade(oldMentor.getGrade());
        newMentor.setSubject(oldMentor.getSubject());
        newMentor.setCompany(oldMentor.getCompany());
        newMentor.setShortIntroduce(oldMentor.getShortIntroduce());
        newMentor.setLongIntroduce(oldMentor.getLongIntroduce());

        String oldCompanyName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileName();
        String oldCompanyPath = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFilePath();
        String oldCompanyOriName = mentorService.findID(mentor).get(0).getCompanyFiles().getCompanyFileOriName();

        newMentor.setProfiles(oldMentor.getProfiles());
        newMentor.setGraduationFiles(oldMentor.getGraduationFiles());

        if(!company.isEmpty()){
            System.out.println("There is new Company file!");

            Path filePath = Paths.get("./company_certification/" + oldCompanyName);
            System.out.println("filePath = " + filePath);
            Files.delete(filePath);

            String CompanyPath =  serverUrl + "/company_certification/";
            String CompanyName =  UUID.randomUUID().toString()+"_"+company.getOriginalFilename();
            CompanyFiles companyFiles = new CompanyFiles(company.getOriginalFilename(), CompanyName, CompanyPath);
            Path saveCompanyPath = Paths.get("./company_certification/" + CompanyName);
            company.transferTo(saveCompanyPath);
            newMentor.setCompanyFiles(companyFiles);
        }
        else{
            System.out.println("There is no Company file!");


            CompanyFiles oldCompanyFile = new CompanyFiles(oldCompanyOriName,oldCompanyName,oldCompanyPath);
            newMentor.setCompanyFiles(oldCompanyFile);
        }
        mentorService.nonPass(newMentor);
        mentorService.update(mentor_id, newMentor);

        return newMentor;

    }

    @ResponseBody
    @PostMapping("/mentors/join/modifyInfo")
    public Mentor modifyMentorInfo(String mentor , HttpServletRequest request, @Valid MentorModifyForm mentorModifyForm) throws IOException, NullPointerException{
        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

        Mentor oldMentor = mentorService.findID(mentor).get(0);
        Mentor newMentor = new Mentor();

        newMentor.setIndex(mentor_id);
        newMentor.setID(mentor);
        if(!mentorModifyForm.getPassword().isEmpty()){
            newMentor.setPassword(mentorModifyForm.getPassword());
        }
        else {
            String oldPassword = mentorService.findID(mentor).get(0).getPassword();
            newMentor.setPassword(oldPassword);
        }

        if(!mentorModifyForm.getName().isEmpty()){
            newMentor.setName(mentorModifyForm.getName());
        }
        else {
            String oldName = mentorService.findID(mentor).get(0).getName();
            newMentor.setName(oldName);
        }

        if(!mentorModifyForm.getSchool().isEmpty()){
            newMentor.setSchool(mentorModifyForm.getSchool());
        }
        else {
            String oldSchool = mentorService.findID(mentor).get(0).getSchool();
            newMentor.setSchool(oldSchool);
        }

        if(mentorModifyForm.getGrade() != null){
            newMentor.setGrade(mentorModifyForm.getGrade());
        }
        else {
            Float oldGrade = mentorService.findID(mentor).get(0).getGrade();
            newMentor.setGrade(oldGrade);
        }

        if(!mentorModifyForm.getSubject().isEmpty()){
            newMentor.setSubject(mentorModifyForm.getSubject());
        }
        else {
            String oldSubject = mentorService.findID(mentor).get(0).getSubject();
            newMentor.setSubject(oldSubject);
        }

        if(!mentorModifyForm.getCompany().isEmpty()){
            newMentor.setCompany(mentorModifyForm.getCompany());
        }
        else {
            String oldCompany = mentorService.findID(mentor).get(0).getCompany();
            newMentor.setCompany(oldCompany);
            mentorService.nonPass(newMentor);
        }

        if(!mentorModifyForm.getShortIntroduce().isEmpty()){
            newMentor.setShortIntroduce(mentorModifyForm.getShortIntroduce());
        }
        else {
            newMentor.setShortIntroduce("안녕하세요 "+ mentorModifyForm.getName() +"입니다");
        }

        if(!mentorModifyForm.getLongIntroduce().isEmpty()){
            newMentor.setLongIntroduce(mentorModifyForm.getLongIntroduce());
        }
        else {
            newMentor.setLongIntroduce("안녕하세요 "+mentorModifyForm.getName() +"입니다");
        }

        newMentor.setProfiles(oldMentor.getProfiles());
        newMentor.setGraduationFiles(oldMentor.getGraduationFiles());
        newMentor.setCompanyFiles(oldMentor.getCompanyFiles());

        mentorService.update(mentor_id, newMentor);
        return newMentor;

    }

    //=====================================================================================

    //=======================================자신을 좋아요 누른 멘티들 리스트 (앱)==============================================
    @ResponseBody
    @GetMapping("/mentors/likedList")
    public List<MobileMenteeJoinForm> mentorLikedList(String mentor){
        List<MobileMenteeJoinForm> menteeLikeList= new ArrayList<>();
        Long mentor_id = mentorService.findID(mentor).get(0).getIndex();

        List<Like> LikedByWhom = likeService.findLiked(mentor_id);
        for(Like like : LikedByWhom){
            MobileMenteeJoinForm mobileMenteeJoinForm = new MobileMenteeJoinForm();
            Mentee mentee = menteeService.findOne(like.getMentee_index());
            mobileMenteeJoinForm.setIndex(mentee.getIndex());
            mobileMenteeJoinForm.setID(mentee.getID());
            mobileMenteeJoinForm.setName(mentee.getName());
            mobileMenteeJoinForm.setSchool(mentee.getSchool());
            mobileMenteeJoinForm.setGrade(mentee.getGrade());
            mobileMenteeJoinForm.setSubject(mentee.getSubject());
            mobileMenteeJoinForm.setProfileFilePath(mentee.getProfiles().getProfilePath()+mentee.getProfiles().getProfileName());
            menteeLikeList.add(mobileMenteeJoinForm);
        }
        return menteeLikeList;
    }

    //=====================================================================================
}
