package com.novmah.bankingapp.mapper;

import com.novmah.bankingapp.dto.response.UserResponse;
import com.novmah.bankingapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression ="java(getFullName(user.getFirstName(), user.getLastName()))")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "modifiedAt", source = "user.modifiedAt")
    UserResponse mapToDto(User user);

    default String getFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

}
