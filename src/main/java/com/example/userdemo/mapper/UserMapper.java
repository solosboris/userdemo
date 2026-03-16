package com.example.userdemo.mapper;

import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UserDTO;
import com.example.userdemo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "status", source = "status", defaultValue="INACTIVE")
    @Mapping(
        target = "createdAt",
        source = "createdAt",
        dateFormat = "dd-MM-yyyy HH:mm:ss"
    )
    @Mapping(
        target = "updatedAt",
        source = "updatedAt",
        dateFormat = "dd-MM-yyyy HH:mm:ss"
    )
    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

}