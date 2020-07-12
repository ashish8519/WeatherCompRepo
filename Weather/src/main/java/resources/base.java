package resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;


public class base<RequestSpecification> {
	
	public WebDriver driver;
	public Properties prop;
	public WebDriverWait wait ;
	public Wait<WebDriver> fwait;
	
	public WebDriver initializeDriver() throws IOException {
		String configFilePath = System.getProperty("user.dir")+"\\src\\main\\java\\resources\\config.properties";
		prop = new Properties();
		FileInputStream fis = new FileInputStream(configFilePath);
		prop.load(fis);
		//String chromeDriverPath = System.getProperty("user.dir")+"\\src\\main\\java\\resources\\chromedriver.exe";
		String browserName = prop.getProperty("browser");
		System.out.println(browserName);
		
		if(browserName.equals("chrome")) {
			WebDriverManager.chromedriver().setup();
			//System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			ChromeOptions c = new ChromeOptions();
			c.addArguments("--disable-notifications");
			driver = new ChromeDriver(c);			
		}else if (browserName.equals("firefox")) {
			System.out.println("Initialize FireFox Driver");
		}else if(browserName.equals("ie")){
			System.out.println("Initialize IE Driver");
		}
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		wait = new WebDriverWait(driver,40,200);
		fwait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);
		return driver;
	}

	public String getScreenshotPath(String testcaseName, WebDriver driver) throws Exception {
		File source = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		String destinationFile = System.getProperty("user.dir")+"\\src\\main\\java\\reports\\"+testcaseName+".png";
		//System.out.println("destination file: "+destinationFile);
		FileUtils.copyFile(source,new File(destinationFile));
		return destinationFile;
		}
	
	public boolean tempComparision(String temp_UI, String temp_API) {
		double temperature_UI = Double.parseDouble(temp_UI);
		double temperature_API = Double.parseDouble(temp_API);
		
		if(Math.abs(Math.abs(temperature_UI)-Math.abs(temperature_API))<=2) {		
			return true; 
		}else
			return false;
	}
}
