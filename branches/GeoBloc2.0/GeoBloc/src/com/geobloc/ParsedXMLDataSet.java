package com.geobloc;

import java.util.ArrayList;
import java.util.List;

/** Conjunto de datos del XML parseado 
 * 
 * @author Jorge Carballo (jelcaf@gmail.com)
 *
 */
public class ParsedXMLDataSet {
	
	/** Lista de p�ginas */
	private List<FormPage> pageList;
	
	/** N�mero de p�ginas que tendr� el formulario */
	private int numPages;
	
	private String Form = null;
	private String Button = null;
	private String Name = null;
	private String MyString = null;
	private String Int = null;
	
	
	public ParsedXMLDataSet () {
		pageList = new ArrayList<FormPage>();
	}
	
	/**
	 * A�ade una p�gina a la lista de p�ginas del formulario
	 * 
	 * @param myPage P�gina a a�adir
	 */
	public void addPage (FormPage myPage){
		pageList.add (myPage);
		numPages++;
	}
	
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public String getForm() {
         return Form;
    }
    public void setForm(String extractedString) {
         this.Form = extractedString;
    }
    public String getButton() {
         return Button;
    }
    public void setButton(String extractedString) {
         this.Button = extractedString;
    }
    public String getName(){
    	return Name;
    }
    public void setName(String extractedString){
    	this.Name = extractedString;
    }
    public String getMyString() {
        return MyString;
   }
    public void setMyString(String extractedString) {
        this.MyString = extractedString;
   }
    public String getInt() {
        return Int;
   }
    public void setInt(String extractedString) {
        this.Int = extractedString;
   }
    public String toString(){
         return "Todos los datos";
    } 

}
