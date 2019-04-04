package edu.arizona.cs;



import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.lucene.document.Document;


public class TestQ1 {

    @Test
    public void testDocsAndScores() {

        String inputFileFullPath="input.txt";

        QueryEngine objQueryEngine = new QueryEngine(inputFileFullPath); // not here
        try {

            String[] common_query = {"information", "retrieval"};


            List<ResultClass> ans1 = objQueryEngine.runQ1(common_query);
            String[] doc_names_q1 = {"Doc1", "Doc2"};
            int counter1 = 0;


            for (ResultClass docs : ans1) {

                assertEquals(doc_names_q1[counter1], docs.DocName.get("docid"));
                counter1 = counter1 + 1;
            }


            List<ResultClass> ans13a = objQueryEngine.runQ13a(common_query);
            String[] doc_names_q13a = {"Doc1", "Doc2"};
            int counter2 = 0;
            for (ResultClass docs : ans13a) {

                assertEquals(doc_names_q13a[counter2], docs.DocName.get("docid"));
                counter2 = counter2 + 1;
            }


            List<ResultClass> ans13b = objQueryEngine.runQ13b(common_query);
            assertEquals(ans13b.size(), 0);


            List<ResultClass> ans13c = objQueryEngine.runQ13c(common_query);
            String[] doc_names_q13c = {"Doc1" };
            int counter3 = 0;
            for (ResultClass docs : ans13c) {
                assertEquals(doc_names_q13c[counter3], docs.DocName.get("docid"));
                counter2 = counter3 + 1;
            }



        }
        catch ( java.io.FileNotFoundException ex)
        {
            System.out.println(ex.getMessage()); }

        catch ( java.io.IOException ex)
        {
            System.out.println(ex.getMessage()); }

    }


}



