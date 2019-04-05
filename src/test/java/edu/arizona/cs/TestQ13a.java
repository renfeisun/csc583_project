package edu.arizona.cs;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestQ13a {

	@Test
	public void test() {
		String inputFileFullPath = "input.txt";

		QueryEngine objQueryEngine = new QueryEngine(inputFileFullPath);
		try {

			String[] common_query = { "Not", "to", "be", "confused", "with", "karma,", "krama", "is", "a", "popular",
					"accessory", "sold", "in", "cambodia;", "the", "word", "means", "\"scarf\"", "in", "this",
					"national", "language", "of", "Cambodia" };
			List<ResultClass> ans13a = objQueryEngine.runQ13a(common_query);
			for (ResultClass ans : ans13a) {
				System.out.println(ans);
			}

		} catch (java.io.IOException ex) {
			System.out.println(ex.getMessage());
		}

	}

//	@Test
//	public void testDocsAndScores() {
//
//		String inputFileFullPath = "input.txt";
//
//		QueryEngine objQueryEngine = new QueryEngine(inputFileFullPath); // not here
//
//		ClassLoader classLoader = getClass().getClassLoader();
//		File filesName = new File(classLoader.getResource("questions.txt").getFile());
//		try (Scanner scanner = new Scanner(filesName)) {
//			while (scanner.hasNextLine()) {
//				String catlog = scanner.nextLine();
//				String question = scanner.nextLine();
//				String answer = scanner.nextLine();
//				scanner.nextLine();
//				String[] common_query = question.split(" ");
//				try {
//					// "The", "dominant", "paper", "in", "our", "nation's", "capital,", "it's",
//					// "among", "the", "top", "10", "U.S.", "papers", "in", "circulation"
//
////					String[] common_query = { "The", "dominant", "paper", "in", "our", "nation's", "capital,", "it's",
////							"among", "the", "top", "10", "U.S.", "papers", "in", "circulation" };
//
//					List<ResultClass> ans13a = objQueryEngine.runQ13a(common_query);
//
//				} catch (java.io.IOException ex) {
//					System.out.println(ex.getMessage());
//				}
//			}
//			scanner.close();
//		} catch (Exception ex) {
//			// System.err.println(ex.getMessage());
//		}
//	}
}
