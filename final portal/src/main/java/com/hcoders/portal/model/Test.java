package com.hcoders.portal.model;

import java.time.LocalDateTime;

import java.util.List;



import org.hibernate.annotations.CreationTimestamp;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Test {

	@Id
	@GeneratedValue(generator = "testseq")
	@GenericGenerator(name = "testseq", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@org.hibernate.annotations.Parameter(name = "value_column_name", value = "sequence_next_hi_value"),
			@org.hibernate.annotations.Parameter(name = "prefer_entity_table_as_segment_value", value = "true"),
			@org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo"),
			@org.hibernate.annotations.Parameter(name = "increment_size", value = "100") })
	private long id;
	private String name;
	private double totalMark;
	private int time;

	@JsonProperty(access = Access.WRITE_ONLY)
	@CreationTimestamp
	private LocalDateTime createDate;
	@UpdateTimestamp
	@JsonProperty(access = Access.WRITE_ONLY)
	private LocalDateTime modifyDate;
	@ManyToOne()
	@JoinColumn(name = "creator_id")
	@JsonProperty(access = Access.WRITE_ONLY)
	private User testCreator;

	@ManyToMany(mappedBy = "tests", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonProperty(access = Access.WRITE_ONLY)
	private List<User> usersTobeTested;

	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
	private List<Question> questions;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTotalMark() {
		return totalMark;
	}

	public void setTotalMark(double totalMark) {
		this.totalMark = totalMark;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(LocalDateTime instant) {
		this.modifyDate = instant;
	}

	public User getTestCreator() {
		return testCreator;
	}

	public void setTestCreator(User testCreator) {
		this.testCreator = testCreator;
	}

	public List<User> getUsersTobeTested() {
		return usersTobeTested;
	}

	public void setUsersTobeTested(List<User> usersTobeTested) {
		this.usersTobeTested = usersTobeTested;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Test other = (Test) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Test [id=" + id + ", name=" + name + ", time=" + time + ", testCreator=" + testCreator + "]";
	}

}
