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

    private String name;

    private String ID;

    private String company;

    @Embedded
    private BasicInfo basicInfo;

    @Embedded
    private Profiles profiles;

    @Embedded
    private GraduationFiles graduationFiles;

    @Embedded
    private CompanyFiles companyFiles;


}
