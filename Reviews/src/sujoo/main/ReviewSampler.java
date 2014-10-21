package sujoo.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.google.common.collect.Lists;

import static sujoo.reviews.datatypes.ProductReviewBuilder.productReview;
import sujoo.nlp.stanford.StanfordNLP;
import sujoo.reviews.datatypes.ProductReview;
import sujoo.util.FileReaderUtil;

public class ReviewSampler {
	private final int maxSampleSize = 50;

	private List<ProductReview> longReviews;
	private List<ProductReview> shortReviews;
	private List<ProductReview> helpfulApparelReviews;
	private List<ProductReview> helpfulMovieReviews;
	
	private Random random;

	private StanfordNLP nlp;

	public static void main(String[] args) {
		ReviewSampler sampler = new ReviewSampler();
		try {
			sampler.sampleReviews();
			sampler.outputReviews();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ReviewSampler() {
		longReviews = Lists.newArrayList();
		shortReviews = Lists.newArrayList();
		helpfulApparelReviews = Lists.newArrayList();
		helpfulMovieReviews = Lists.newArrayList();
		random = new Random();
		nlp = StanfordNLP.createBasicWordCounter();
	}

	public void sampleReviews() throws FileNotFoundException, IOException {
		FileReaderUtil reader = getFileReader();
		int counter = 0;
		while (reader.hasNext()) {
			String[] line = reader.next();

			ProductReview review = productReview().withGl(line[2])
					.withStarRating(line[3]).withHelpfulVotes(line[4])
					.withTotalVotes(line[5]).withReviewText(line[7]).build();

			sampleReview(review);
			counter++;
		}
		System.out.println("Count: " + counter);
	}

	private FileReaderUtil getFileReader() throws FileNotFoundException {
		File file = null;

		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		} else {
			System.exit(0);
		}

		System.out.println("What is the split character for your input file?");
		Scanner input = new Scanner(System.in);
		String split = input.nextLine();
		input.close();

		return new FileReaderUtil(file, split);
	}

	private void sampleReview(ProductReview review) {
		if (!samplesFull() && random.nextBoolean()) {
			if (notFull(longReviews)
					&& nlp.getWordCount(review.getReviewText()) > 1000) {
				longReviews.add(review);
			} else if (notFull(shortReviews)
					&& nlp.getWordCount(review.getReviewText()) == 20) {
				shortReviews.add(review);
			}

			if (isHelpfulGlReview(review, "gl_apparel")
					&& notFull(helpfulApparelReviews)) {
				helpfulApparelReviews.add(review);
			}
			if (isHelpfulGlReview(review, "gl_dvd")
					&& notFull(helpfulMovieReviews)) {
				helpfulMovieReviews.add(review);
			}
		}
	}

	private boolean samplesFull() {
		return longReviews.size() == maxSampleSize
				&& shortReviews.size() == maxSampleSize
				&& helpfulApparelReviews.size() == maxSampleSize
				&& helpfulMovieReviews.size() == maxSampleSize;
	}

	private boolean notFull(List<ProductReview> list) {
		return list.size() < maxSampleSize;
	}

	private boolean isHelpfulGlReview(ProductReview review, String gl) {
		return review.getGl().equals(gl) && review.getTotalVotes() >= 10
				&& review.getHelpfulVotes() == review.getTotalVotes();
	}

	public void outputReviews() throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(
					"sample_reviews.txt"));
			writer.printf("Long Reviews (%d):%n", longReviews.size());
			for (ProductReview review : longReviews) {
				writer.println(review.getReviewText());
			}
			writer.println();

			writer.printf("Short Reviews (%d):%n", shortReviews.size());
			for (ProductReview review : shortReviews) {
				writer.println(review.getReviewText());
			}
			writer.println();

			writer.printf("Helpful Apparel Reviews (%d):%n",
					helpfulApparelReviews.size());
			for (ProductReview review : helpfulApparelReviews) {
				writer.println(review.getReviewText());
			}
			writer.println();

			writer.printf("Helpful Movie Reviews (%d):%n",
					helpfulMovieReviews.size());
			for (ProductReview review : helpfulMovieReviews) {
				writer.println(review.getReviewText());
			}
			writer.println();
			writer.close();
	}
}
