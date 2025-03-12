package com.hcoders.portal.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.ConfirmationToken;
import com.hcoders.portal.model.User;
import com.hcoders.portal.repository.ConfirmationTokenRepository;
import com.hcoders.portal.repository.UserRepository;
import com.hcoders.portal.service.ConfirmationTokenService;

import jakarta.transaction.Transactional;

@Service
@Qualifier("confirmationTokenServiceImpl")
@Transactional
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public List<Long> findUsersIdsWithUnusedToken(Long adminId) {
		return confirmationTokenRepository.findUsersIdsWithUnusedToken(adminId);
	}

	public List<User> findUsersWithUnusedToken(Long adminId) {
		List<Long> usersIdsWithUnusedToken = findUsersIdsWithUnusedToken(adminId);
		List<User> usersWithUnusedToken = new ArrayList<User>();
		for (Long userId : usersIdsWithUnusedToken) {
			Optional<User> user = userRepository.findById(userId);
			if (user.isPresent())
				usersWithUnusedToken.add(user.get());
		}
		return usersWithUnusedToken;
	}

	@Override
	public ConfirmationToken save(ConfirmationToken confirmationToken) {
		return confirmationTokenRepository.save(confirmationToken);
	}

	@Override
	public void removeUserOfConfirmationToken(Long userId) {
		confirmationTokenRepository.removeUserOfConfirmationToken(userId);
	}

	@Override
	public void deleteByUserId(Long userId) {
		confirmationTokenRepository.deleteByUser_Id(userId);
	}

}
