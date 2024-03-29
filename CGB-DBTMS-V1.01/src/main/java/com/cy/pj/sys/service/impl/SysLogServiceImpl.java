package com.cy.pj.sys.service.impl;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cy.pj.sys.dao.SysLogDao;
import com.cy.pj.sys.entity.SysLog;
import com.cy.pj.sys.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
/**
 * 业务逻辑层对象
 * 1)核心业务(日志分页查询,日志删除)
 * 2)扩展业务(权限控制,事务控制,...)
 */
@Slf4j
@Service
@Transactional
public class SysLogServiceImpl extends BasePageService<SysLog> implements SysLogService {
	private SysLogDao sysLogDao;
	@Autowired
	public SysLogServiceImpl(SysLogDao sysLogDao) {
		super(sysLogDao);
		this.sysLogDao=sysLogDao;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void saveObject(SysLog entity) {
		log.info("log.current.thread="+Thread.currentThread());
	    
		sysLogDao.insertObject(entity);
	}
	/**
	 * @RequiresPermissions("") 告诉spring
	 * 执行此方法需要权限控制.
	 */
	@RequiresPermissions("sys:log:delete")
	@Override
	public int deleteObjects(Integer... ids) {
		//1.参数校验
		if(ids==null||ids.length==0)
		throw new IllegalArgumentException("请先选择");
		//2.执行删除
		int rows=sysLogDao.deleteObjects(ids);
		log.info("delete rows "+rows);
		//3.返回结果
		return rows;
	}
	
}
