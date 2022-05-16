package youandme.youandme.domain;

import lombok.Getter;

import javax.persistence.Embeddable;
import java.io.File;
import java.util.UUID;

@Embeddable
@Getter
public class zzFiles {
    private String fileOriName;
    private String fileName;
    private String filePath;
    protected zzFiles(){
    }

    public zzFiles(String fileOriName, String fileName, String filePath) {
        this.fileOriName = fileOriName;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}