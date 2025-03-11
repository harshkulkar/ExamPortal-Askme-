package com.hcoders.portal.model;

import java.util.Date;



import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Answer {

	@Id
	@GeneratedValue(generator = "answerseq")
	@GenericGenerator(name = "answerseq", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@org.hibernate.annotations.Parameter(name = "value_column_name", value = "sequence_next_hi_value"),
			@org.hibernate.annotations.Parameter(name = "prefer_entity_table_as_segment_value", value = "true"),
			@org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo"),
			@org.hibernate.annotations.Parameter(name = "increment_size", value = "100") })

	private long answerId;
	private String text;
	private boolean correct;
	@Column(columnDefinition = "boolean default false", insertable = false, updatable = false)
	private boolean selected;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Create_Date", nullable = false, updatable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private Date createDate;
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Modify_Date", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private Date modifyDate;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Question_Id", nullable = true, updatable = true)
	@JsonProperty(access = Access.WRITE_ONLY)
	private Question question;

	public Answer() {

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(long answerId) {
		this.answerId = answerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (answerId ^ (answerId >>> 32));
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
		Answer other = (Answer) obj;
		if (answerId != other.answerId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Answer [answerId=" + answerId + ", text=" + text + ", correct=" + correct + ", selected=" + selected
				+ "]";
	}

}
