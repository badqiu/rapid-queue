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

import com.google.code.rapid.queue.metastore.dao.QueueDao;
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.query.QueueQuery;

/**
 * tableName: rq_queue
 * [Queue] 的Dao操作 
 *  
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public class QueueDaoImpl extends BaseSpringJdbcDao implements QueueDao{
	
	private RowMapper<Queue> entityRowMapper = new BeanPropertyRowMapper<Queue>(getEntityClass());
	
	static final private String COLUMNS = "queue_name,vhost_name,remarks,durable_type,auto_delete,auto_delete_expires,exclusive,size,memory_size,max_size,ttl,created_time,operator,last_updated_time";
	static final private String SELECT_FROM = "select " + COLUMNS + " from rq_queue";
	
	@Override
	public Class<Queue> getEntityClass() {
		return Queue.class;
	}
	
	@Override
	public String getIdentifierPropertyName() {
		return "queueName";
	}
	
	public RowMapper<Queue> getEntityRowMapper() {
		return entityRowMapper;
	}
	
	public void insert(Queue entity) {
		String sql = "insert into rq_queue " 
			 + " (queue_name,vhost_name,remarks,durable_type,auto_delete,auto_delete_expires,exclusive,size,memory_size,max_size,ttl,created_time,operator,last_updated_time) " 
			 + " values "
			 + " (:queueName,:vhostName,:remarks,:durableType,:autoDelete,:autoDeleteExpires,:exclusive,:size,:memorySize,:maxSize,:ttl,:createdTime,:operator,:lastUpdatedTime)";
//		insertWithGeneratedKey(entity,sql); //for sqlserver:identity and mysql:auto_increment
		
		//其它主键生成策略
		//insertWithOracleSequence(entity,"sequenceName",sql); //oracle sequence: 
		//insertWithDB2Sequence(entity,"sequenceName",sql); //db2 sequence:
		//insertWithUUID(entity,sql); //uuid
		insertWithAssigned(entity,sql); //手工分配
	}
	
	public int update(Queue entity) {
		String sql = "update rq_queue set "
					+ " remarks=:remarks,durable_type=:durableType,auto_delete=:autoDelete,auto_delete_expires=:autoDeleteExpires,exclusive=:exclusive,size=:size,memory_size=:memorySize,max_size=:maxSize,ttl=:ttl,created_time=:createdTime,operator=:operator,last_updated_time=:lastUpdatedTime "
					+ " where  queue_name = :queueName and vhost_name = :vhostName ";
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
	}
	
	public int deleteById(String queueName, String vhostName) {
		String sql = "delete from rq_queue where  queue_name = ? and vhost_name = ? ";
		return  getSimpleJdbcTemplate().update(sql,  queueName,vhostName);
	}

	public Queue getById(String queueName, String vhostName) {
		String sql = SELECT_FROM + " where  queue_name = ? and vhost_name = ? ";
		return (Queue)DataAccessUtils.singleResult(getSimpleJdbcTemplate().query(sql, getEntityRowMapper(),queueName,vhostName));
	}
	

	public Page<Queue> findPage(QueueQuery query) {
		
		StringBuilder sql = new StringBuilder("select "+ COLUMNS + " from rq_queue where 1=1 ");
		if(isNotEmpty(query.getQueueName())) {
            sql.append(" and queue_name = :queueName ");
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
		if(isNotEmpty(query.getExclusive())) {
            sql.append(" and exclusive = :exclusive ");
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
		if(isNotEmpty(query.getTtl())) {
            sql.append(" and ttl = :ttl ");
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
}
