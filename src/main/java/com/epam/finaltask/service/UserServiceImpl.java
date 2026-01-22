package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.CustomEntityNotFoundException;
import com.epam.finaltask.exception.EntityAlreadyExistsException;
import com.epam.finaltask.exception.StatusCodes;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public void checkIfUserExists(String username) {
		if (userRepository.existsByUsername(username)) {
			throw new EntityAlreadyExistsException("This username is already exist",
					StatusCodes.DUPLICATE_USERNAME.name());
		}
	}

	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username)
				.orElseThrow(() -> new CustomEntityNotFoundException(
						"Entity not found", StatusCodes.ENTITY_NOT_FOUND.name()));
	}

	@Override
	public UserDTO register(UserDTO userDTO) {
		checkIfUserExists(userDTO.getUsername());
		User user = userMapper.toUser(userDTO);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		User user = findUserByUsername(username);
		User updatedUser = userMapper.toUser(userDTO);

		user.setUsername(updatedUser.getUsername());
		user.setRoles(updatedUser.getRoles());
		user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		user.setBalance(userDTO.getBalance());
		user.setPhoneNumber(updatedUser.getPhoneNumber());

		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		User user = findUserByUsername(username);
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		UUID userId = UUID.fromString(userDTO.getId());
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new CustomEntityNotFoundException(
						"Entity not found",
						StatusCodes.ENTITY_NOT_FOUND.name()
				));
		User updatedUser = userMapper.toUser(userDTO);
		user.setActive(updatedUser.isActive());

		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new CustomEntityNotFoundException("Entity not found",
						StatusCodes.ENTITY_NOT_FOUND.name()));
		return userMapper.toUserDTO(user);
	}
}
