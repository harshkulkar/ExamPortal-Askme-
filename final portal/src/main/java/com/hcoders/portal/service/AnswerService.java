package com.hcoders.portal.service;

import java.util.Optional;

import com.hcoders.portal.model.Answer;

public interface AnswerService {
	Answer save(Answer answer);

	Answer saveAndFlush(Answer answer);

	Optional<Answer> findById(long answerId);

	void deleteById(Long answerId);
}
