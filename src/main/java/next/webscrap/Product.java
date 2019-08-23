package next.webscrap;

public class Product {
	
	private String title;
	private String price;
	private String fit;
	private String img;
	private String category;

	public Product(String title, String price, String img, String fit,String category) {
		super();
		this.title = title;
		this.price = price;
		this.fit = fit;
		this.img = img;
		this.category = category;
	}
	
	public String getTitle() {
		return title;
	}

	public String getPrice() {
		return price;
	}

	public String getFit() {
		return fit;
	}

	public String getImg() {
		return img;
	}
		
	public String getCategory() {
		return category;
	}
}
