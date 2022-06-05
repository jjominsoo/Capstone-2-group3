package youandme.youandme.form;


import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;


@Getter @Setter
public class MenteeModifyForm {

    private String password;
    private String Name;
    private String school;
    private Float grade;
    private String subject;

    private String profileName;
    private String profilePath;

}
