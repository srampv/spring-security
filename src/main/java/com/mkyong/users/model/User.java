package com.mkyong.users.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


public class User{private String username;
private String password;
private boolean enabled;
private Set<UserRole> userRole = new HashSet<UserRole>(0);
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
public Set<UserRole> getUserRole() {
	return userRole;
}
public void setUserRole(Set<UserRole> userRole) {
	this.userRole = userRole;
}

}
