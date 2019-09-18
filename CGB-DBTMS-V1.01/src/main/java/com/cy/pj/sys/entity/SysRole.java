package com.cy.pj.sys.entity;
import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class SysRole extends BaseEntity implements Serializable{
	private static final long serialVersionUID = 5444781487816683086L;
	private Integer id;
	@NotBlank
	private String name;
	private String note;
}
