package com.cy.pj.sys.controller;
import javax.validation.Valid;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cy.pj.common.vo.JsonResult;
import com.cy.pj.sys.entity.SysUser;
import com.cy.pj.sys.service.SysUserService;

@RestController
@RequestMapping("/user/")
public class SysUserController {
	@Autowired
	@Qualifier("sysUserServiceImpl")
	private SysUserService sysUserService;
	 
	@RequestMapping("doUpdatePassword")
	public JsonResult doUpdatePassword(
			 String pwd,
			 String newPwd,
			 String cfgPwd) {
		 sysUserService.updatePassword(pwd, newPwd, cfgPwd);
		 return new JsonResult("update ok");
	}
	
	@RequestMapping("doLogin")
	public JsonResult doLogin(
			String username,
			String password,
			boolean isRememberMe) {
		//1.获取Subject对象
		Subject subject=SecurityUtils.getSubject();
		//2.提交用户信息
		UsernamePasswordToken token=new UsernamePasswordToken();
		token.setUsername(username);
		token.setPassword(password.toCharArray());
		if(isRememberMe)token.setRememberMe(true);
		subject.login(token);//提交谁SecurityManager
		return new JsonResult("login ok");
	}
	
	@GetMapping("doFindObjectById")
	public JsonResult doFindObjectById(Integer id) {
		return new JsonResult(sysUserService.findObjectById(id));
	}
	@RequestMapping("doValidById")
	public JsonResult doValidById(
			Integer id,Integer valid) {
		sysUserService.validById(id, valid,
				"admin");
		return new JsonResult("update ok");
	}
	@PostMapping("doUpdateObject")
	public JsonResult doUpdateObject(
			SysUser entity,Integer[] roleIds) {
		sysUserService.updateObject(entity, roleIds);
		return new JsonResult("update ok");
	}
	@PostMapping("doSaveObject")
	public JsonResult doSaveObject(
			@Valid SysUser entity,Integer[] roleIds) {
		sysUserService.saveObject(entity, roleIds);
		return new JsonResult("save ok");
	}
	@RequestMapping("doFindPageObjects")
	public JsonResult doFindPageObjects(
			String username,
			Integer pageCurrent) {
		return new JsonResult(
		     sysUserService.findPageObjects(username,
				pageCurrent));
	}
	
}
