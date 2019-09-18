package com.cy.pj.sys.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cy.pj.common.annotation.RequiredLog;
import com.cy.pj.common.vo.PageObject;
import com.cy.pj.sys.dao.SysUserDao;
import com.cy.pj.sys.dao.SysUserRoleDao;
import com.cy.pj.sys.entity.SysUser;
import com.cy.pj.sys.service.SysUserService;
import com.cy.pj.sys.service.exception.ServiceException;
import com.cy.pj.sys.vo.SysUserDeptVo;

@Transactional(rollbackFor = Throwable.class
,isolation = Isolation.READ_COMMITTED,
 timeout =30,propagation = Propagation.REQUIRED)
@Service
public class SysUserServiceImpl 
       extends BasePageService<SysUserDeptVo> 
       implements SysUserService {
	 private SysUserDao sysUserDao;
	 private SysUserRoleDao sysUserRoleDao;
	 @Autowired
	 public SysUserServiceImpl(SysUserDao sysUserDao,
			 SysUserRoleDao sysUserRoleDao) {
		 super(sysUserDao);
		 this.sysUserDao=sysUserDao;
		 this.sysUserRoleDao=sysUserRoleDao;
	}
	 
	@Override
	public int updatePassword(
			String oldPassword, 
			String newPassword,
			String cfgPassword) {
	    //1.验证非空
		if(StringUtils.isEmpty(oldPassword))
		throw new IllegalArgumentException("原密码不能空");
		if(StringUtils.isEmpty(newPassword))
		throw new IllegalArgumentException("新密码不能空");
		//2.验证原密码是否正确
		SysUser user=(SysUser)
		SecurityUtils.getSubject().getPrincipal();
		SimpleHash sh=new SimpleHash("MD5",
		oldPassword,user.getSalt(),1024);
		if(!user.getPassword().equals(sh.toHex()))
		throw new IllegalArgumentException("输入的原密码不正确");
		//3.验证两次输入密码是否一致
		if(!newPassword.equals(cfgPassword))
		throw new IllegalArgumentException("两次密码输入不一致");
		//4.更新密码并返回结果
		String salt=UUID.randomUUID().toString();
	    sh=new SimpleHash("MD5",newPassword,salt, 1024);
		int rows=sysUserDao.updatePassword(sh.toHex(), salt,user.getId());
		return rows;
	}
	@Transactional(readOnly = true)
	@Override
	public Map<String, Object> findObjectById(Integer id) {
		 //1.参数校验
		 if(id==null||id<1)
		 throw new IllegalArgumentException("id值无效");
		 //2.查询用户以及对应的部门信息
		 SysUserDeptVo user=
		 sysUserDao.findObjectById(id);
		 //3.查找用户对应的角色信息
		 List<Integer> roleIds=
		 sysUserRoleDao.findRoleIdsByUserId(id);
		 //4.封装数据并返回
		 Map<String,Object> map=new HashMap<>();
		 map.put("user", user);
		 map.put("roleIds", roleIds);
		return map;
	}
	@Override
	public int updateObject(SysUser entity, Integer[] roleIds) {
		//1.参数校验
		if(entity==null)
		throw new IllegalArgumentException("保存对象不能为空");
		if(StringUtils.isEmpty(entity.getUsername()))
		throw new IllegalArgumentException("用户名不能为空");
		//2.写用户信息
		int rows=sysUserDao.updateObject(entity);
		//3.写用户角色关系数据
		sysUserRoleDao.deleteObjectsByUserId(entity.getId());
		sysUserRoleDao.insertObjects(entity.getId(),
				roleIds);
		//4.返回结果
		return rows;
	}
	
	@Override
	public int saveObject(SysUser entity, Integer[] roleIds) {
		//1.参数校验
		if(entity==null)
			throw new IllegalArgumentException("保存对象不能为空");
		//if(StringUtils.isEmpty(entity.getUsername()))
		//throw new IllegalArgumentException("用户名不能为空");
		if(StringUtils.isEmpty(entity.getPassword()))
			throw new IllegalArgumentException("密码不能为空");
		//2.写用户信息
		String salt=UUID.randomUUID().toString();
		SimpleHash sh=new SimpleHash(
				"MD5",//algorithmName 加密算法 
				entity.getPassword(), //source 没加密的密码
				salt, //盐值
				1024);//hashIterations
		entity.setSalt(salt);
		entity.setPassword(sh.toHex());
		int rows=sysUserDao.insertObject(entity);
		//3.写用户角色关系数据
		int userRoleRows=
		sysUserRoleDao.insertObjects(entity.getId(),
				roleIds);
		//if(userRoleRows>0)
		//throw new ServiceException("角色分配失败");
		//4.返回结果
		return rows;
	}
	@RequiresPermissions("sys:user:valid")
	@RequiredLog("禁用启用")
	@Override
	public int validById(Integer id, Integer valid, String modifiedUser) {
		if(id==null||id<1)
		throw new IllegalArgumentException("id值不合法");
		if(valid!=1&&valid!=0)
		throw new IllegalArgumentException("状态值不正确");
		//..
		int rows=sysUserDao.validById(id, valid, modifiedUser);
		if(rows==0)
		throw new ServiceException("记录可能已经存在");
		return rows;
	}
	@RequiresPermissions("sys:user:view")
	@Override
	public PageObject<SysUserDeptVo> findPageObjects(String name, Integer pageCurrent) {
		// TODO Auto-generated method stub
		return super.findPageObjects(name, pageCurrent);
	}
	

}
