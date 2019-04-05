/**
 * 
 */
package edu.arizona.cs;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

/**
 * @author RenfeiSun
 *
 */
public class SourceProcessor {
	private String contentTable;

	public SourceProcessor(String contentTable) {
		this.contentTable = contentTable;
	}

	public void index_generate(IndexWriter writer) {
		ClassLoader classLoader = getClass().getClassLoader();
		File filesName = new File(classLoader.getResource(this.contentTable).getFile());
		try (Scanner scanner = new Scanner(filesName)) {
			while (scanner.hasNextLine()) {
				File file = new File(classLoader.getResource(scanner.nextLine().trim()).getFile());
				HashMap<String, String> pages = readFile(file);
				for(String title : pages.keySet()) {
					String content = pages.get(title);
					Document doc = new Document();
					doc.add(new StringField("docid", title, Field.Store.YES));
	                doc.add(new TextField("contents", content, Field.Store.YES));
	                writer.addDocument(doc);
				}
			}
			scanner.close();
			writer.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	private HashMap<String, String> readFile(File filesName) {
		String title = "";
		String content = "";
		HashMap<String, String> pages = new HashMap<>();
		try (Scanner scanner = new Scanner(filesName)) {
			while (scanner.hasNextLine()) {
				String str = scanner.nextLine();
				if (str.contains("[[") && str.contains("]]") && str.startsWith("[[")) {
					pages.put(title, content);
					title = str.replaceAll("\\[tpl].*?\\[/tpl]", "").trim();
					content = "";
				} else {
					content += str.replaceAll("\\[tpl].*?\\[/tpl]", "").trim();
				}
			}
			scanner.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return pages;
	}
}
