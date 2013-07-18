package com.raindrop.screening;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.raindrop.application.MyApplication;
import com.raindrop.customview.NavigationBar;
import com.raindrop.util.Contants;

public class SplashActivity extends Activity {

	NavigationBar myNavigationbar;
	ListView questionTypeLV;
	MyApplication myapp;
//	SystemExitBroadcastReceiver exitReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		init();
	}
	
	public void init(){
//		exitReceiver=new SystemExitBroadcastReceiver();
		myapp=(MyApplication)getApplication();
		myapp.addActivity(SplashActivity.this);
		initNavigationbar();
		initQuestionTypeListview();
	}
	
	
	public void initNavigationbar(){
		myNavigationbar=(NavigationBar)findViewById(R.id.helpNb);
		myNavigationbar.setBtnRightVisble(false);
		myNavigationbar.setBtnLeftVisble(false);
		myNavigationbar.setTvTitle("��ѡ�����");
	}
	@Override
	public void onResume(){
		super.onResume();
		IntentFilter filter=new IntentFilter();
		filter.addAction(Contants.EXIT);
//		this.registerReceiver(exitReceiver, filter);
	}
	
	String age="δ��";
	String relation="δ��";
	String gender="δ��";
	int questionType=1;
	
	public void initQuestionTypeListview(){
//		���A(�ʺ�0��16���¶�ͯ)
//		���B(�ʺ�16��30���¶�ͯ)
//		���C(�ʺ�2�����϶�ͯ)
		final String [] adapterSource=new String[]{"���ӵ�����(����)","���ͺ��ӵĹ�ϵ","���ӵ��Ա�","��ʼ����"};
		final int [] dialogItemsID={R.array.age,R.array.relation,R.array.gender};
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,adapterSource);
		questionTypeLV=(ListView)findViewById(R.id.questionTypeLV);
		questionTypeLV.setAdapter(adapter);
		questionTypeLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {

				
				
				if(arg2==3){
					
					if("δ��".equals(age)){
						AlertDialog.Builder builder=new Builder(SplashActivity.this);
						builder.setTitle("��ʾ").setMessage("����Ҫѡ���ӵ�������ܿ�ʼ����").setPositiveButton("ȷ��", null).show();
					}else{
						Intent intent =new Intent(SplashActivity.this, MainActivity.class);
						intent.putExtra("questionType", questionType);
						String[]ages=getResources().getStringArray(R.array.age);
						myapp.setAge(age);
						myapp.setGender(gender);
						myapp.setRelation(relation);
						startActivity(intent);
//						SplashActivity.this.finish();
						overridePendingTransition(R.anim.fadein, R.anim.fadeout); 
					}
				}else{
					AlertDialog.Builder builder=new Builder(SplashActivity.this);
					builder.setTitle(adapterSource[arg2]).setSingleChoiceItems(dialogItemsID[arg2], 0,new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String answer=getResources().getStringArray(dialogItemsID[arg2])[which];
							Log.i("sjl","answer:"+answer);
							switch(arg2){
							case 0:
								questionType=which;
								age=answer;
							case 1:
								relation=answer;
							case 2:
								gender=answer; 
								default:
									break ;
							}
							dialog.dismiss();
						}
					}).show();
				}
				
			}
		});
		
		

		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_help, menu);
		return true;
	}

}
