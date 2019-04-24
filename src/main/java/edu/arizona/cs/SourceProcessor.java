/**
 * 
 */
package edu.arizona.cs;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import edu.stanford.nlp.simple.*;

/**
 * @author RenfeiSun
 *
 */
public class SourceProcessor {
	private String corpusDir;
	private ArrayList<String> stopWordsList;
	private final String[] stopWords = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

	public SourceProcessor(String corpusDir) {
		this.corpusDir = corpusDir;
		stopWordsList = new ArrayList<>(Arrays.asList(stopWords));
	}

	public void index_generate(IndexWriter writer) {
		ClassLoader classLoader = getClass().getClassLoader();
		File dirc = new File(classLoader.getResource(this.corpusDir).getFile());
		try {
			int index = 0;
			System.out.print("Start");
			for (File file : dirc.listFiles()) {
				HashMap<String, String[]> pages = readFile(file);
				for(String title : pages.keySet()) {
					String contents[] = pages.get(title);
					Document doc = new Document();
					doc.add(new StringField("docid", title, Field.Store.YES));
	                doc.add(new TextField("contents", contents[1], Field.Store.YES));
	                doc.add(new TextField("categories", contents[0], Field.Store.YES));
	                writer.addDocument(doc);
				}
				index++;
				System.out.println("Finshied " + index + "/80");
			}
			writer.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	private String removeStopWords(List<String> list){
		String str = "";
		for(String lemma : list){
			if(!stopWordsList.contains(lemma))
				str += lemma + " ";
		}
		return str.trim();
	}

	private HashMap<String, String[]> readFile(File filesName) {
		String title = "";
		String content = "";
		String categories ="";
		HashMap<String, String[]> pages = new HashMap<>();
		try (Scanner scanner = new Scanner(filesName)) {
			while (scanner.hasNextLine()) {
				String str = scanner.nextLine();
				if (str.contains("[[") && str.contains("]]") && str.startsWith("[[")) {
					String[] c = new String[2];
					c[0] = categories;
					c[1] = content;
					pages.put(title, c);
					title = str.replaceAll("\\[tpl].*?\\[/tpl]", "").replaceAll("\\]", "").replaceAll("\\[", "").trim();
					categories="";
					content = "";
				} else {
					String temp = str.replaceAll("\\[tpl].*?\\[/tpl]", "").replaceAll("[^ a-zA-Z\\d]", " ").trim();
					if(!temp.equals("")) {
						Sentence stc = new Sentence(temp);					
						String lemma = removeStopWords(stc.lemmas());
						if(temp.startsWith("CATEGORIES:"))
							categories = lemma;
						else
							content += lemma;
					}
					
				}
			}
			scanner.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return pages;
	}
}
