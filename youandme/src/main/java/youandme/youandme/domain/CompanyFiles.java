package youandme.youandme.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class CompanyFiles {

    private String CompanyFileOriName;
    private String CompanyFileName;
    private String CompanyFilePath;

    protected CompanyFiles(){
    }

    public CompanyFiles(String fileOriName, String fileName, String filePath) {
        this.CompanyFileOriName = fileOriName;
        this.CompanyFileName = fileName;
        this.CompanyFilePath = filePath;
    }

}