package com.hcoders.portal.model;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class User {
	@Id
	@GeneratedValue(generator = "userseq")
	@GenericGenerator(name = "userseq", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@org.hibernate.annotations.Parameter(name = "value_column_name", value = "sequence_next_hi_value"),
			@org.hibernate.annotations.Parameter(name = "prefer_entity_table_as_segment_value", value = "true"),
			@org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo"),
			@org.hibernate.annotations.Parameter(name = "increment_size", value = "100") })

	private long id;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String password;
	private String institution;
	private String role;
	private boolean enabled;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id", referencedColumnName = "id", nullable = true)
	private Address address= new Address();

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Create_Date", nullable = false, updatable = false)
	private Date createDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Modify_Date", nullable = false)
	private Date modifyDate;

	@OneToMany(mappedBy = "testCreator", cascade = CascadeType.ALL)
	private List<Test> createdTests = new ArrayList<Test>();

	@ManyToMany()
	@JoinTable(name = "users_tests", joinColumns = { @JoinColumn(name = "userTobeTested_id") }, inverseJoinColumns = {
			@JoinColumn(name = "test_id") })
	private List<Test> tests = new ArrayList<Test>();

	public User() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public List<Test> getCreatedTests() {
		return createdTests;
	}

	public void setCreatedTests(List<Test> createdTests) {
		this.createdTests = createdTests;
	}

	public List<Test> getTests() {
		return tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public boolean equals(Object obj) {
		// self check
		if (this == obj)
			return true;
		// null check
		if (obj == null)
			return false;
		// type check and cast
		if (getClass() != obj.getClass())
			return false;
		User user = (User) obj;
		// field comparison
		return username.equals(user.username) && id == user.getId();
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", enabled=" + enabled + "]";
	}

	
}