package edu.arizona.cs;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestQ13a {

	@Test
	public void test() {
		String contents = "input.txt";
		String questions = "input.txt";

		QueryEngine objQueryEngine = new QueryEngine(contents, questions);
		try {

			String[] common_query = { "Not", "to", "be", "confused", "with", "karma,", "krama", "is", "a", "popular",
					"accessory", "sold", "in", "cambodia;", "the", "word", "means", "\"scarf\"", "in", "this",
					"national", "language", "of", "Cambodia" };
			List<ResultClass> ans13a = objQueryEngine.search(common_query);
			for (ResultClass ans : ans13a) {
				System.out.println(ans);
			}

		} catch (java.io.IOException ex) {
			System.out.println(ex.getMessage());
		}

	}
	
	@Test
	public void queryProcessorTest() {
		QueryProcessor queryPro = new QueryProcessor("questions.txt");
		queryPro.queryKeyGetenerate();
		System.out.println(queryPro);
	}
}
