package com.example.SecurityMicroservice.Utils;

import com.example.SecurityMicroservice.DTO.UserDTO;
import com.example.SecurityMicroservice.Models.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class UserMapper {
    public static User mapToUser (UserDTO userDTO){
        ModelMapper modelMapper = new ModelMapper();

        PropertyMap<UserDTO, User> propertyMap = new PropertyMap<> (){
            protected void configure() {
                map(source.getUserName()).setUserName(null);
//                map().setRoles(source.getRoles().stream()
//                        .map(role -> modelMapper.map(role, Role.class))
//                        .collect(Collectors.toSet()));
            }
        };

        modelMapper.addMappings(propertyMap);
        return modelMapper.map(userDTO, User.class);
    }

    public static UserDTO mapToDTO (User user){
        ModelMapper modelMapper= new ModelMapper();

        PropertyMap<User, UserDTO> propertyMap = new PropertyMap<> (){
            protected void configure() {

                map(source.getUsername()).setUserName(null);
//                map().setRoles(source.getRoles().stream()
//                        .map(role -> modelMapper.map(role, RoleDTO.class))
//                        .collect(Collectors.toList()));
            }
        };

        modelMapper.addMappings(propertyMap);
        return modelMapper.map(user, UserDTO.class);
    }
}
