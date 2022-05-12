package com.kakao.cafe.mapper;

import com.kakao.cafe.domain.User;
import com.kakao.cafe.domain.User.UserBuilder;
import com.kakao.cafe.dto.UserCreateRequest;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-05-13T00:49:15+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.3.2.jar, environment: Java 11.0.14.1 (Amazon.com Inc.)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateRequest user) {
        if ( user == null ) {
            return null;
        }

        UserBuilder user1 = User.builder();

        user1.userId( user.getUserId() );
        user1.password( user.getPassword() );
        user1.name( user.getName() );
        user1.email( user.getEmail() );

        return user1.build();
    }
}
