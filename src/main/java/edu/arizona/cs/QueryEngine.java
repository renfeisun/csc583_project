package edu.arizona.cs;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import edu.stanford.nlp.simple.*;

import java.io.*;
import java.util.Arrays;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class QueryEngine {
    private String input_file ="";
    private StandardAnalyzer analyzer = null;    
    private IndexWriter writer = null;    
    private Directory index = null;
    private Query query = null;
    private IndexSearcher searcher = null;
    
    public QueryEngine(String inputFileObj){
        try{
            input_file =inputFileObj;
            analyzer = new StandardAnalyzer();
            index = new RAMDirectory();
            writer = new IndexWriter(index, new IndexWriterConfig(analyzer));      
            index_gen();
            searcher = new IndexSearcher(DirectoryReader.open(index));
        } catch (Exception ex){
            System.out.println(ex.getMessage());    
        }

    }

    private void index_gen() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(input_file).getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                Document doc = new Document();
                String line = scanner.nextLine();
                String docID = line.split(" ")[0];            
                String content = line.replace(docID+" ", "");
                doc.add(new StringField("docid", docID, Field.Store.YES));
                doc.add(new TextField("contents", content, Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.close();
            scanner.close();
        } catch (Exception ex){
            System.out.println(ex.getMessage());    
        }
    }

    private void query_gen(String[] query_list, int type){
        try{
            switch(type){
                case 1:
                    {
                        String querystr = "contents:" + query_list[0] + " AND contents:" + query_list[1];
                        query = new QueryParser("", analyzer).parse(querystr);
                        break;
                    }
                case 2:
                    {
                        String querystr = "contents:" + query_list[0] + "AND NOT contents:" + query_list[1];
                        query = new QueryParser("", analyzer).parse(querystr);
                        break;
                    }
                case 3:
                    {
                        String querystr = "contents:" + "\"" + String.join(" ", query_list) + "\"" + "~1";
                        query = new QueryParser("", analyzer).parse(querystr);
                        break;
                    }
                case 4:
                    {
                        String querystr = "contents:" + query_list[0] + " OR contents:" + query_list[1];
                        query = new QueryParser("", analyzer).parse(querystr);
                        break;
                    }
                case 5:
                    {
                        String querystr = String.join(" ", query_list);
                        query = new QueryParser("contents", analyzer).parse(querystr);
                        break;
                    }    
                default:
                    break;
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());    
        }

    }

    private List<ResultClass> search(){
        List<ResultClass>  ans=new ArrayList<ResultClass>();
        int hitsPerPage = 10;        
        TopDocs docs = null;
        try{        
            docs = searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                Document d = searcher.doc(hits[i].doc);
                ans.add(new ResultClass(d, hits[i].score));                
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());    
        }
        for(int i = 0; i < ans.size(); i++){
            System.out.println(ans.get(i));
        }
        return ans;
        
    }

    public static void main(String[] args ) {

        try {

            String fileName = "input.txt";
            System.out.println("********Welcome to  Homework 3!");
            String[] query13a = {"information", "retrieval"};
            QueryEngine objQueryEngine = new QueryEngine(fileName);
            List<ResultClass> ans1 = objQueryEngine.runQ1(query13a);
            List<ResultClass> ans2 = objQueryEngine.runQ13a(query13a);
            List<ResultClass> ans3 = objQueryEngine.runQ13b(query13a);
            List<ResultClass> ans4 = objQueryEngine.runQ13c(query13a);
            List<ResultClass> ans5 = objQueryEngine.runQ14(query13a);
            List<ResultClass> ans6 = objQueryEngine.runQ13c(query13a);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    public List<ResultClass> runQ1(String[] query) throws java.io.FileNotFoundException,java.io.IOException {
        try{        
            query_gen(query, 4);
            this.searcher.setSimilarity(new BM25Similarity());    
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }       
        return search();
    }
    public List<ResultClass> runQ13a(String[] query) throws java.io.FileNotFoundException,java.io.IOException {
        try{        
            query_gen(query, 1);
            this.searcher.setSimilarity(new BM25Similarity());  
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }       
        return search();
    }

    public List<ResultClass> runQ13b(String[] query) throws java.io.FileNotFoundException,java.io.IOException {
        try{        
            query_gen(query, 2);
            this.searcher.setSimilarity(new BM25Similarity()); 
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }       
        return search();
    }

    public List<ResultClass> runQ13c(String[] query) throws java.io.FileNotFoundException,java.io.IOException {
        try{        
            query_gen(query, 3);
            this.searcher.setSimilarity(new BM25Similarity());     
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }       
        return search();
    }

    public List<ResultClass> runQ14(String[] query) throws java.io.FileNotFoundException,java.io.IOException {
        try{        
            query_gen(query, 5);
            this.searcher.setSimilarity(new ClassicSimilarity());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }       
        return search();
    }
}
