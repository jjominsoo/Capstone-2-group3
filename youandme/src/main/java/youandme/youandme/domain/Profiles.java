package youandme.youandme.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Profiles {
    private String ProfileOriName;
    private String ProfileName;
    private String ProfilePath;


    protected Profiles() {
    }

    public Profiles(String fileOriName, String fileName, String filePath) {
        this.ProfileOriName = fileOriName;
        this.ProfileName = fileName;
        this.ProfilePath = filePath;
    }
}