package com.hcoders.portal.service;

import java.util.List;
import java.util.Optional;

import com.hcoders.portal.model.Question;

public interface QuestionService {

	Optional<Question> findById(Long questionId);
	
	List<Question> findByTestId(Long testId);

	Question save(Question question);
	
	void deleteById(Long quesstionId);
}
