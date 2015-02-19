package com.jiafan.Fdbutils;

import java.util.List;

import com.example.mydbutils.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mybean mybean = new mybean(this);
		mybean.setName("张三");
		mybean.setAge("5");
		boolean save = mybean.save();
		System.out.println(save);
		mybean.setName("李四");
		mybean.setAge("6");
		boolean save2 = mybean.save();
		System.out.println(save2);
		
	
	//	mybean mybean2 = new mybean(this);
	//	Beanvalues beanvalues = new Beanvalues();
	/*	beanvalues.addWhereequalto("name", "张三");
		beanvalues.addWhereequalto("age", "5");
		beanvalues.addSetting("name", "张珈凡");
		beanvalues.addSetting("age", "100111");*/
		
	
	
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
