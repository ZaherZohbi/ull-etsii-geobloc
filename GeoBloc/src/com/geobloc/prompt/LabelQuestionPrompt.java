package com.geobloc.prompt;

import org.xml.sax.Attributes;

import android.util.Log;

import com.geobloc.handlers.AttributeTag;
import com.geobloc.shared.Utilities;
import com.geobloc.shared.Utilities.QuestionType;

public class LabelQuestionPrompt extends QuestionPrompt {
	
	private static final String TAG = "LabelQuestionPrompt";
	
	private String title;
	
	private Attributes atts;
	
	public LabelQuestionPrompt(String name, AttributeTag att) {
		
		Log.i(TAG, "Constructor"+TAG);
		// ID
		if (att.attMap.containsKey(Utilities.ATTR_ID)) {
			this.setQuestionId(att.attMap.get(Utilities.ATTR_ID));
		} else {
			Log.e(TAG, "<"+name+"> has not ID");
		}
		
		if (att.isRequired())
			this.setRequired();
			
		this.setQuestionTitle(name);
		this.setType();
	}


	@Override
	public void setType () {
		type = QuestionType.GB_LABEL;
	}
	
	/**
	 * Sets the text on the label
	 * @param name
	 */
	public void setQuestionTitle (String name) {
		title = name;
	}
	
	public String getQuestionTitle () {
		return title;
	}

	/**
	 * Devuelve en �ste caso el texto de la etiqueta
	 */
	@Override
	public Object getAnswer() {
		return getQuestionTitle();
	}
	

}
