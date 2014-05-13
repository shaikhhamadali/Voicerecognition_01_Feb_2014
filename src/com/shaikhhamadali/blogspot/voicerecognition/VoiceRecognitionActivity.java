package com.shaikhhamadali.blogspot.voicerecognition;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class VoiceRecognitionActivity extends Activity {
	//variables
	//code to get result
	 private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	 //declare controls
	 private EditText eTTextHint;
	 private ListView lVTextMatches;
	 private Spinner spTextMatches;
	 private Button btnSpeak;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_voice_recognition);
		  //Initialize controls
		  initializeControls();
		  // Check if voice recognition is present 
		  isVoiceRecognitionAvaialble();
	}
	private void initializeControls() {
		  eTTextHint = (EditText) findViewById(R.id.eTTextHint);
		  lVTextMatches = (ListView) findViewById(R.id.lVTextMatches);
		  spTextMatches = (Spinner) findViewById(R.id.spTextMatches);
		  btnSpeak = (Button) findViewById(R.id.btnSpeak);
	}
	public void isVoiceRecognitionAvaialble() {
		  PackageManager pm = getPackageManager();
		  //start an activity that will prompt the user for speech and send it through a speech recognizer
		  List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
		    RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		  //check's that is activity available
		  if (activities.size() == 0) {
		   btnSpeak.setEnabled(false);
		   btnSpeak.setText("Voice recognizer not present");
		   Toast.makeText(this, "Voice recognizer not present",
		     Toast.LENGTH_SHORT).show();
		  }
		 }

		 public void speak(View view) {
			//create instance of Intent and pass RecognizerIntent.ACTION_RECOGNIZE_SPEECH
		  Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		  // Specify the calling package to identify your application
		  intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
		    .getPackage().getName());

		  // Display a hint to the user about what he should say.
		  intent.putExtra(RecognizerIntent.EXTRA_PROMPT, eTTextHint.getText()
		    .toString());

		  // Given an hint to the recognizer about what the user is going to say
		  //There are two form of language model available
		  //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
		  //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

		  // If number of Matches is not selected then return show toast message
		  if (spTextMatches.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
		   Toast.makeText(this, "Please select No. of Matches from spinner",
		     Toast.LENGTH_SHORT).show();
		   return;
		  }

		  int noOfMatches = Integer.parseInt(spTextMatches.getSelectedItem()
		    .toString());
		  // Specify how many results you want to receive. The results will be
		  // sorted where the first result is the one with higher confidence.
		  intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
		  //Start the Voice recognizer activity for the result.
		  startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		 }

		 @Override
		 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

		   //If Voice recognition is successful then it returns RESULT_OK
		   if(resultCode == RESULT_OK) {
			// creates list of all the text matches
		    ArrayList<String> textMatchList = data
		    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		    //proceed further if is list is not empty
		    if (!textMatchList.isEmpty()) {
		     // If first Match contains the 'search' word
		     // Then start web search.
		     if (textMatchList.get(0).contains("search")) {

		        String searchQuery = textMatchList.get(0);
		                                           searchQuery = searchQuery.replace("search","");
		        //create instance of Intent and pass Intent.ACTION_WEB_SEARCH
		        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
		        //put text you want to search
		        search.putExtra(SearchManager.QUERY, searchQuery);
		        //start activity and pass search intent
		        startActivity(search);
		     } else {
		         // populate the Matches
		         lVTextMatches.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,textMatchList));
		     }

		    }
		   //Result code for various error.
		   }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
		    showToastMessage("Audio Error");
		   }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
		    showToastMessage("Client Error");
		   }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
		    showToastMessage("Network Error");
		   }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
		    showToastMessage("No Match");
		   }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
		    showToastMessage("Server Error");
		   }
		  super.onActivityResult(requestCode, resultCode, data);
		 }
		 /**
		 * Helper method to show the toast message
		 **/
		 void showToastMessage(String message){
		  Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		 }
}
