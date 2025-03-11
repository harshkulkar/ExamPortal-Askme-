package com.hcoders.portal.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_user")
public class AdminUser {

	@EmbeddedId
	private AdminUserId adminUserId;

	public AdminUserId getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(AdminUserId adminUserId) {
		this.adminUserId = adminUserId;
	}

}
