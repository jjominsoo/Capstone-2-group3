package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Embeddable
@Getter
public class zzFiles {
    private String img_id;
    private String filename;
    private String fileOriName;
    private String fileUrl;

    public zzFiles(){
    }

    public zzFiles(String img_id, String filename, String fileOriName, String fileUrl) {
        this.img_id = img_id;
        this.filename = filename;
        this.fileOriName = fileOriName;
        this.fileUrl = fileUrl;
    }
}