package com.hcoders.portal.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormView {

	private int number;
	private int[] correctAnswers;
	private Question[] testQuestions;
	private User[] testCandidates;
	private List<Test> testsView = new ArrayList<Test>();

	private String username;
	private String testName;
	private Date endDate;
	private double totalMark;
	private double grade;
	private boolean passed;

	private String fromEmail;
	private String emailPassword;

	public FormView() {
		super();
	}

	public FormView(String username, String testName, Date endDate, double totalMark, double grade, boolean passed) {
		super();
		this.username = username;
		this.testName = testName;
		this.endDate = endDate;
		this.totalMark = totalMark;
		this.grade = grade;
		this.passed = passed;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int[] getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(int[] correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public Question[] getTestQuestions() {
		return testQuestions;
	}

	public void setTestQuestions(Question[] testQuestions) {
		this.testQuestions = testQuestions;
	}

	public User[] getTestCandidates() {
		return testCandidates;
	}

	public void setTestCandidates(User[] testCandidates) {
		this.testCandidates = testCandidates;
	}

	public List<Test> getTestsView() {
		return testsView;
	}

	public void setTestsView(List<Test> testsView) {
		this.testsView = testsView;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Date getEndDate() {
		return endDate;
	}

	public double getTotalMark() {
		return totalMark;
	}

	public void setTotalMark(double totalMark) {
		this.totalMark = totalMark;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

}
