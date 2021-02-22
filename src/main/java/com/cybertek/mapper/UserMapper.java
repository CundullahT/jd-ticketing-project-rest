package com.cybertek.mapper;

import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public UserMapper(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public User convertToEntity(UserDTO dto) {

        return modelMapper.map(dto, User.class);

    }

    public UserDTO convertToDto(User entity) {

        return modelMapper.map(entity, UserDTO.class);
    }

}
