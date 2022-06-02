package youandme.youandme.service;

import youandme.youandme.domain.Administrator;
import youandme.youandme.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {


    private final AdminRepository adminRepository;


    @Transactional
    public void join(Administrator admin){
        adminRepository.save(admin);
    }


    public Administrator findID(int admin_index){
        return adminRepository.findID(admin_index);
    }


}
