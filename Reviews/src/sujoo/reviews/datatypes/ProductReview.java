package sujoo.reviews.datatypes;

public class ProductReview {

	private int id;
	private String gl;
	private int starRating;
	private double helpfulVotes;
	private double totalVotes;
	private String headline;
	private String reviewText;

	public ProductReview() {
		id = -1;
		gl = "";
		starRating = -1;
		helpfulVotes = -1;
		totalVotes = -1;
		headline = "";
		reviewText = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGl() {
		return gl;
	}

	public void setGl(String gl) {
		this.gl = gl;
	}

	public int getStarRating() {
		return starRating;
	}

	public void setStarRating(int starRating) {
		this.starRating = starRating;
	}

	public double getHelpfulVotes() {
		return helpfulVotes;
	}

	public void setHelpfulVotes(int helpfulVotes) {
		this.helpfulVotes = helpfulVotes;
	}

	public double getTotalVotes() {
		return totalVotes;
	}

	public void setTotalVotes(int totalVotes) {
		this.totalVotes = totalVotes;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getReviewText() {
		return reviewText;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
}
