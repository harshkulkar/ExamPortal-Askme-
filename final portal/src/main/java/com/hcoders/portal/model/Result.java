package com.hcoders.portal.model;

import java.time.LocalDateTime;



import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Result {

	@Id
	@GeneratedValue(generator = "resultseq")
	@GenericGenerator(name = "resultseq", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@org.hibernate.annotations.Parameter(name = "value_column_name", value = "sequence_next_hi_value"),
			@org.hibernate.annotations.Parameter(name = "prefer_entity_table_as_segment_value", value = "true"),
			@org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo"),
			@org.hibernate.annotations.Parameter(name = "increment_size", value = "100") })
	private Long id;
	private Long testId;
	private String testName;
	@CreationTimestamp
	private LocalDateTime createDate;
	private double totalMark;
	private double grade;
	private boolean passed;
	private Long examineeId;

	public Result() {
	}

	public Result(Long testId, String testName, double grade, double totalMark, boolean passed, Long examineeId) {
		super();
		this.testId = testId;
		this.testName = testName;
		this.grade = grade;
		this.totalMark = totalMark;
		this.passed = passed;
		this.examineeId = examineeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public double getTotalMark() {
		return totalMark;
	}

	public void setTotalMark(double totalMark) {
		this.totalMark = totalMark;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public Long getExamineeId() {
		return examineeId;
	}

	public void setExamineeId(Long examineeId) {
		this.examineeId = examineeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Result other = (Result) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Result [id=" + id + ", testName=" + testName + ", createDate=" + createDate + ", grade=" + grade
				+ ", passed=" + passed + ", examineeId=" + examineeId + "]";
	}

}
