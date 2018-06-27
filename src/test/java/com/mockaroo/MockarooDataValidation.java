package com.mockaroo;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MockarooDataValidation {

	static WebDriver driver;
	List<String> Cities;
	List<String> Countries;
	Set<String> citieSet;
	Set<String> countriesSet;
	static int numberOfField = 6;
	static String MOCK_DATAPath;

	@BeforeClass
	public void setUpClass() {

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get("https://mockaroo.com/");
		driver.manage().window().fullscreen();
	}

	@Test (priority = 1)
	public void mockarooDataValidationTest() throws InterruptedException {
		String expectedTitle = "Mockaroo - Random Data Generator and API Mocking Tool | JSON / CSV / SQL / Excel";
		String actualTitle = driver.getTitle();
		assertEquals(actualTitle, expectedTitle);
		assertEquals(driver.findElement(By.xpath("//div[@class='brand']")).getText(), "mockaroo");
		assertEquals(driver.findElement(By.xpath("//div[@class='tagline']")).getText(), "realistic data generator");
		removeExistingFields();
		assertTrue(driver.findElement(By.xpath("//div[@class='column column-header column-name']")).isDisplayed());
		assertTrue(driver.findElement(By.xpath("//div[@class='column column-header column-type']")).isDisplayed());
		assertTrue(driver.findElement(By.xpath("//div[@class='column column-header column-options']")).isDisplayed());
		assertTrue(driver.findElement(By.xpath("//a[.='Add another field']")).isEnabled());
		assertEquals(driver.findElement(By.id("num_rows")).getAttribute("value"), "1000");
		assertTrue(driver.findElement(By.xpath("//option[@value='csv']")).isSelected());
		assertTrue(driver.findElement(By.xpath("//option[@value='unix']")).isSelected());
		assertTrue(driver.findElement(By.id("schema_include_header")).isSelected());
		addAnotherField("City");
		addAnotherField("Country");
		Thread.sleep(5000);
		driver.findElement(By.xpath("//button[.='Download Data']")).click();

	}
	
	// 5
	public static void removeExistingFields() {
		List<WebElement> list = new ArrayList<>();
		list = driver.findElements(By.xpath("//a[@class='close remove-field remove_nested_fields']"));
		for (WebElement each : list) {
			each.click();
		}
	}
	
	// 12-13
	public void addAnotherField(String keyWord) throws InterruptedException {
		numberOfField++;
		
		Thread.sleep(5000);
		WebElement addAnotherFieldEl = driver.findElement(By.xpath("(//div[@class='table-body'])/a"));
		addAnotherFieldEl.click();
		driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[" + numberOfField + "]"))
		.sendKeys(keyWord);
		driver.findElement(By.xpath("(//input[@data-action='type'])[" + numberOfField + "]")).click();
		
		Thread.sleep(5000);
		driver.findElement(By.id("type_search_field")).clear();
		driver.findElement(By.id("type_search_field")).sendKeys(keyWord);
		driver.findElement(By.xpath("//div[.='" + keyWord + "']")).click();
	}

	@Test (priority = 2)
	public void ReadAllDataAndWorkWithCollectionTest() throws InterruptedException, IOException {
		Thread.sleep(5000);

		MOCK_DATAPath = "/Users/viorica/Downloads/MOCK_DATA.csv";
		readFileAndVerify();
		Collections.sort(Cities);
		Collections.sort(Countries);
		System.out.println("\nThe longest city name: " + longestName(Cities));
		System.out.println("The shortest city name: " + shortestName(Cities) + "\n");

		howManytimesEachStringIsMentioned(Countries);

		readFileInHashSet();

		verifyCountInArrayAndSet(Countries, countriesSet);

	}


	// 18 - 21
	public void readFileAndVerify() throws IOException {

		FileReader fileR = new FileReader(MOCK_DATAPath);
		BufferedReader buffR = new BufferedReader(fileR);

		Cities = new ArrayList<>();
		Countries = new ArrayList<>();

		int count = -1; // because of "City" and "Countries" words
		String line = "";
		while ((line = buffR.readLine()) != null) {
			String tempWithComma = line;
			String tempOutComma[] = tempWithComma.split(",");
			Cities.add(tempOutComma[0]);
			Countries.add(tempOutComma[1]);
			count++;
		}
		assertEquals(count, 1000);
		assertTrue(Cities.get(0).equalsIgnoreCase("City"));
		assertTrue(Countries.get(0).equalsIgnoreCase("Country"));
	}

	// 22 -> find the city with the shortest name
	public String shortestName(List<String> arrName) {

		String temp = arrName.get(0);
		for (String each : arrName) {
			if (temp.length() > each.length()) {
				temp = each;
			}
		}
		return temp;
	}

	// 22 -> find the city with the longest name
	public String longestName(List<String> arrName) {

		String temp = arrName.get(0);
		for (String each : arrName) {
			if (temp.length() < each.length()) {
				temp = each;
			}
		}
		return temp;
	}

	// 23 -> find how many times each Country is mentioned
	public void howManytimesEachStringIsMentioned(List<String> arrName) {

		Set<String> arrNameSet = new HashSet<>(arrName);
		List<String> arrUniqueName = new ArrayList<>(arrNameSet);
		Collections.sort(arrUniqueName);
		for (int i = 0; i < arrUniqueName.size(); i++) {
			int times = 0;
			times = Collections.frequency(arrName, arrUniqueName.get(i));
			System.out.println(arrUniqueName.get(i) + " - " + times);
		}
	}

	// 24 & 26 -> From file add all Cities and Countries to HashSet
	public void readFileInHashSet() throws IOException {

		FileReader fileR = new FileReader(MOCK_DATAPath);
		BufferedReader buffR = new BufferedReader(fileR);

		citieSet = new HashSet<>();
		countriesSet = new HashSet<>();

		String line = "";
		while ((line = buffR.readLine()) != null) {
			String tempWithComma = line;
			String tempOutComma[] = tempWithComma.split(",");
			citieSet.add(tempOutComma[0]);
			countriesSet.add(tempOutComma[1]);

		}
	}

	// 25 & 27 - > Count how many unique cities and countries are in array lists and
	// assert with the count of HashSet
	public void verifyCountInArrayAndSet(List<String> listNames, Set<String> setNames) {
		Set<String> tempSet = new HashSet<>(listNames);
		List<String> listUniquesNames = new ArrayList<>(tempSet);

		assertEquals(listUniquesNames.size(), setNames.size());
	}

	@AfterClass
	public void tearDownClass() {
		driver.close();
	}

}
