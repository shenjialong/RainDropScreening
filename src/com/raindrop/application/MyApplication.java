package com.raindrop.application;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {
	
	String age; 
	String gender;
	String  terminal;
	String relation;
	String userid;
	
//	����һ��activity������  ���浱ǰ�򿪵�����Activity������   �˳�ʱ ���ε���finish ʵ���˳�����  �����broadcastReceiver��ʽ ������  by sjl 2013 07 18 
	List <Activity> activityList=new ArrayList<Activity>();
	
	public void  addActivity(Activity activity){
		activityList.add(activity);
	}
	
	public void  closeActivitys(){
		for(int i=0;i<activityList.size();i++){
			if(activityList.get(i)!=null)
			activityList.get(i).finish();
		}
		System.exit(0);
	}
	
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	

}
