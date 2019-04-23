package edu.arizona.cs;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestQ13a {

	@Test
	public void test() {
		String contents = "input.txt";
		String questions = "input.txt";

		QueryEngine objQueryEngine = new QueryEngine(contents, questions);


	}
	
	@Test
	public void queryProcessorTest() {
		QueryProcessor queryPro = new QueryProcessor("questions.txt");
		queryPro.queryKeyGetenerate();
		System.out.println(queryPro);
	}
}
