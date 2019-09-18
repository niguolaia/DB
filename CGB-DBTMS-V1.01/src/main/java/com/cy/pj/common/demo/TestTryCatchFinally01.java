package com.cy.pj.common.demo;
public class TestTryCatchFinally01 {
	static int doMethod01() {//JVM 方法栈(栈帧)
		int a=10;
		try {
			//@Before
			a++;
			return a;//@AfterReturing
		}catch(Exception e) {
			a++;//@AfterThrowing
			return a;
		}finally {
			a++;//@After
		}
	}
	public static void main(String[] args) {
		int a=doMethod01();
		System.out.println(a);
		
	}
}
