package com.hcoders.portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcoders.portal.model.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
