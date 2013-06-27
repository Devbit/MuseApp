package com.hro.museapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Start extends Activity 
{
	Button button1, button2, button3, button4;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		button1 = (Button)findViewById(R.id.button1); // Top button
		button2 = (Button)findViewById(R.id.button2); // Mid left
		button3 = (Button)findViewById(R.id.button3); // Mid right
		button4 = (Button)findViewById(R.id.button4); // Bottom left
		//button5 = (Button)findViewById(R.id.button5); // Bottom right
		
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), AllPlacesActivity.class);
				startActivity(i);
				
			}});
			
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(), FavouritesList.class);
				startActivity(i);
				
			}});
		
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(), AllCharitiesActivity.class);
				startActivity(i);
				
			}});
		
		button4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(), AboutAppActivity.class);
				startActivity(i);
				
			}});
	}
	
}
