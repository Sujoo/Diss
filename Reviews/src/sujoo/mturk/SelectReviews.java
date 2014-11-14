package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class SelectReviews {

    private BufferedReader reader;
    private PrintWriter writer;
    private String gl;

    public static void main(String[] args) throws Exception {
        SelectReviews p = new SelectReviews(4);
        p.identifySubCats();
        //p.selectShoeReviews();
    }

    public SelectReviews(int type) throws Exception {
        // Shoes
        if (type == 1) {
            reader = new BufferedReader(new FileReader("C:\\Users\\mbcusick\\Documents\\Data\\2014\\FitRatingReviews"));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("shoeReviews"), "UTF-8")));
            gl = "gl_shoes";
        }
        // Apparel
        else if (type == 2) {
            reader = new BufferedReader(new FileReader("C:\\Users\\mbcusick\\Documents\\Data\\2014\\FitRatingReviews"));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("apparelReviews"), "UTF-8")));
            gl = "gl_apparel";
        }
        // Cameras
        else if (type ==3 ) {
            reader = new BufferedReader(new FileReader("C:\\Users\\mbcusick\\Documents\\Data\\2014\\CameraKindleReviews"));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("cameraReviews"), "UTF-8")));
            gl = "gl_camera";
        }
        // Books
        else if (type == 4) {
            reader = new BufferedReader(new FileReader("C:\\Users\\mbcusick\\Documents\\Data\\2014\\CameraKindleReviews"));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("bookReviews"), "UTF-8")));
            gl = "gl_digital_ebook_purchase";
        }
    }
    
    public void identifySubCats() throws Exception {
        Multiset<String> subcats = HashMultiset.create();
        Multiset<String> cats = HashMultiset.create();

        String currentLine = null;
        currentLine = reader.readLine();
        // 0 review_id 1 parent_asin_name 2 parent_asin 3 ASIN 4 Product Group Description 5 productcategory
        // 6 subcategory 7 enum_rating 8 submission_date 9 overall_rating 10 helpful_count 11 total_vote_count
        // 12 review_title 13 text_fragment 14 offset
        while ((currentLine = reader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            if (fields[4].equals(gl)) {
                cats.add(fields[5]);
                subcats.add(fields[6]);
            }
        }

        reader.close();
        writer.close();

        System.out.println("Categories");
        for (String cat : cats.elementSet()) {
            System.out.println(cat + " : " + cats.count(cat));
        }
        System.out.println("---------------------------");
        System.out.println("SubCategories");
        for (String subcat : Multisets.copyHighestCountFirst(subcats).elementSet()) {
            System.out.println(subcat + " : " + subcats.count(subcat));
        }
    }

    public void selectShoeReviews() throws Exception {
        int minWordsInReview = 4;
        int reviewsFromEachSubcat = 50;
        Multiset<String> subcats = HashMultiset.create();

        String currentLine = null;
        currentLine = reader.readLine();
        // 0 review_id 1 parent_asin_name 2 parent_asin 3 ASIN 4 Product Group Description 5 productcategory
        // 6 subcategory 7 enum_rating 8 submission_date 9 overall_rating 10 helpful_count 11 total_vote_count
        // 12 review_title 13 text_fragment 14 offset
        while ((currentLine = reader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            if (fields[4].equals("gl_shoes")) {
                String subcat = fields[6];
                if (subcat.equals("4020 Casual") || subcat.equals("5060 Boots") || subcat.equals("7722 Women's Sandals") || subcat.equals("5010 Pumps") || 
                        subcat.equals("7021 Womens Running") || subcat.equals("7011 Mens Running") || subcat.equals("7017 Mens Fitness/Cross-Training") ||
                        subcat.equals("7715 Men's Work/Hunt Boots") || subcat.equals("7712 Men's Sandals") || subcat.equals("5020 Flats")) {
                    if (fields[13].split(" ").length >= minWordsInReview) { 
                        subcats.add(subcat);
                        if (subcats.count(subcat) <= reviewsFromEachSubcat) {
                            writer.println(fields[1] + "\t" + fields[8] + "\t" + fields[9] + "\t" + fields[12] + "\t" + fields[13]);
                        }
                    }
                }
            }
        }

        reader.close();
        writer.close();
        
        for (String subcat : subcats.elementSet()) {
            System.out.println(subcat + " : " + subcats.count(subcat));
        }
    }
}
