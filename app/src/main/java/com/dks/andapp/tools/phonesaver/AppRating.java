package com.dks.andapp.tools.phonesaver;

import java.net.URL;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class AppRating {
	private static Context locContext;
	private final static String APP_TITLE = "Phone Saver - Ad";
	
	private static RatingBar ratingBar;
	private static EditText feedBackText;
	private static EditText userEmail;
	private static EditText userNameText;
	private static sendEmailTask sendEmail;
	
	public AppRating(Context context) {
		locContext = context;
		sendEmail = new sendEmailTask();
	}
		
	public void showRateDialog() {
		final String pkgName= locContext.getPackageName();
		final Dialog dialog = new Dialog(locContext);
		dialog.setTitle("Rate " + APP_TITLE);
	
		LinearLayout ll = new LinearLayout(locContext);
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(locContext);
		
		tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);
        
        ratingBar = new RatingBar(locContext);
        int width = LayoutParams.WRAP_CONTENT;
        int height = LayoutParams.WRAP_CONTENT;
        ratingBar.setLayoutParams(new LayoutParams(width, height));
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);
        ratingBar.setRating(3);
        ll.addView(ratingBar);
        
        /*ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
        	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
        	{
        		userRating = Integer.parseInt(String.valueOf(rating));
        	}
        });*/
        
        Button b1 = new Button(locContext);
        b1.setText("Rate " + APP_TITLE);
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if ( ratingBar.getRating() == 0){
            		//Prompt user for rating
            		Toast.makeText(locContext, "Please Rate before proceeding", Toast.LENGTH_LONG).show();
            	}
            	else if (ratingBar.getRating() <= 3) {
            		//Ask for online feedback
            		dialog.dismiss();
            		showFeedBackDialog();
            	}
            	else {
	            	locContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgName)));
	                dialog.dismiss();
            	}
            }
        });        
        ll.addView(b1);
        
        Button b2 = new Button(locContext);
        b2.setText("Remind me later");
        b2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		dialog.dismiss();
        	}
        });
        
        dialog.setContentView(ll);
        dialog.show();
	}
	
	private static void showFeedBackDialog() {
		final Dialog feedBackDialog = new Dialog(locContext);
		feedBackDialog.setTitle("Please provide your feedback for: " + APP_TITLE);
		LinearLayout ll = new LinearLayout(locContext);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		userNameText = new EditText(locContext);
		userNameText.setHint("Your Name");
		userNameText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		ll.addView(userNameText);
		
		userEmail = new EditText(locContext);
		userEmail.setHint("Your Email");
		userEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		ll.addView(userEmail);
		
		feedBackText = new EditText(locContext);
		feedBackText.setHint("Feedback");
		feedBackText.setSingleLine(false);
		feedBackText.setLines(10);
		feedBackText.setMinLines(5);
		feedBackText.setEms(10);
		feedBackText.setGravity(Gravity.LEFT|Gravity.TOP);
		feedBackText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		ll.addView(feedBackText);
		
		Button sendFeedback = new Button(locContext);
		sendFeedback.setText("Send the feedback");
		sendFeedback.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		sendEmail.execute(new String[] {"laymantool@gmail.com",
        				"Developer83",
        				"Feedback about Phone Saver"});
        		feedBackDialog.dismiss();
        	}
        });
		ll.addView(sendFeedback);

		feedBackDialog.setContentView(ll);
		feedBackDialog.show();
	}
	
	private class sendEmailTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String...strings ) {
			try{
				GmailSender sender = new GmailSender(strings[0],strings[1] );
				sender.sendMail(strings[2],   
						userEmail.getText().toString() + "\n\n" + 
								feedBackText.getText().toString(),
	                    strings[0],
	                    strings[0]);   
			} catch (Exception e) {   
				Log.e("SendMail", e.getMessage(), e);   
			}
			return "";
		}
		
	}
}
