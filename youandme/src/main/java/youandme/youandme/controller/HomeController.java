package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import youandme.youandme.domain.Administrator;
import youandme.youandme.service.AdminService;

import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Time;
import java.util.*;


@Controller
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class HomeController {

    @Autowired
    private final AdminService adminService;

    @GetMapping("/")
    public String getLogin(Model model, HttpServletRequest request){
        Administrator administrator = new Administrator();
        if(adminService.findID(1) == null){
            administrator.setID("admin");
            administrator.setPassword("admin");
            adminService.join(administrator);
        }
        else{
            Administrator admin = adminService.findID(1);

            if(admin.isAdmin_use()){
                if(admin.isStatus()){
                    return "home";
                }
                System.out.println("현재 로그인되어 있지 않습니다.");
            }
            else{
                System.out.println("someone is using!");
            }
            return "login";

        }


        model.addAttribute("admin", new Administrator());
        return "login";
    }

    @PostMapping("/")
    public String login(@Valid Administrator admin){
        Administrator findAdmin = adminService.findID(1);
        if(!findAdmin.getID().equals(admin.getID())){
            System.out.println("no such ID");
            return "login";
        }
        else if(!findAdmin.getPassword().equals(admin.getPassword())){
            System.out.println("wrong password");
            return "login";
        }
        adminService.setTrue();
        log.info("login controller");

        return "home";
    }


    @Scheduled(fixedDelay = 30000, initialDelay = 1000)
    public void autoLogout(){
        adminService.setFalse();
    }
//    5분 지나면 자동 로그아웃


}
