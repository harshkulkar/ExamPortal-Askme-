package com.hcoders.portal.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.Question;
import com.hcoders.portal.repository.QuestionRepository;
import com.hcoders.portal.service.QuestionService;

import jakarta.transaction.Transactional;

@Service
@Qualifier("questionServiceImpl")
@Transactional
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	private QuestionRepository questionRepository;

	@Override
	public List<Question> findByTestId(Long testId) {
		return questionRepository.findByTest_Id(testId);
	}

	@Override
	public Question save(Question question) {
		return questionRepository.save(question);
	}

	@Override
	public Optional<Question> findById(Long questionId) {
		return questionRepository.findById(questionId);
	}

	@Override
	public void deleteById(Long quesstionId) {
		questionRepository.deleteById(quesstionId);
	}

}
