package next.webscrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 *
 */
public class App {
	
	private static List<Product> products = new ArrayList<Product>();

	public static void main(String[] args) {
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		
		try {

			FileInputStream excelFile = new FileInputStream(new File("Input.xlsx"));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Cell query = currentRow.getCell(0);
				if (query != null) {
					scrapeData(query.getStringCellValue(), client);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		generateExcel(products);
	}

	public static void scrapeData(String searchQuery, WebClient client) {
		
		HtmlImage imageElement = null;
		HtmlElement titleElement = null;
		HtmlElement priceElement = null;
		HtmlElement fitElement = null;
		String image = null;
		String title = null;
		String price = null;
		String fit = null;
		
		try {
			String searchUrl = "https://www.next.co.uk/shop/gender-women/" + URLEncoder.encode(searchQuery, "UTF-8");
			HtmlPage page = client.getPage(searchUrl);
			HtmlElement categoryElement = page.getFirstByXPath("//div[contains(@class,'SearchedFor')]");
			String category = categoryElement != null ? categoryElement.asText() : "";
			for (int i = 1; i <= 5; i++) {
				imageElement = (HtmlImage) page
						.getFirstByXPath("//article[contains(@id, 'i" + i + "')]//a[contains(@class, 'Image')]//img");
				fitElement = ((HtmlElement) page.getFirstByXPath("//article[contains(@id, 'i" + i + "')]//li//a//img"));
				titleElement = (HtmlElement) page
						.getFirstByXPath("//article[contains(@id, 'i" + i + "')]//*[contains(@class, 'Title')]");
				priceElement = (HtmlElement) page
						.getFirstByXPath("//article[contains(@id, 'i" + i + "')]//*[contains(@class, 'Price')]");
				image = imageElement != null ? imageElement.getSrcAttribute() : "";
				title = titleElement != null ? titleElement.asText() : "";
				price = priceElement != null ? priceElement.asText() : "";
				fit = fitElement != null ? fitElement.getAttribute("data-itemfit") : "";
				products.add(new Product(title, price, image, fit, category));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateExcel(List<Product> products) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();

			XSSFSheet sheet = workbook.createSheet("sheet1");
			
			int rownum = 0;
			
			Row row = sheet.createRow(rownum++);
			
			Cell title = row.createCell(0);
			Cell price = row.createCell(1);
			Cell fit = row.createCell(2);
			Cell image = row.createCell(3);
			Cell category = row.createCell(4);
			title.setCellValue("Title");
			price.setCellValue("Price");
			fit.setCellValue("Fit");
			image.setCellValue("Image");
			category.setCellValue("Category");
			
			for (Product product : products) {
				
				row = sheet.createRow(rownum++);
				title = row.createCell(0);
				price = row.createCell(1);
				fit = row.createCell(2);
				image = row.createCell(3);
				category = row.createCell(4);
				title.setCellValue(product.getTitle());
				price.setCellValue(product.getPrice());
				fit.setCellValue(product.getFit());
				image.setCellValue(product.getImg());
				category.setCellValue(product.getCategory());

			}

			FileOutputStream out = new FileOutputStream(new File("Product.xlsx"));
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
