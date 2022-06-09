package youandme.youandme.service;

import youandme.youandme.domain.Chat;
import youandme.youandme.repository.ChatRepository;
import youandme.youandme.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {


    private final ChatRepository chatRepository;

    //회원가입
    @Transactional
    public Long save(Chat chat){
        chatRepository.save(chat);
        return chat.getChat_num();
    }

    public List<Chat> findSender(Long senderIndex){
        return chatRepository.findSender(senderIndex);
    }

    public List<Chat> findReceiver(Long receiverIndex){
        return chatRepository.findReceiver(receiverIndex);
    }

    public List<Chat> findChat(Long senderIndex, Long receiverIndex){
        return chatRepository.findChat(senderIndex, receiverIndex);
    }

//    public boolean login(Mentee menteeId) {
//        Mentee findMentee = menteeRepository.findOne(mentee.getIndex());
//        System.out.println("findMentee = " + findMentee);
//        if(findMentee == null){
//            System.out.println("mentee is null!");
//            return false;
//        }
//
//        if(!findMentee.getBasicInfo().getPassword().equals(mentee.getBasicInfo().getPassword())){
//            System.out.println("not right password");
//            return false;
//        }
//
//        return true;
//    }

}
