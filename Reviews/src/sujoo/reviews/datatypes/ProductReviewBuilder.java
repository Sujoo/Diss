package sujoo.reviews.datatypes;

public class ProductReviewBuilder {
	private final ProductReview review;
	
	private ProductReviewBuilder() {
		review = new ProductReview();
	}
	
	public static ProductReviewBuilder productReview() {
		return new ProductReviewBuilder();
	}
	
	public ProductReviewBuilder withId(int id) {
		review.setId(id);
		return this;
	}
	
	public ProductReviewBuilder withGl(String gl) {
		review.setGl(gl);
		return this;
	}
	
	public ProductReviewBuilder withStarRating(int starRating) {
		review.setStarRating(starRating);
		return this;
	}
	
	public ProductReviewBuilder withStarRating(String starRating) {
		return withStarRating(handleString(starRating));
	}
	
	public ProductReviewBuilder withHelpfulVotes(int helpfulVotes) {
		review.setHelpfulVotes(helpfulVotes);
		return this;
	}
	
	public ProductReviewBuilder withHelpfulVotes(String helpfulVotes) {
		return withHelpfulVotes(handleString(helpfulVotes));
	}
	
	public ProductReviewBuilder withTotalVotes(int totalVotes) {
		review.setTotalVotes(totalVotes);
		return this;
	}
	
	public ProductReviewBuilder withTotalVotes(String totalVotes) {
		return withTotalVotes(handleString(totalVotes));
	}
	
	public ProductReviewBuilder withHeadline(String headline) {
		review.setHeadline(headline);
		return this;
	}
	
	public ProductReviewBuilder withReviewText(String reviewText) {
		review.setReviewText(reviewText);
		return this;
	}
	
	public ProductReview build() {
		return review;
	}
	
	private int handleString(String number) {
		if (number.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(number);
		}
	}
}
