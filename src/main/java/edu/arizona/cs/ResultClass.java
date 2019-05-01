package edu.arizona.cs;

import org.apache.lucene.document.Document;


public class ResultClass implements Comparable<ResultClass>{

    Document DocName ;
    double doc_score = 0;

    public ResultClass(Document DocName, double doc_score){
        this.DocName = DocName;
        this.doc_score = doc_score;
    }

    public String getDocName() {
    	return DocName.get("docid");
    }
    
    public double getScore(){
    	return doc_score;
    }
    
    public String toString(){
    	return "DocName: " + DocName.get("docid") + " DocScore: " + doc_score;
    }

	@Override
	public int compareTo(ResultClass o) {
		return o.doc_score > this.doc_score ? 1 : -1;
	}
}
