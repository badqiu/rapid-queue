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

import com.google.code.rapid.queue.metastore.dao.VhostDao;
import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.query.VhostQuery;

/**
 * tableName: rq_vhost
 * [Vhost] 的Dao操作 
 *  
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public class VhostDaoImpl extends BaseSpringJdbcDao implements VhostDao{
	
	private RowMapper<Vhost> entityRowMapper = new BeanPropertyRowMapper<Vhost>(getEntityClass());
	
	static final private String COLUMNS = "vhost_name,remarks,host";
	static final private String SELECT_FROM = "select " + COLUMNS + " from rq_vhost";
	
	@Override
	public Class<Vhost> getEntityClass() {
		return Vhost.class;
	}
	
	@Override
	public String getIdentifierPropertyName() {
		return "vhostName";
	}
	
	public RowMapper<Vhost> getEntityRowMapper() {
		return entityRowMapper;
	}
	
	public void insert(Vhost entity) {
		String sql = "insert into rq_vhost " 
			 + " (vhost_name,remarks,host) " 
			 + " values "
			 + " (:vhostName,:remarks,:host)";
		insertWithGeneratedKey(entity,sql); //for sqlserver:identity and mysql:auto_increment
		
		//其它主键生成策略
		//insertWithOracleSequence(entity,"sequenceName",sql); //oracle sequence: 
		//insertWithDB2Sequence(entity,"sequenceName",sql); //db2 sequence:
		//insertWithUUID(entity,sql); //uuid
		//insertWithAssigned(entity,sql) //手工分配
	}
	
	public int update(Vhost entity) {
		String sql = "update rq_vhost set "
					+ " remarks=:remarks,host=:host "
					+ " where  vhost_name = :vhostName ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}
	
	public int deleteById(String vhostName) {
		String sql = "delete from rq_vhost where  vhost_name = ? ";
		return  getSimpleJdbcTemplate().update(sql,  vhostName);
	}

	public Vhost getById(String vhostName) {
		String sql = SELECT_FROM + " where  vhost_name = ? ";
		return (Vhost)DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(),vhostName));
	}
	

	public Page<Vhost> findPage(VhostQuery query) {
		
		StringBuilder sql = new StringBuilder("select "+ COLUMNS + " from rq_vhost where 1=1 ");
		if(isNotEmpty(query.getVhostName())) {
            sql.append(" and vhost_name = :vhostName ");
        }
		if(isNotEmpty(query.getRemarks())) {
            sql.append(" and remarks = :remarks ");
        }
		if(isNotEmpty(query.getHost())) {
            sql.append(" and host = :host ");
        }
		
        //sql.append(" order by :sortColumns ");
		
		return pageQuery(sql.toString(),query,getEntityRowMapper());				
	}
}
