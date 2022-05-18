package youandme.youandme.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class GraduationFiles {

    private String GraduationFileOriName;
    private String GraduationFileName;
    private String GraduationFilePath;

    protected GraduationFiles(){
    }

    public GraduationFiles(String fileOriName, String fileName, String filePath) {
        this.GraduationFileOriName = fileOriName;
        this.GraduationFileName = fileName;
        this.GraduationFilePath = filePath;
    }

}