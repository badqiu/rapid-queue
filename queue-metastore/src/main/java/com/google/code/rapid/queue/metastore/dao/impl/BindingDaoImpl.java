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

import com.google.code.rapid.queue.metastore.dao.BindingDao;
import com.google.code.rapid.queue.metastore.model.Binding;
import com.google.code.rapid.queue.metastore.query.BindingQuery;

/**
 * tableName: rq_binding
 * [Binding] 的Dao操作 
 *  
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public class BindingDaoImpl extends BaseSpringJdbcDao implements BindingDao{
	
	private RowMapper<Binding> entityRowMapper = new BeanPropertyRowMapper<Binding>(getEntityClass());
	
	static final private String COLUMNS = "queue_name,exchange_name,vhost_name,router_key,remarks";
	static final private String SELECT_FROM = "select " + COLUMNS + " from rq_binding";
	
	@Override
	public Class<Binding> getEntityClass() {
		return Binding.class;
	}
	
	@Override
	public String getIdentifierPropertyName() {
		return "queueName";
	}
	
	public RowMapper<Binding> getEntityRowMapper() {
		return entityRowMapper;
	}
	
	public void insert(Binding entity) {
		String sql = "insert into rq_binding " 
			 + " (queue_name,exchange_name,vhost_name,router_key,remarks) " 
			 + " values "
			 + " (:queueName,:exchangeName,:vhostName,:routerKey,:remarks)";
		insertWithGeneratedKey(entity,sql); //for sqlserver:identity and mysql:auto_increment
		
		//其它主键生成策略
		//insertWithOracleSequence(entity,"sequenceName",sql); //oracle sequence: 
		//insertWithDB2Sequence(entity,"sequenceName",sql); //db2 sequence:
		//insertWithUUID(entity,sql); //uuid
		//insertWithAssigned(entity,sql) //手工分配
	}
	
	public int update(Binding entity) {
		String sql = "update rq_binding set "
					+ " router_key=:routerKey,remarks=:remarks "
					+ " where  queue_name = :queueName and exchange_name = :exchangeName and vhost_name = :vhostName ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}
	
	public int deleteById(String queueName, String exchangeName, String vhostName) {
		String sql = "delete from rq_binding where  queue_name = ? and exchange_name = ? and vhost_name = ? ";
		return  getSimpleJdbcTemplate().update(sql,  queueName,exchangeName,vhostName);
	}

	public Binding getById(String queueName, String exchangeName, String vhostName) {
		String sql = SELECT_FROM + " where  queue_name = ? and exchange_name = ? and vhost_name = ? ";
		return (Binding)DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(),queueName,exchangeName,vhostName));
	}
	

	public Page<Binding> findPage(BindingQuery query) {
		
		StringBuilder sql = new StringBuilder("select "+ COLUMNS + " from rq_binding where 1=1 ");
		if(isNotEmpty(query.getQueueName())) {
            sql.append(" and queue_name = :queueName ");
        }
		if(isNotEmpty(query.getExchangeName())) {
            sql.append(" and exchange_name = :exchangeName ");
        }
		if(isNotEmpty(query.getVhostName())) {
            sql.append(" and vhost_name = :vhostName ");
        }
		if(isNotEmpty(query.getRouterKey())) {
            sql.append(" and router_key = :routerKey ");
        }
		if(isNotEmpty(query.getRemarks())) {
            sql.append(" and remarks = :remarks ");
        }
		
        //sql.append(" order by :sortColumns ");
		
		return pageQuery(sql.toString(),query,getEntityRowMapper());				
	}
}
