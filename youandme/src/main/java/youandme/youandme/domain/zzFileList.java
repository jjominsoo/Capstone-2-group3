package youandme.youandme.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class zzFileList {

    private List<String> fileOriName3 = new ArrayList<>();
    private List<String> fileName3 = new ArrayList<>();
    private List<String> filePath3 = new ArrayList<>();

    public zzFileList(List<String> fileOriName, List<String> fileName, List<String> filePath) {
        this.fileOriName3 = fileOriName;
        this.fileName3 = fileName;
        this.filePath3 = filePath;
    }

    public zzFileList() {

    }

    public void show(){
        System.out.println("fileOriName3 = " + fileOriName3);

    }
}
