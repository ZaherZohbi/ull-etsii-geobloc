package com.geobloc.activities;


import com.geobloc.R;
//import com.geobloc.activities.FormActivity.FormsLoader_FormsTaskListener;
import com.geobloc.form.FormPage;
import com.geobloc.form.FormPage.PageType;
import com.geobloc.handlers.FormHandler;
import com.geobloc.listeners.IStandardTaskListener;
import com.geobloc.shared.GBSharedPreferences;
import com.geobloc.shared.Utilities;
import com.geobloc.tasks.LoadFormTask;
import com.geobloc.widget.QuestionWidget;
import com.geobloc.widget.CreateWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Activity that loads the form and is responsible to handle it graphically
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class FormActivity extends Activity {
	private static final String TAG = "FormActivity";
	
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
	
	private class FormsLoader_FormsTaskListener implements IStandardTaskListener {

		private Context callerContext;
		private Context appContext;
		
		public FormsLoader_FormsTaskListener(Context appContext, Context callerContext) {
			this.callerContext = callerContext;
			this.appContext = appContext;
		}
		
		@Override
		public void taskComplete(Object result) {
			pDialog.dismiss();
			
			formH = (FormHandler)result;
			if (formH == null) {
			    new AlertDialog.Builder(callerContext)
					.setTitle("Error")
					.setMessage(loadTask.message)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                        finish();
	                        dialog.cancel();
	                   }
	               })
			      .show();
			}
			else {
				Utilities.showTitleAndMessageDialog(callerContext, formH.getNameForm(),
						"Formulario "+formH.getNameForm()+" cargado correctamente");
				postTaskFinished();
			}
		}

		@Override
		public void progressUpdate(int progress, int total) {
			// TODO Auto-generated method stub
			
		}
    	
    }
	
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	
	private String filename;
	private String filepath;
	
	private static ProgressDialog pDialog;
	private LoadFormTask loadTask;
	private IStandardTaskListener listener;
	
	private static FormHandler formH;
	
	private SharedPreferences prefs;
	
	LinearLayout vista;
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Aqui debemos conocer el nombre del fichero
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
        	filename = bundle.getString(FormActivity.FILE_NAME);
        	filepath = bundle.getString(FormActivity.FILE_PATH);
        }
        else {
        	Utilities.showToast(getApplicationContext(),
            		"No se ha seleccionado fichero",
                    Toast.LENGTH_SHORT);
        	finish();
        }

        initConfig();
        
        /** to use the custom title */
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.custom_title);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        
        final Object data = getLastNonConfigurationInstance();
        
        // The activity is starting for the first time
        if (data == null) {
            myLoadForm();
        } else {
            // El viewFlipper ya existe
        }

        
	}
	
	/**
	 * Initial config
	 */
	public void initConfig () {
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		Log.v(TAG, "Esto: <"+GBSharedPreferences.__FORM_BACKGROUND_COLOR__+">");
		Utilities.background = Integer.parseInt(prefs.getString(GBSharedPreferences.__FORM_BACKGROUND_COLOR__, "-1"));
		Log.v(TAG, "Es    <"+Utilities.background+">");
		
		Log.v(TAG, "Esto: <"+GBSharedPreferences.__FORM_TEXT_COLOR__+">");
		Utilities.fontColor = Integer.parseInt(prefs.getString(GBSharedPreferences.__FORM_TEXT_COLOR__, "-12303292"));
		Log.v(TAG, "Es    <"+Utilities.fontColor+">");
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		return viewFlipper;
	}
	
	private void myLoadForm () {
        setContentView(R.layout.flipper_question);
		setTitle(getString(R.string.app_name)+ " > " + filename);
        
		pDialog = ProgressDialog.show(this, "Working", "Loading form "+filename);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
				
		/*** Flipper *********/
	    viewFlipper = (ViewFlipper)findViewById(R.id.flipper);
	    slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
	    slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
	    slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
	    slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
	    
	    
	    gestureDetector = new GestureDetector(new MyGestureDetector());
	    gestureListener = new View.OnTouchListener() {
	    	public boolean onTouch(View v, MotionEvent event) {
	    		if (gestureDetector.onTouchEvent(event)) {
	    			return true;
	    		}
	    		return false;
	    	}
	    };
	    /**********************/
		
        
        loadTask = new LoadFormTask();
        loadTask.setContext(getApplicationContext());
        loadTask.setListener(new FormsLoader_FormsTaskListener(getApplicationContext(), this));
        
        loadTask.execute(filepath);
	}
	
	private void postTaskFinished() {
		LinearLayout lL = (LinearLayout) findViewById(R.id.FormLayoutInit);
		lL.setBackgroundColor(Utilities.background);
		lL = (LinearLayout) findViewById(R.id.FormLayoutEnd);
		lL.setBackgroundColor(Utilities.background);
		
		/** Rellenamos el Titulo y la descripci�n del formulario */
		/*** Colocamos texto en el viewFlipper */
		TextView tView = (TextView)findViewById(R.id.TitleForm);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(getString(R.string.form_loaded, formH.getNameForm()));
		tView = (TextView)findViewById(R.id.FormVersion);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(getString(R.string.form_version, formH.getVersionForm()));
		tView = (TextView)findViewById(R.id.FormDescription);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(formH.getDescription());
		tView = (TextView)findViewById(R.id.TextFingerMov);
		tView.setTextColor(Utilities.fontColor);
		tView.setText(getString(R.string.help_form_mov));
		
		setFlipperPages();
		setNumPage();
	}
	
	private void setNumPage () {
	
		final TextView leftText = (TextView) findViewById(R.id.left_text);
		final TextView rightText = (TextView) findViewById(R.id.right_text);
		
    	int page = viewFlipper.getDisplayedChild();
    	int max_page = viewFlipper.getChildCount();
    	
		if ((page >= 0) && (page < max_page)) {
			if ((page > 0) && (page < (max_page-1))) {
				leftText.setText(formH.getNameForm()+" > "+formH.getNamePage(page-1));
			}
			else {
				leftText.setText(formH.getNameForm());
			}
        	rightText.setText("P�gina: "+(page+1)+"/"+max_page);
		} else {
			rightText.setText("");
		}
	}
	
	private void setFlipperPages () {
		Context context = FormActivity.this;
		QuestionWidget wdget;
		
	    if (formH != null) {
	    	for (int page=0; page < formH.getNumPages(); page++) {
	    	
	    		PageType mType = (formH.getPage(page)).getPageType();
	    		if (mType == null) {
	    			Log.e(TAG, "La p�gina "+page+"no tiene tipo");
	    		}
	    		switch (mType) {
	    		
	    			case PHOTO:
	    				/*View myView = View.inflate(this, R.layout.gallery, null);
	    				RelativeLayout layoutR = (RelativeLayout) myView.findViewById(R.id.photoGalleryLayout);
	    				viewFlipper.addView(layoutR, page+1);
	    				break;*/
	    			case AUDIO:
	    				/*LinearLayout vistaA = new LinearLayout(context);
	    	    		vistaA.setPadding(5, 5, 5, 5);
	    	    		vistaA.setOrientation(LinearLayout.VERTICAL);
	    	    		vistaA.setBackgroundColor(Utilities.background);*/
	    	    		
	    				wdget = CreateWidget.createWidget(formH.getQuestionOfPage(0, page), context, (ViewGroup)viewFlipper);
	    				//viewFlipper.addView ((View) wdget, page+1);
	    				
	    				//viewFlipper.addView(vistaA, page+1);
	    			case VIDEO: 
	    			case GPS: break;
	    			case DATA:
	    				LinearLayout vistaR = new LinearLayout(context);
	    	    		vistaR.setPadding(5, 5, 5, 5);
	    	    		vistaR.setOrientation(LinearLayout.VERTICAL);
	    	    		vistaR.setHorizontalScrollBarEnabled(true);
	    	    		vistaR.setVerticalScrollBarEnabled(true);
	    	    		vistaR.setBackgroundColor(Utilities.background);
	    	    		
	    	    		int numQuestions = formH.getNumQuestionOfPage(page);
	    	    		
	    	    		for (int question=0; question < numQuestions; question++) {
	    	    			/** create the appropriate widget depending on the question */
	    	    			wdget = CreateWidget.createWidget(formH.getQuestionOfPage(question, page), context, (ViewGroup)viewFlipper);
	    	    			vistaR.addView((View)wdget);
	    	    		}
	    	    		viewFlipper.addView(vistaR, page+1); 			
	    			default: break;
	    		
	    		}
	    		
	    		
	    	}
	    }
	}
	
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	if (viewFlipper.getDisplayedChild() < (viewFlipper.getChildCount()-1)) {
                		viewFlipper.setInAnimation(slideLeftIn);
                		viewFlipper.setOutAnimation(slideLeftOut);
                		viewFlipper.showNext();
                		
                		setNumPage();
                	} else {
                		Utilities.showToast(getApplicationContext(), getString(R.string.no_more_pages_at_rigth), Toast.LENGTH_SHORT);
                	}
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	if (viewFlipper.getDisplayedChild() > 0) {
                		viewFlipper.setInAnimation(slideRightIn);
                		viewFlipper.setOutAnimation(slideRightOut);
                		viewFlipper.showPrevious();
                		
                		setNumPage();
                	} else {
                		Utilities.showToast(getApplicationContext(), getString(R.string.no_more_pages_at_left), Toast.LENGTH_SHORT);
                	}
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
	        return true;
	    else
	    	return false;
    }

	public void setListener(IStandardTaskListener listener) {
		this.listener = listener;
	}

	
}
