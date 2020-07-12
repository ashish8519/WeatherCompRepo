package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WeatherPage {
	
public WebDriver driver;

	
	public WeatherPage(WebDriver driver) {
		this.driver = driver;
		}
	
	private By searchReset = By.cssSelector("#icon_holder");	
	public By searchWeatherLoc = By.cssSelector("#searchBox");
	private By closepopup =By.xpath("//a[@href='#close']");
	
	
	public By setXpathLoc(String loc, String city) {
		loc = loc.replace("varCity", city);
		return By.xpath(loc);
	}
		
	public WebElement searchWeather() {
		return driver.findElement(searchWeatherLoc);
	}
	
	public WebElement searchReset() {
		return driver.findElement(searchReset);
	}
	
	public WebElement closepopup() {
		return driver.findElement(closepopup);
	}
	
	public WebElement cityCheckBox(String citi ) {
		
		return driver.findElement(setXpathLoc("//label[@for='varCity'] //input",citi));
	}
	
	public WebElement cityTempBaner(String citi) {
		return driver.findElement(setXpathLoc("//div[@title='varCity']",citi));
	}
	
	public WebElement cityTemp(String citi) {
		return driver.findElement(setXpathLoc("//div[@title='varCity'] //div/span[1]",citi));
	}

}
