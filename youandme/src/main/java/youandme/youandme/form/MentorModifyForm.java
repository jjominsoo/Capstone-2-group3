package youandme.youandme.form;


import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Getter @Setter
public class MentorModifyForm {

    private String password;
    private String Name;
    private String school;
    private Float grade;
    private String subject;
    private String company;

    private String profileName;
    private String profilePath;

    private String graduationFileName;
    private String graduationFilePath;

    private String companyFileName;
    private String companyFilePath;

    private String shortIntroduce;
    private String longIntroduce;

}
