package youandme.youandme.service;

import org.springframework.data.jpa.repository.Modifying;
import youandme.youandme.domain.Like;
import youandme.youandme.domain.Mentee;
import youandme.youandme.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;

    @Transactional
    public void save(Like like){
        validateDuplicateLike(like);
    }

    @Transactional
    public List<Like> findLike(Long mentee_index){
        return likeRepository.findLike(mentee_index);
    }

    @Transactional
    public List<Like> findLiked(Long mentor_index){

        return likeRepository.findLiked(mentor_index);
    }


    public List<Like> findUnliked(Long mentee_id, Long mentor_id){
        return likeRepository.findUnliked(mentee_id, mentor_id);
    }

    @Transactional
    public void unliked(Like unlike){
        likeRepository.unliked(unlike);
    }


    private void validateDuplicateLike(Like like) {
        List<Like> findLikes = likeRepository.findLike(like.getMentee_index());
        if(!findLikes.isEmpty()){
            for( Like like1 : findLikes){
                if(like1.getMentor_index() ==  like.getMentor_index()){

                }
                else{
                    likeRepository.save(like);
                }
            }
        }else{
            likeRepository.save(like);
        }


    }

}
