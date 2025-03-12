package com.hcoders.portal.service.serviceImpl;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.User;
import com.hcoders.portal.repository.UserRepository;
import com.hcoders.portal.service.ResultService;
import com.hcoders.portal.service.TestService;
import com.hcoders.portal.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Qualifier("userServiceImpl")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	@Qualifier("testServiceImpl")
	private TestService testService;

	@Autowired
	@Qualifier("resultServiceImpl")
	private ResultService resultService;

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User save(User user, Long adminId) {
		User savedUser = userRepository.save(user);
		userRepository.assignUser(adminId, savedUser.getId());
		return savedUser;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public User update(User user) {
		return userRepository.save(user);
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);

	}

	@Override
	public User findByEmailIgnoreCase(String email) {
		return userRepository.findByEmailIgnoreCase(email);
	}

	@Override
	public User findByUsernameIgnoreCase(String username) {
		return userRepository.findByUsernameIgnoreCase(username);
	}

	@Override
	public List<Long> findAllIdsOfOwnUsers(long adminId) {
		return userRepository.findAllIdsOfOwnUsers(adminId);
	}

	public List<User> findAllUsersByAdminId(long adminId) {
		List<Long> idsOfUsersBelongToAdmin = userRepository.findAllIdsOfOwnUsers(adminId);
		List<User> users = new ArrayList<User>();
		for (Long id : idsOfUsersBelongToAdmin) {
			Optional<User> user = userRepository.findById(id);
			if (user.isPresent())
				users.add(user.get());
		}
		return users;
	}

	public List<User> findAllActiveUsersByAdminId(long adminId) {
		List<Long> idsOfUsersBelongToAdmin = userRepository.findAllIdsOfOwnUsers(adminId);
		List<User> users = new ArrayList<User>();
		for (Long id : idsOfUsersBelongToAdmin) {
			Optional<User> user = userRepository.findById(id);
			if (user.isPresent())
				if (user.get().isEnabled())
					users.add(user.get());
		}
		return users;
	}

	public List<User> findAllCandidatesForATestByAdminId(long testId, long adminId) {
		List<User> users = findAllActiveUsersByAdminId(adminId);

		List<User> usersSeeTest = users.stream().filter(user -> testService.isAccessableByUser(testId, user.getId()))
				.collect(Collectors.toList());

		users.removeAll(usersSeeTest);

		return users;
	}

	public Long findAdminOf(Long userId) {
		return userRepository.findAdminIdOf(userId);
	}

	@Override
	public void assignUser(long adminId, long userId) {
		userRepository.assignUser(adminId, userId);
	}

	@Override
	public void deleteByUsername(String username) {
		userRepository.deleteByUsername(username);

	}

	@Override
	public void deleteByEmail(String email) {
		userRepository.deleteByEmail(email);
	}

}