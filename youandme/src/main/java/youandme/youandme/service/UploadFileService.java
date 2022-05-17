//package youandme.youandme.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.PostConstruct;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.UUID;
//
//@Service
//public class UploadFileService {
//    private String dir = "/tmp";
//
//    private Path fileDir;
//
//    private final String TYPE_CSV = "text/csv";
//
//    @PostConstruct
//    public void postConstruct() {
//        fileDir = Paths.get(dir).toAbsolutePath().normalize();
//
//        try {
//            Files.createDirectories(fileDir);
//        } catch (IOException e) {
//        }
//    }
//
//    public UploadFile uploadFile(MultipartFile file){
//        if(TYPE_CSV.equals(file.getContentType()) == false){
//        }
//        String uploadFileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
//        } catch (IOException e) {
//        }
//
//        String realName = UUID.randomUUID().toString() + "_" + uploadFileName;
//        Path targetLocation = fileDir.resolve(realName);
//        try {
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//        }
//
//        return UploadFile.builder()
//                .displayName(uploadFileName)
//                .size(file.getSize())
//                .count(fileTargetCount)
//                .build();
//    }
//}