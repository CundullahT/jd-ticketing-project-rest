package com.cybertek.mapper;

import com.cybertek.dto.RoleDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Role;
import com.cybertek.entity.User;
import com.cybertek.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private ModelMapper modelMapper;
    @Autowired
    UserRepository userRepository;
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToEntity(UserDTO dto){

         return modelMapper.map(dto,User.class);

    }

    public UserDTO convertToDto(User entity){

        return modelMapper.map(entity,UserDTO.class);
    }
}
