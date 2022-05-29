package youandme.youandme.controller;


import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;


@Getter @Setter
public class MenteeForm {

    @NotEmpty(message = "회원 아이디는 필수입니다")
    private String ID;

    private String password;
    private String Name;
    private String school;
    private Float grade;
    private String subject;

    private String profileName;
    private String profilePath;

    private boolean status;

    private String introduce;
}
