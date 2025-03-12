package com.hcoders.portal.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.Test;
import com.hcoders.portal.repository.TestRepository;
import com.hcoders.portal.service.TestService;

@Service
@Qualifier("testServiceImpl")
public class TestServiceImpl implements TestService {

	@Autowired
	private TestRepository testRepository;

	@Override
	public List<Test> findAllByCreaterId(Long id) {
		return testRepository.findAllByCreaterId(id);
	}

	@Override
	public void addTest(Test test) {
		testRepository.save(test);
	}

	@Override
	public Optional<Test> findById(Long id) {
		return testRepository.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		testRepository.deleteById(id);
	}

	@Override
	public Test save(Test test) {
		return testRepository.save(test);
	}

	@Override
	public Boolean isAccessableByUser(Long testId, Long userId) {
		return testRepository.isAccessableByUser(testId, userId);
	}

	@Override
	public void setTestAccess(Long testId, Long userId, int access) {
		testRepository.setTestAccess(testId, userId, access);
	}

	@Override
	public void setTotalMarkForTest(double totalMark, Long testId) {
		testRepository.setTotalMarkForTest(totalMark, testId);
	}

}
