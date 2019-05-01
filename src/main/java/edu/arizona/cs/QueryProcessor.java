/**
 * 
 */
package edu.arizona.cs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.simple.Sentence;

/**
 * @author RenfeiSun
 *
 */
public class QueryProcessor {
	private String questions;
	private HashMap<String, LinkedList<QueryClass>> queryKey;
	private int total = 0;
	private ArrayList<String> stopWordsList;
	private final String[] stopWords = { "a", "an", "the", "and", "but", "if", "of", "at", "by", "for", "with", "about",
			"to", "from", "up", "down", "in", "out", "on", "off", "over", "under" };

	public QueryProcessor(String file) {
		questions = file;
		queryKey = new HashMap<>();
		stopWordsList = new ArrayList<>(Arrays.asList(stopWords));
	}

	public HashMap<String, LinkedList<QueryClass>> getQueryKey() {
		return queryKey;
	}

	public int totalQuery() {
		return total;
	}

	public void queryKeyGetenerate() {
		ClassLoader classLoader = getClass().getClassLoader();
		File filesName = new File(classLoader.getResource(this.questions).getFile());
		Sentence stc;
		try (Scanner scanner = new Scanner(filesName.listFiles()[0])) {
			while (scanner.hasNextLine()) {
				total++;
				String category = scanner.nextLine().replaceAll("[^ a-zA-Z\\d]", " ").trim();
				String question = scanner.nextLine().replaceAll("[^ a-zA-Z\\d]", " ").trim();
				String answer = scanner.nextLine();
				stc = new Sentence(category);
				category = removeStopWords(stc.lemmas());// String.join(" ",
															// stc.lemmas());
				stc = new Sentence(question);
				question = removeStopWords(stc.lemmas());// String.join(" ",
															// stc.lemmas());
				pushIntoMap(category, question, answer);
				scanner.nextLine();
			}
			scanner.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	private void pushIntoMap(String category, String question, String answer) {
		String[] query = question.split(" ");
		String[] cat = category.split(" ");
		if (!queryKey.containsKey(answer)) {
			LinkedList<QueryClass> qc = new LinkedList<>();
			queryKey.put(answer, qc);
		}
		queryKey.get(answer).add(new QueryClass(query, cat));
	}

	private String removeStopWords(List<String> list) {
		String str = "";
		for (String lemma : list) {
			if (!stopWordsList.contains(lemma))
				str += lemma + " ";
		}
		return str.trim();
	}

	// public String toString() {
	// String str = "";
	// for(String key : queryKey.keySet()) {
	// str += '\n' + key + '\n';
	// QueryClass querys = queryKey.get(key);
	// str += querys;
	// }
	// return str;
	// }
}
