package ndtv;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import pageObjects.LandingPage;
import pageObjects.WeatherPage;

import static io.restassured.RestAssured.*;
import resources.base;

public class Weather extends base {
	
	private static Logger log = LogManager.getLogger(base.class.getName());
	public WebDriver driver;
	public String testDataPath  = System.getProperty("user.dir")+"\\src\\main\\java\\resources\\TestData.xlsx";
	
	@BeforeSuite
	public void launch() throws IOException  {
		driver = initializeDriver();
		log.info("Driver is initialized");
		driver.get(prop.getProperty("url"));
		log.info("Launched NDTV Home Page");		
	}
	
	@AfterSuite
	public void teardown() {
		driver.close();
		log.info("Closing the browser");
	}
	
	@BeforeTest
	public void naviagteWeatherPage() throws InterruptedException {
		LandingPage l  = new LandingPage(driver);
		
		wait.until(ExpectedConditions.elementToBeClickable(l.subMenu()));
		l.subMenu().click();
		log.info("Clicked on sub menu options");
		l.weather().click();
		log.info("Navigated to NDTV Weather Page");
	}
	
	@Test(dataProvider = "getData")
	public void basePageNavigation(String place, String pathParam, String queryParam, String requestType, String resource, String statusCode) throws Exception {

/*********************************************************************************************
				UI Code Started
**********************************************************************************************/		
	  WeatherPage w = new WeatherPage(driver);
	  
	  Actions a = new Actions(driver);
	  wait.until(ExpectedConditions.visibilityOfElementLocated(w.searchWeatherLoc));
	  wait.until(ExpectedConditions.elementToBeClickable(w.searchWeather()));
	  w.searchWeather().clear();
	  a.moveToElement(w.searchWeather()).click().sendKeys("").build().perform();
	  a.moveToElement(w.searchWeather()).click().sendKeys(place).sendKeys(Keys.ENTER).build().perform();
	  
	  Assert.assertTrue(w.cityCheckBox(place).isDisplayed());
	  	  
	  if(!w.cityCheckBox(place).isSelected()) {
		  w.cityCheckBox(place).click();
		  log.info("Checked the "+place+ " checkbox");
		  }
	  
	  WebElement wb = w.cityTempBaner(place);
	  JavascriptExecutor jse = (JavascriptExecutor)driver;
	  jse.executeScript("arguments[0].click();", wb);
	  log.info("Clicked on the "+place+ " on map to see weather pop-up");
	  
	  String sh_Name = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
	  sh_Name = place+"_"+sh_Name;
	  sh_Name = getScreenshotPath(sh_Name,driver);
	  log.info("Capture the tempature screenshot "+sh_Name);
	  
	  
	  String temp_UI = w.cityTemp(place).getText(); 
	  temp_UI = temp_UI.replace("℃", "");
	  System.out.println(place + " : " +temp_UI);
	  log.info("Captured the temp of "+place+ " in Celsius: "+temp_UI);
		
/*********************************************************************************************
				UI Ended and API Code Started
**********************************************************************************************/
	 
	Response response = null;		  
	RequestSpecification req = new RequestSpecBuilder().setBaseUri(prop.getProperty("baseURI")).addQueryParam("appid", "7fe67bf08c80ded756e598d6f8fedaea").addQueryParam("units", "metric").build();
	ResponseSpecification res = new ResponseSpecBuilder().expectStatusCode(Integer.parseInt(statusCode)).build();
	// CODE TO ADD QUERY PARAMETERS START
	if (!queryParam.trim().equalsIgnoreCase("null")) {
		if(queryParam.contains("&&")) {
			String[] queryParams = queryParam.split("&&");
			for(int i=0;i<queryParams.length;i++) {
				String[] queryPar = queryParams[i].split(":");
				
				req = req.queryParam(queryPar[0].trim(), queryPar[1].trim());
				} 
			}else { 
				String[] queryPar = queryParam.split(":"); 
				req = req.queryParam(queryPar[0].trim(), queryPar[1].trim());
				}
	}
	// ENDS
		RequestSpecification request = given().spec(req);
		
	// CODE TO HANDLE REQUEST TYPE AND RESOURCE
		if (requestType.equalsIgnoreCase("post")) {
			response = request.when().post(resource).then().spec(res).extract().response();
		 }else if (requestType.equalsIgnoreCase("get")) {
			 response = request.when().get(resource).then().spec(res).extract().response();
		 }else if (requestType.equalsIgnoreCase("put")) { 
			 response = request.when().put(resource).then().spec(res).extract().response();
		 }else if (requestType.equalsIgnoreCase("delete")) {
			 response = request.when().delete(resource).then().spec(res).extract().response();
		 	}
					  
		String resString = response.asString();
		System.out.println(resString);
		
		JsonPath js = new JsonPath(resString);
		String temp_API = js.getString("main.temp");
		System.out.println(place + " : " +temp_API);

/*********************************************************************************************
				API Ended and UI-API Temperature Comparison
**********************************************************************************************/
		
		
		boolean Result = tempComparision(temp_UI,temp_API);
		System.out.println("City : "+place+" ,UI Temp: "+temp_UI+" , API Temp: "+temp_API);
		Assert.assertTrue(Result, "Temp comparision for city: "+place+" ,UI Temp: "+temp_UI+" , API Temp: "+temp_API);
		w.closepopup().click();	  
	
	}
/*********************************************************************************************
				End of Code
**********************************************************************************************/	
	@DataProvider
	public Object[][] getData() throws Exception{
		
		
		XSSFWorkbook workbook = new XSSFWorkbook(testDataPath);
		
		XSSFSheet sheet = workbook.getSheet("TestData");
		
		XSSFRow row = sheet.getRow(0);
		int colCnt = row.getLastCellNum();
		
		int rowCnt = sheet.getLastRowNum()+1;
		Object[][] data = new Object[rowCnt-1][colCnt];
		System.out.println("total no of data rows: "+(rowCnt-1));
		System.out.println("total no of columns: "+colCnt);		
		
		int i=0;
		int j=0;
		Iterator<Row> rows = sheet.iterator();
		while(rows.hasNext()){
			if(i==0) {
				rows.next();
			}
			
			Iterator<Cell> ce = rows.next().cellIterator();
			while(ce.hasNext()) {
				data[i][j] = ce.next().getStringCellValue().toString();
				
				j=j+1;				
			}
			j=0;
			i=i+1;
			
		}
		workbook.close();	
		return data;
	}
	
	
}

