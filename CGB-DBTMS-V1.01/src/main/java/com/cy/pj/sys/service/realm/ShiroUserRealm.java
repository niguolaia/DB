package com.cy.pj.sys.service.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cy.pj.sys.dao.SysMenuDao;
import com.cy.pj.sys.dao.SysRoleMenuDao;
import com.cy.pj.sys.dao.SysUserDao;
import com.cy.pj.sys.dao.SysUserRoleDao;
import com.cy.pj.sys.entity.SysUser;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ShiroUserRealm extends AuthorizingRealm {
	@Autowired
	private SysUserDao sysUserDao;
	/**
	 * 设置加密算法及加密次数等.
	 */
	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		HashedCredentialsMatcher hcm=new HashedCredentialsMatcher();
		hcm.setHashAlgorithmName("MD5");
		hcm.setHashIterations(1024);
		super.setCredentialsMatcher(hcm);
	}
	/**负责认证信息的获取及封装
	 * @param token 包含用户名和密码*/
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		//1.获取用户名
		UsernamePasswordToken upToken=
		(UsernamePasswordToken)token;
		String username=upToken.getUsername();
		//2.基于用户名查询数据库
		SysUser user=
		sysUserDao.findUserByUserName(username);
		//3.判定用户是否存在
		if(user==null)
		throw new UnknownAccountException();
		//4.判定用户是被禁用
		if(user.getValid()==0)
		throw new LockedAccountException();
		//5.封装用户信息并返回
		ByteSource credentialsSalt=
		ByteSource.Util.bytes(user.getSalt());
		SimpleAuthenticationInfo info=
		new SimpleAuthenticationInfo(
				user,//principal用户身份
				user.getPassword(), //hashedCredentials 已加密的密码
				credentialsSalt,
				getName());//返回给SecurityManager(本身认证和授权功能)
		return info;
	}
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;
	@Autowired
	private SysMenuDao sysMenuDao;
	/**负责授权信息的获取及封装*/
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
		PrincipalCollection principals) {
		log.info("***doGetAuthorizationInfo***");
		//1.获取登录用户id
		SysUser user=(SysUser)
		principals.getPrimaryPrincipal();
		Integer userId=user.getId();
		//2.基于登录用户id获取用户的角色id
		List<Integer> roleIds=
		sysUserRoleDao.findRoleIdsByUserId(userId);
		if(roleIds==null||roleIds.size()==0)
		throw new AuthorizationException();
		//3.基于角色id获取菜单id
		Integer[] array= {};
		List<Integer> menuIds=
		sysRoleMenuDao.findMenuIdsByRoleIds(roleIds.toArray(array));
		if(menuIds==null||menuIds.size()==0)
		throw new AuthorizationException();
		//4.基于菜单id获取权限标识
		List<String> permissions=
		sysMenuDao.findPermissions(menuIds.toArray(array));
		if(permissions==null||permissions.size()==0)
		throw new AuthorizationException();
		//5.封装权限标识并返回.
		Set<String> stringPermissions=
		new HashSet<>();
		for(String p:permissions) {
			if(!StringUtils.isEmpty(p)) {
				stringPermissions.add(p);
			}
		}
		SimpleAuthorizationInfo info=
		new SimpleAuthorizationInfo();
		info.setStringPermissions(stringPermissions);
		return info;//返回给授权管理器
	}

}







