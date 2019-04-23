package edu.arizona.cs;

public class QueryClass {
	String[] question;
	String[] category;
	
	public QueryClass(String[] q, String[] c) {
		question = q;
		category = c;
	}
	
	public String[] getQuestions() {
		return question;
	}
	
	public String[] getCategory() {
		return category;
	}
	
	public String toString() {
		String str = "query:";
		for(String s : question)
			str += " " + s;
		str += " category: ";
		for(String s : category)
			str += " " + s;
		return str;
	}
}
