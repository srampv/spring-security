package com.seo.users.dao;

import com.seo.users.model.User;

public interface UserDao {

	User findByUserName(String username);

}