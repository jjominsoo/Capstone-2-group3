package youandme.youandme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import youandme.youandme.domain.Administrator;
import youandme.youandme.service.AdminService;

import javax.validation.Valid;


@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    @Autowired
    private final AdminService adminService;

    @GetMapping("/")
    public String getLogin(Model model){
        Administrator administrator = new Administrator();
        if(adminService.findID(1) == null){
            administrator.setID("admin");
            administrator.setPassword("admin");
            adminService.join(administrator);
        }
        model.addAttribute("admin", new Administrator());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid Administrator admin){
        Administrator findAdmin = adminService.findID(1);
        if(!findAdmin.getID().equals(admin.getID())){
            System.out.println("no such ID");
            return "/login";
        }
        else if(!findAdmin.getPassword().equals(admin.getPassword())){
            System.out.println("wrong password");
            return "/login";
        }
        log.info("login controller");

        return "home";
    }

//    @RequestMapping("/")
//    public String home(){
//        log.info("home controller");
//        return "home";
//    }

}
