package com.hcoders.portal.service.serviceImpl;

import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.Answer;
import com.hcoders.portal.repository.AnswerRepository;
import com.hcoders.portal.service.AnswerService;

import jakarta.transaction.Transactional;

@Service
@Qualifier("answerServiceImpl")
@Transactional
public class AnswerServiceImpl implements AnswerService {

	@Autowired
	private AnswerRepository answerRepository;

	@Override
	public Answer save(Answer answer) {
		return answerRepository.save(answer);
	}

	public Answer saveAndFlush(Answer answer) {
		return answerRepository.saveAndFlush(answer);
	}

	@Override
	public Optional<Answer> findById(long answerId) {
		return answerRepository.findById(answerId);
	}

	@Override
	public void deleteById(Long answerId) {
		answerRepository.deleteById(answerId);
	}

}
