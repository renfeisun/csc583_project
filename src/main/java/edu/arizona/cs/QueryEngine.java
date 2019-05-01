package edu.arizona.cs;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.nio.file.Paths;

public class QueryEngine {

	public static boolean isGrad = false;

	private WhitespaceAnalyzer analyzer = null;
	private IndexWriter writer = null;
	private Directory index = null;
	private Query query = null;
	private IndexSearcher searcher = null;
	private SourceProcessor sp;
	private QueryProcessor qp;

	public QueryEngine(String inputFileObj, String questions) {
		try {
			analyzer = new WhitespaceAnalyzer();
			index = FSDirectory.open(Paths.get("./index"));
			writer = new IndexWriter(index, new IndexWriterConfig(analyzer));
			// sp = new SourceProcessor(inputFileObj);
			// sp.index_generate(writer);
			qp = new QueryProcessor(questions);
			qp.queryKeyGetenerate();
			searcher = new IndexSearcher(DirectoryReader.open(index));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	private void query_gen(String[] query_list, String[] categories) {
		try {
			String[] fields = new String[query_list.length + categories.length + categories.length];
			String[] querys = new String[query_list.length + categories.length + categories.length];
			for (int i = 0; i < query_list.length; i++) {
				fields[i] = "contents";
				querys[i] = query_list[i];
			}

			for (int i = query_list.length; i < query_list.length + categories.length; i++) {
				fields[i] = "contents";
				querys[i] = categories[i - query_list.length];
			}

			for (int i = query_list.length + categories.length; i < fields.length; i++) {
				fields[i] = "categories";
				querys[i] = categories[i - query_list.length - categories.length];
			}
			// query = new QueryParser("", analyzer).parse(String.join(" ",
			// query_list) + String.join("^1.5 ", categories));
			query = MultiFieldQueryParser.parse(querys, fields, analyzer);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	private void query_pair(String[] query_list, String[] categories) {
		try {
			String[] fields = new String[query_list.length - 1 + categories.length + categories.length];
			String[] querys = new String[query_list.length - 1 + categories.length + categories.length];
			for (int i = 0; i < query_list.length - 1; i++) {
				fields[i] = "contents";
				querys[i] = "\"" + query_list[i] + " " + query_list[i + 1] + "\"" + "~400";
			}

			for (int i = query_list.length - 1; i < query_list.length - 1 + categories.length; i++) {
				fields[i] = "contents";
				querys[i] = categories[i - query_list.length + 1];
			}

			for (int i = query_list.length - 1 + categories.length; i < fields.length; i++) {
				fields[i] = "categories";
				querys[i] = categories[i - query_list.length + 1 - categories.length];
			}
			// query = new QueryParser("", analyzer).parse(String.join(" ",
			// query_list) + String.join("^1.5 ", categories));
			query = MultiFieldQueryParser.parse(querys, fields, analyzer);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("why?" + ex.getMessage());
		}
	}

	private List<ResultClass> search() {
		List<ResultClass> ans = new ArrayList<ResultClass>();
		int hitsPerPage = 10;
		TopDocs docs = null;
		try {
			System.out.println(query);
			docs = searcher.search(query, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;

			// System.out.println("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				Document d = searcher.doc(hits[i].doc);
				ans.add(new ResultClass(d, hits[i].score));
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		// for (int i = 0; i < ans.size(); i++) {
		// System.out.println(ans.get(i));
		// }
		return ans;

	}

	public static void main(String[] args) {
		isGrad = args[1].equals("1") ? true : false;
		QueryEngine engine = new QueryEngine("wiki", "question");

		try {
			double[] score = engine.searchAll();
			System.out.println("P@1: " + score[0] * 100 / engine.qp.totalQuery() + "%");
			System.out.println("MRR: " + String.format("%.2f", score[1] / engine.qp.totalQuery()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double[] searchAll() throws java.io.FileNotFoundException, java.io.IOException {
		// this.searcher.setSimilarity(new ClassicSimilarity());
		this.searcher.setSimilarity(new BM25Similarity());
		HashMap<String, LinkedList<QueryClass>> queryKey = qp.getQueryKey();
		double[] resultScore = new double[2];
		resultScore[0] = 0.0;
		resultScore[1] = 0.0;
		for (String key : queryKey.keySet()) {
			LinkedList<QueryClass> querys = queryKey.get(key);
			for (QueryClass query : querys) {
				System.out.println(key);
				query_gen(query.getQuestions(), query.getCategory());
				List<ResultClass> ans = search();
				ArrayList<ResultClass> result = new ArrayList<>();
				if (!isGrad) {
					result.addAll(ans);
				} else {
					query_pair(query.getQuestions(), query.getCategory());
					List<ResultClass> ans2 = search();
					for (int i = 0; i < ans.size(); i++) {
						boolean flag = true;
						for (int j = 0; j < ans2.size(); j++) {
							if (ans.get(i).getDocName().equals(ans2.get(j).getDocName())) {
								ResultClass temp = new ResultClass(ans.get(i).DocName,
										ans.get(i).getScore() + ans2.get(j).getScore());
								result.add(temp);
								flag = false;
								break;
							}
						}
						if (flag)
							result.add(ans.get(i));
					}
					for (int i = 0; i < ans2.size(); i++) {
						boolean flag = true;
						for (int j = 0; j < ans.size(); j++) {
							if (ans.get(j).getDocName().equals(ans2.get(i).getDocName())) {
								flag = false;
								break;
							}
						}
						if (flag)
							result.add(ans2.get(i));
					}
				}
				Collections.sort(result);
				for (int i = 0; i < result.size() && i < 10; i++) {
					if (i == 0 && result.get(i).getDocName().equals(key))
						resultScore[0]++;
					if (result.get(i).getDocName().equals(key))
						resultScore[1] += 1.0 / (i + 1);
					System.out.println(result.get(i));
				}
			}
		}
		return resultScore;
	}
}
