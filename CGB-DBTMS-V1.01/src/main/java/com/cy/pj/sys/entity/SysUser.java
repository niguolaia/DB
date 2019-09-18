package com.cy.pj.sys.entity;
import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = false)
public class SysUser extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -7447006575577551655L;
	private Integer id;
	@NotBlank(message = "username can not be null")
	private String username;
	private String password;
	private String salt;//盐值
	@Email
	@NotBlank(message = "email can not be null")
	private String email;
	private String mobile;
	private Integer valid=1;
	@NotNull(message = "部门id不能为null")
	private Integer deptId;
}
