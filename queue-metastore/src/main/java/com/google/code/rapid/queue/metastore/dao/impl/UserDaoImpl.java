/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao.impl;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.dao.UserDao;
import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.metastore.query.UserQuery;

/**
 * tableName: rq_user
 * [User] 的Dao操作 
 *  
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public class UserDaoImpl extends BaseSpringJdbcDao implements UserDao{
	
	private RowMapper<User> entityRowMapper = new BeanPropertyRowMapper<User>(getEntityClass());
	
	static final private String COLUMNS = "username,password,remarks,email,mobile";
	static final private String SELECT_FROM = "select " + COLUMNS + " from rq_user";
	
	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}
	
	@Override
	public String getIdentifierPropertyName() {
		return "username";
	}
	
	public RowMapper<User> getEntityRowMapper() {
		return entityRowMapper;
	}
	
	public void insert(User entity) {
		String sql = "insert into rq_user " 
			 + " (username,password,remarks,email,mobile) " 
			 + " values "
			 + " (:username,:password,:remarks,:email,:mobile)";
		insertWithGeneratedKey(entity,sql); //for sqlserver:identity and mysql:auto_increment
		
		//其它主键生成策略
		//insertWithOracleSequence(entity,"sequenceName",sql); //oracle sequence: 
		//insertWithDB2Sequence(entity,"sequenceName",sql); //db2 sequence:
		//insertWithUUID(entity,sql); //uuid
		//insertWithAssigned(entity,sql) //手工分配
	}
	
	public int update(User entity) {
		String sql = "update rq_user set "
					+ " password=:password,remarks=:remarks,email=:email,mobile=:mobile "
					+ " where  username = :username ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}
	
	public int deleteById(String username) {
		String sql = "delete from rq_user where  username = ? ";
		return  getSimpleJdbcTemplate().update(sql,  username);
	}

	public User getById(String username) {
		String sql = SELECT_FROM + " where  username = ? ";
		return (User)DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(),username));
	}
	

	public Page<User> findPage(UserQuery query) {
		
		StringBuilder sql = new StringBuilder("select "+ COLUMNS + " from rq_user where 1=1 ");
		if(isNotEmpty(query.getUsername())) {
            sql.append(" and username = :username ");
        }
		if(isNotEmpty(query.getPassword())) {
            sql.append(" and password = :password ");
        }
		if(isNotEmpty(query.getRemarks())) {
            sql.append(" and remarks = :remarks ");
        }
		if(isNotEmpty(query.getEmail())) {
            sql.append(" and email = :email ");
        }
		if(isNotEmpty(query.getMobile())) {
            sql.append(" and mobile = :mobile ");
        }
		
        //sql.append(" order by :sortColumns ");
		
		return pageQuery(sql.toString(),query,getEntityRowMapper());				
	}
}
