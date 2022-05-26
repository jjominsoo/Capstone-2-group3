package youandme.youandme.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Mentor {

    @Id @GeneratedValue
    @Column(name = "mentor_id")
    private Long index;


    private String ID;
    private String password;
    private String name;
    private String school;
    private Float grade;
    private String subject;
    private String company;
    private boolean pass;
//    @Embedded
//    private BasicInfo basicInfo;
    @Embedded
    private Profiles profiles;
    @Embedded
    private GraduationFiles graduationFiles;
    @Embedded
    private CompanyFiles companyFiles;

//    private String introduce;
}
