package com.google.code.rapid.queue.metastore.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DB2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

import cn.org.rapid_framework.beanutils.BeanUtils;
import cn.org.rapid_framework.beanutils.PropertyUtils;
import cn.org.rapid_framework.jdbc.dialect.Dialect;
import cn.org.rapid_framework.jdbc.support.OffsetLimitResultSetExtractor;
import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.util.ObjectUtils;
import cn.org.rapid_framework.util.SqlRemoveUtils;
import cn.org.rapid_framework.util.page.PageQuery;
import cn.org.rapid_framework.util.page.Paginator;
/**
 * Spring的JDBC基类
 * @author badqiu
 *
 */
public abstract class BaseSpringJdbcDao extends JdbcDaoSupport {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected SimpleJdbcTemplate simpleJdbcTemplate;
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	protected Class getEntityClass(){
		throw new UnsupportedOperationException("not yet implements");
	}
	
	//用于分页的dialect,在线参考: http://code.google.com/p/rapid-framework/wiki/rapid_dialect
	private Dialect dialect;
	
	public void setDialect(Dialect d) {
		this.dialect = d;
	}
	
	protected void checkDaoConfig() {
		super.checkDaoConfig();
		if(dialect == null) throw new IllegalStateException("'dialect' property must be not null");
		log.info("use jdbc dialect:"+dialect);
		simpleJdbcTemplate = new SimpleJdbcTemplate(getJdbcTemplate());
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
	}
	
	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	protected void checkSingleRowAffected(String sql,int rowsAffected) throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
		checkRowAffected(sql,rowsAffected,1);
	}
	
	/**
	 * 检查update调用的rowsAffected必须为正确的行数
	 * @param sql
	 * @param rowsAffected
	 * @param requiredRowsAffected
	 * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
	 */
	public void checkRowAffected(String sql,int rowsAffected,int requiredRowsAffected) throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
		if (requiredRowsAffected > 0 && rowsAffected != requiredRowsAffected) {
			throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(sql, requiredRowsAffected, rowsAffected);
		}
	}
	
	@SuppressWarnings("all")
	public Page pageQuery(String sql, PageQuery pageQuery,RowMapper rowMapper) {
		Map paramMap = new HashMap(PropertyUtils.describe(pageQuery));
		return pageQuery(sql,paramMap,queryTotalItems(sql, paramMap),pageQuery.getPageSize(),pageQuery.getPage(),rowMapper);
	}
	
	@SuppressWarnings("all")
	public Page pageQuery(String sql, Map paramMap,int pageSize, int pageNumber, RowMapper rowMapper) {
		return pageQuery(sql,paramMap,queryTotalItems(sql, paramMap),pageSize,pageNumber,rowMapper);
	}
	
	@SuppressWarnings("all")
	private Page pageQuery(String sql, Map paramMap, final int totalItems,int pageSize, int pageNumber, RowMapper rowMapper) {
		if(totalItems <= 0) {
			return new Page(pageNumber,pageSize,0);
		}
		Paginator paginator = new Paginator(pageNumber, pageSize, totalItems);
		List list = pageQueryForList(sql, paramMap,paginator.getOffset(),pageSize,rowMapper);
		return new Page(pageNumber,pageSize,totalItems,list);
	}
	
	private int queryTotalItems(String querySql,Map paramMap) {
		//FIXME 未处理group by的 select count(*) from (subquery)
		//FIXME 性能需要提升,没有group by的情况下，不要采用子查询
//		String removedOrderByQuery = "select count(*) from ( " + SqlRemoveUtils.removeOrders(querySql) + " ) as c ";
		String removedOrderByQuery = "select count(*) " + SqlRemoveUtils.removeSelect(SqlRemoveUtils.removeOrders(querySql));
		return getNamedParameterJdbcTemplate().queryForInt(removedOrderByQuery,new MapSqlParameterSource((Map)paramMap));
	}
	
	static final String LIMIT_PLACEHOLDER = ":__limit";
	static final String OFFSET_PLACEHOLDER = ":__offset";
	protected List pageQueryForList(String sql, final Map paramMap, int startRow,int pageSize, final RowMapper rowMapper) {
		//支持limit查询
		if(dialect.supportsLimit()) {
			paramMap.put(LIMIT_PLACEHOLDER.substring(1), pageSize);
			
			//支持limit及offset.则完全使用数据库分页
			if(dialect.supportsLimitOffset()) {
				paramMap.put(OFFSET_PLACEHOLDER.substring(1), startRow);
				sql = dialect.getLimitString(sql,startRow,OFFSET_PLACEHOLDER,pageSize,LIMIT_PLACEHOLDER);
				startRow = 0;
			}else {
				//不支持offset,则在后面查询中使用游标配合limit分页
				sql = dialect.getLimitString(sql, 0,null, pageSize,LIMIT_PLACEHOLDER);
			}
			
			pageSize = Integer.MAX_VALUE;
		}
		return (List)getNamedParameterJdbcTemplate().query(sql, paramMap, new OffsetLimitResultSetExtractor(startRow,pageSize,rowMapper));
	}
	
	///// insert with start
	/**
	 * 适用sqlserver:identity,mysql:auto_increment 自动生成主键
	 */
	public int insertWithGeneratedKey(Object entity, String insertSql) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int affectedRows = getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity) , keyHolder);
		setIdentifierProperty(entity, keyHolder.getKey().longValue());
		return affectedRows;
	}
	
	public int insertWithIdentity(Object entity,String insertSql) {
		return insertWithGeneratedKey(entity, insertSql);
	}
	
	public int insertWithAutoIncrement(Object entity,String insertSql) {
		return insertWithIdentity(entity,insertSql);
	}

	public int insertWithSequence(Object entity,AbstractSequenceMaxValueIncrementer sequenceIncrementer,String insertSql) {
		Long id = sequenceIncrementer.nextLongValue();
		setIdentifierProperty(entity, id);
		return getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity));
	}
	
	public int insertWithDB2Sequence(Object entity,String sequenceName,String insertSql) {
		return insertWithSequence(entity, new DB2SequenceMaxValueIncrementer(getDataSource(),sequenceName), insertSql);
	}
	
	public int insertWithOracleSequence(Object entity,String sequenceName,String insertSql) {
		return insertWithSequence(entity, new OracleSequenceMaxValueIncrementer(getDataSource(),sequenceName), insertSql);
	}
	
	public int insertWithUUID(Object entity,String insertSql) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		setIdentifierProperty(entity, uuid);
		return getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity));
	}
	/**
	 * 手工分配ID插入
	 * @param entity
	 * @param insertSql
	 */
	public int insertWithAssigned(Object entity,String insertSql) {
		return getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity));
	}
	///// insert with end
	
	/**
	 * 得到主键对应的property
	 */
	protected String getIdentifierPropertyName() {
		throw new UnsupportedOperationException("not yet implements");
	}
	
	/**
	 * 设置实体的主键值
	 */
	public void setIdentifierProperty(Object entity, Object id) {
		try {
			BeanUtils.setProperty(entity, getIdentifierPropertyName(), id);
		} catch (Exception e) {
			throw new IllegalStateException("cannot set property value:"+id+" on entityClass:"+entity.getClass()+" by propertyName:"+getIdentifierPropertyName(),e);
		}
	}
	
	/**
	 * 得到实体的主键值
	 */
	public Object getIdentifierPropertyValue(Object entity) {
		try {
			return PropertyUtils.getProperty(entity, getIdentifierPropertyName());
		} catch (Exception e) {
			throw new IllegalStateException("cannot get property value on entityClass:"+entity.getClass()+" by propertyName:"+getIdentifierPropertyName(),e);
		}
	}
	
	public static boolean isNotEmpty(Object c) {
		return ObjectUtils.isNotEmpty(c);
	}
}
