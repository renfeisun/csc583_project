/**
 * 
 */
package edu.arizona.cs;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author RenfeiSun
 *
 */
public class QueryProcessor {
	private String questions;
	private HashMap<String, LinkedList<String[]>> queryKey;
	private int total = 0;
	
	public QueryProcessor(String file) {
		questions = file;
		queryKey = new HashMap<>();
	}
	
	public HashMap<String, LinkedList<String[]>> getQueryKey(){
		return queryKey;
	}
	
	public int totalQuery() {
		return total;
	}
	
	public void queryKeyGetenerate(){
		ClassLoader classLoader = getClass().getClassLoader();
		File filesName = new File(classLoader.getResource(this.questions).getFile());
		try (Scanner scanner = new Scanner(filesName)) {
			while (scanner.hasNextLine()) {
				total++;
				String category = scanner.nextLine();
				String question = scanner.nextLine();
				String answer = scanner.nextLine();
				pushIntoMap(category, question, answer);
				scanner.nextLine();
			}
			scanner.close();
		} catch (Exception ex) {
			//System.err.println(ex.getMessage());
		}
	}
	
	private void pushIntoMap(String category, String question, String answer) {
		String[] query = question.split(" ");
		if(!queryKey.containsKey(answer)) {
			queryKey.put(answer, new LinkedList<String[]>());
			
		}
		queryKey.get(answer).add(query);
	}
	
	public String toString() {
		String str = "";
		for(String key : queryKey.keySet()) {
			str += '\n' + key + '\n';
			LinkedList<String[]> querys = queryKey.get(key);
			for(String[] query : querys) {
				str += String.join(" ", query) + '\n';
			}
		}
		return str;
	}
}
