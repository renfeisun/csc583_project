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
import org.apache.lucene.store.FSDirectory;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;


public class QueryEngine {
    private StandardAnalyzer analyzer = null;    
    private IndexWriter writer = null;    
    private Directory index = null;
    private Query query = null;
    private IndexSearcher searcher = null;
    private SourceProcessor sp;
    
    public QueryEngine(String inputFileObj){
        try{
            analyzer = new StandardAnalyzer();
            //index = FSDirectory.open(Paths.get("./index"));
            index = FSDirectory.open(Paths.get("./index"));
            writer = new IndexWriter(index, new IndexWriterConfig(analyzer));      
            sp = new SourceProcessor(inputFileObj);
        	sp.index_generate(writer);
            searcher = new IndexSearcher(DirectoryReader.open(index));
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
        	System.out.println(query);
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
    	QueryEngine engine = new QueryEngine("input.txt");
    }

    public List<ResultClass> runQ13a(String[] query) throws java.io.FileNotFoundException,java.io.IOException {
        try{        
            query_gen(query, 5);
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
