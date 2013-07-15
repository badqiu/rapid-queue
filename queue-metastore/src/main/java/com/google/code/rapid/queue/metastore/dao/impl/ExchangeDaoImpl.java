/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.dao.ExchangeDao;
import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.query.ExchangeQuery;

/**
 * tableName: rq_exchange
 * [Exchange] 的Dao操作 
 *  
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public class ExchangeDaoImpl extends BaseSpringJdbcDao implements ExchangeDao{
	
	private RowMapper<Exchange> entityRowMapper = new BeanPropertyRowMapper<Exchange>(getEntityClass());
	
	static final private String COLUMNS = "exchange_name,vhost_name,remarks,durable_type,auto_delete,auto_delete_expires,type,size,memory_size,max_size,created_time,operator,last_updated_time,enabled";
	static final private String SELECT_FROM = "select " + COLUMNS + " from rq_exchange";
	
	@Override
	public Class<Exchange> getEntityClass() {
		return Exchange.class;
	}
	
	@Override
	public String getIdentifierPropertyName() {
		return "exchangeName";
	}
	
	public RowMapper<Exchange> getEntityRowMapper() {
		return entityRowMapper;
	}
	
	public void insert(Exchange entity) {
		entity.setCreatedTime(new Date());
		String sql = "insert into rq_exchange " 
			 + " (exchange_name,vhost_name,remarks,durable_type,auto_delete,auto_delete_expires,type,size,memory_size,max_size,created_time,operator,last_updated_time,enabled) " 
			 + " values "
			 + " (:exchangeName,:vhostName,:remarks,:durableType,:autoDelete,:autoDeleteExpires,:type,:size,:memorySize,:maxSize,:createdTime,:operator,:lastUpdatedTime,:enabled)";
//		insertWithGeneratedKey(entity,sql); //for sqlserver:identity and mysql:auto_increment
		
		//其它主键生成策略
		//insertWithOracleSequence(entity,"sequenceName",sql); //oracle sequence: 
		//insertWithDB2Sequence(entity,"sequenceName",sql); //db2 sequence:
		//insertWithUUID(entity,sql); //uuid
		insertWithAssigned(entity,sql); //手工分配
	}
	
	public int update(Exchange entity) {
		entity.setLastUpdatedTime(new Date());
		String sql = "update rq_exchange set "
					+ " remarks=:remarks,durable_type=:durableType,auto_delete=:autoDelete,auto_delete_expires=:autoDeleteExpires,type=:type,size=:size,memory_size=:memorySize,max_size=:maxSize,created_time=:createdTime,operator=:operator,last_updated_time=:lastUpdatedTime,enabled=:enabled "
					+ " where  exchange_name = :exchangeName and vhost_name = :vhostName ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}
	
	public int deleteById(String exchangeName, String vhostName) {
		String sql = "delete from rq_exchange where  exchange_name = ? and vhost_name = ? ";
		return  getSimpleJdbcTemplate().update(sql,  exchangeName,vhostName);
	}

	public Exchange getById(String exchangeName, String vhostName) {
		String sql = SELECT_FROM + " where  exchange_name = ? and vhost_name = ? ";
		return (Exchange)DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(),exchangeName,vhostName));
	}
	

	public Page<Exchange> findPage(ExchangeQuery query) {
		
		StringBuilder sql = new StringBuilder("select "+ COLUMNS + " from rq_exchange where 1=1 ");
		if(isNotEmpty(query.getExchangeName())) {
            sql.append(" and exchange_name = :exchangeName ");
        }
		if(isNotEmpty(query.getVhostName())) {
            sql.append(" and vhost_name = :vhostName ");
        }
		if(isNotEmpty(query.getRemarks())) {
            sql.append(" and remarks = :remarks ");
        }
		if(isNotEmpty(query.getDurableType())) {
            sql.append(" and durable_type = :durableType ");
        }
		if(isNotEmpty(query.getAutoDelete())) {
            sql.append(" and auto_delete = :autoDelete ");
        }
		if(isNotEmpty(query.getAutoDeleteExpires())) {
            sql.append(" and auto_delete_expires = :autoDeleteExpires ");
        }
		if(isNotEmpty(query.getType())) {
            sql.append(" and type = :type ");
        }
		if(isNotEmpty(query.getSize())) {
            sql.append(" and size = :size ");
        }
		if(isNotEmpty(query.getMemorySize())) {
            sql.append(" and memory_size = :memorySize ");
        }
		if(isNotEmpty(query.getMaxSize())) {
            sql.append(" and max_size = :maxSize ");
        }
		if(isNotEmpty(query.getCreatedTimeBegin())) {
		    sql.append(" and created_time >= :createdTimeBegin ");
		}
		if(isNotEmpty(query.getCreatedTimeEnd())) {
            sql.append(" and created_time <= :createdTimeEnd ");
        }
		if(isNotEmpty(query.getOperator())) {
            sql.append(" and operator = :operator ");
        }
		if(isNotEmpty(query.getLastUpdatedTimeBegin())) {
		    sql.append(" and last_updated_time >= :lastUpdatedTimeBegin ");
		}
		if(isNotEmpty(query.getLastUpdatedTimeEnd())) {
            sql.append(" and last_updated_time <= :lastUpdatedTimeEnd ");
        }
		
        //sql.append(" order by :sortColumns ");
		
		return pageQuery(sql.toString(),query,getEntityRowMapper());				
	}

	@Override
	public List<Exchange> findByVhostName(String vhostName) {
		return getSimpleJdbcTemplate().query(SELECT_FROM+" where vhost_name = ?", getEntityRowMapper(), vhostName);
	}
	
}
