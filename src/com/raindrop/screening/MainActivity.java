package com.raindrop.screening;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raindrop.application.MyApplication;
import com.raindrop.customview.NavigationBar;
import com.raindrop.util.Contants;
import com.raindrop.util.HttpUtil;
import com.umeng.fb.FeedbackAgent;

public class MainActivity extends Activity implements OnClickListener{

	 MyApplication  myapp;
     ImageButton imageBtnYes;
     ImageButton imageBtnNo;
     LinearLayout displayLL;
     LinearLayout displayPartLL;
     NavigationBar navigationBar;
     ImageView questionIv; 
     TextView questionTv;
     Map <Integer,String> questions;
     Map <String,Integer> answers;
     ProgressBar progressbar;
     String [] questionsArray;
     JSONObject json;
     int questionLength;
     int questionType;
     int currentNum;
     FeedbackAgent agent;
     
	 Handler myhandler=new Handler(){
		 @Override
		 public void handleMessage(Message msg) {
			      super.handleMessage(msg);
			      String jsonstring =msg.obj.toString();
//			      questionTv.setText(jsonstring);
	      }
	 };
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	
	public void initMyApplication(){
		myapp=(MyApplication)getApplication();
		myapp.addActivity(MainActivity.this);
		myapp.setTerminal("android");
		myapp.setUserid(getUuid());
	}
	private void init(){
		initMyApplication();
		initUI();
		initQuestions();
	}
	
	private void initUI(){
		
		 imageBtnYes=(ImageButton)findViewById(R.id.imageBtnYes);
	     imageBtnNo=(ImageButton)findViewById(R.id.imageBtnNo);
	     displayLL=(LinearLayout)findViewById(R.id.displayLL);
	     questionTv=(TextView)findViewById(R.id.questionTv);
	     progressbar=(ProgressBar)findViewById(R.id.progressBar1);
	     displayPartLL=(LinearLayout)findViewById(R.id.displayPartLL);
	     questionIv=(ImageView)findViewById(R.id.questionIv);
	     answers=new HashMap<String, Integer>();
	     agent = new FeedbackAgent(MainActivity.this);
	     agent.sync();
	     
	     setNavigationBar();
	     setImageButtonListener();
	}
	/**
	 * �����û�ID
	 * @return
	 */
    public String getUuid(){
//   	 �ж��Ƿ��ǵ�һ��ʹ��  
	   	SharedPreferences userInfo = MainActivity.this.getSharedPreferences("userInfo", 0);
	   	String userid=userInfo.getString("userid", "");
	   	if("".equals(userid)){
//	   		��һ��ʹ��
	   		TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE); 
    		String tmDevice = "" + tm.getDeviceId(); 
    		String tmSerial = "" + tm.getSimSerialNumber(); 
    		String  androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID); 
    		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode()); 
    		userid= deviceUuid.toString();
    		Editor editor=userInfo.edit();
	   		editor.putString("userid",userid).commit();
	   	}
	   	return userid;
    }
	
	private void setImageButtonListener(){
		imageBtnYes.setOnClickListener(this);
		imageBtnNo.setOnClickListener(this);
	}
	
	private void setNavigationBar(){
		
		navigationBar=(NavigationBar)findViewById(R.id.navigationBar1);
		navigationBar.setTvTitle("M-CHAT��");
		navigationBar.setBtnLeftVisble(false);
		navigationBar.setBtnRightText("����");
		navigationBar.setBtnRightClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder=new Builder(MainActivity.this);
				builder.setTitle("����").setItems(R.array.operations, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
							case 0:
//								���¿�ʼ����
								currentNum=0;
								setQuestionContent();
								Intent intent =new Intent(MainActivity.this, SplashActivity.class);
								startActivity(intent);
								finish();
								overridePendingTransition(R.anim.fadein, R.anim.fadeout); 
								Toast.makeText(MainActivity.this, "����ʼ����ɣ��뿪ʼ����", Toast.LENGTH_SHORT).show();
								break; 
							case 1:
							    agent.startFeedbackActivity();
								break; 
							case 2:
//								�˳�Ӧ��  �˴��Ѿ���Ϊ��myapp��ά����һ��activity���� ͳһ����  by sjl 2013 07 18 
//								�㲥֪ͨ����Activity �ر����лActivity
//								Intent exitIntent=new  Intent();
//								exitIntent.setAction(Contants.EXIT);
//								MainActivity.this.sendBroadcast(exitIntent);
								myapp.closeActivitys();
//								finish();   
//								overridePendingTransition(R.anim.fadein, R.anim.fadeout); 
								
//								������������� ������Դ  ���� ��̨�̹߳ر�  ���²��ܹ㲥�������� by sjl 20130627
//								System.exit(0);
								break;
						}
					}
				}).setPositiveButton("ȡ��", null).show();
				
			}
		});
	}
	
	private void initQuestions(){
		Intent intent=this.getIntent();
		questionType=intent.getIntExtra("questionType", 1);
		Resources res=this.getResources();
		
		switch (questionType) {
			case 0:
				questionsArray=res.getStringArray(R.array.questionType1);
				break;
			case 1:
				questionsArray=res.getStringArray(R.array.questionType2);
				break;
			case 2:
				questionsArray=res.getStringArray(R.array.questionType3);
				break;
			default:
				break;
		};
		questionLength=questionsArray.length;
		currentNum=0;
		progressbar.setMax(questionLength);
		progressbar.setProgress(1);
		setQuestionContent();
	}
	
	private void setQuestionContent(){
		questionTv.setText(Html.fromHtml("<b>"+questionsArray[currentNum]+"</b>"));
//		String filed="question"+currentNum;
//		Class<?> c;
//		Field f;
//		try {
//			c=Class.forName("com.raindrop.screening.R"+"$"+"drawable");
//			f = c.getField(filed);
//			questionIv.setImageResource(Integer.valueOf(f.get(c.newInstance()).toString()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		int key=currentNum+1;
		if(v.getId()==R.id.imageBtnYes){
				answers.put(key+"", 1);
		}else if(v.getId()==R.id.imageBtnNo){
				answers.put(key+"", 0);
		}
		currentNum++;
		if(currentNum<questionLength){
			setQuestionContent();
			progressbar.setProgress(currentNum+1);
		}else{
			commitAnswers();
		}
		startAlphaAnimation(displayPartLL);
	}
	public JSONObject formatRequestParams(){
		JSONObject jsonparams=new JSONObject();
		try {
		for(int i=0;i<answers.size();i++){
				jsonparams.put("question"+(i+1), answers.get((i+1)+""));
		}
			jsonparams.put("userid", myapp.getUserid());
			jsonparams.put("terminal", myapp.getTerminal());
			jsonparams.put("age", myapp.getAge());
			jsonparams.put("gender", myapp.getGender());
			jsonparams.put("gender", myapp.getRelation());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonparams;
	}
	public void commitAnswers(){
		json=formatRequestParams();
		Thread mythread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String,String> requestParams=new HashMap<String, String>();
					requestParams.put("answer", json.toString());
					Log.i("sjl", "�ش�Ľ��"+json.toString());
					String jsonstring=HttpUtil.postRequest("datacommit", requestParams);
					Message me=new Message();
					Log.i("sjl", "����������ֵ��"+jsonstring);
					me.obj=jsonstring;
					myhandler.sendMessage(me);
				} catch (Exception e) {
					e.printStackTrace();
				}
				}
			});
//		��������ύ����
		mythread.start();
		handleResult();
	}
	public void  handleResult(){
//		���ݲ�ͬ����� ���в�ͬ���߼��ж�
		int register=0;
		Resources rs=this.getResources();
		String [] answerStandard=rs.getStringArray(R.array.answers);
		int dangerIndexNum=0;
		boolean result=true; 
		switch(questionType){
			case 0:
//				������ص������δ�Ѽ��ã������ʹ����߼����Ҹ���
				break; 
			case 1:
//				���Խ�������ԡ�˵�� ������������ɸ�� 16~30���� �� ͯ ,�� 23 �� �� �� �� �� д �� �� �� �� �� �� �� �� ׼ :�� ��ѡ��Ϊ���ǡ��򡰷�,�� 1��18��20��2��ѡ���ǡ�, �������ѡ����ʱ���жϸ���Ϊ����;�������б� ��׼:�ܹ� 23���С�3�����Ի� 6�������Ŀ�� (�� �� �� Ŀ Ϊ �� 2��7��9��13��14��15 �� )�� 2 �� �� �� Ϊ �¶�֢�߷���,���һ���绰���,��δͨ�������� ��һ��������
//				16��30�������
				for(int i=0;i<questionsArray.length;i++){
					if(answerStandard.equals(answers.get(i)));
					{
						register++;
						if(i==2||i==7||i==9||i==13||i==14||i==15){
							dangerIndexNum++;
						}
						if(dangerIndexNum>=2){
							result=false;
							break;
						}
					}
					if(register>=3){
						result=false;
						break;
					}
				}
				break; 
			case 2:
//				ͬ0��������ص������δ�Ѽ��ã������ʹ����߼����Ҹ���
				break; 
			default:
				break; 
		}
		if(!result){
			questionTv.setText(Html.fromHtml("���Խ�������� <br/> �¶�֢�߷���,����ѯ��ع�����Ա<br/>���һ���绰���,��δͨ���������һ������"));
		}else{
			questionTv.setText("���Խ����������");
		}
	}
	
	/**
	 * ʵ�ֽ���Ч�� 
	 * @param view
	 */
	public void startAlphaAnimation(View view){
		AlphaAnimation aa=new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);
	}
	
	@Override  
	public boolean onKeyUp(int keyCode, KeyEvent event)   
	{   
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {   
	    	finish();   
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);   
			return true;   
	    }   
	    return super.onKeyUp(keyCode, event);   
	} 
	
	
}
