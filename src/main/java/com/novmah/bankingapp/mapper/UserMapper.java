package com.novmah.bankingapp.mapper;

import com.novmah.bankingapp.dto.response.UserResponse;
import com.novmah.bankingapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression ="java(getFullName(user.getFirstName(), user.getLastName()))")
    UserResponse mapToDto(User user);

    default String getFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

}
