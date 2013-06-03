package com.example.androidhive;

import com.example.androidhive.R;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class Launch extends Activity 
{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        
        Thread runStartup = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(1000);
					Intent intent = new Intent("com.example.androidhive.START");
					startActivity(intent);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				finally
				{
					finish();
				}
			}
		};
		
		runStartup.start();
        
    }

    
}