package com.jiafan.Fdbutils;

import android.content.Context;

public class mybean extends BaseFdbUtilsbean{

	
	
	public mybean(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public String name;
	public String age;
	public String sex;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	

	
}
