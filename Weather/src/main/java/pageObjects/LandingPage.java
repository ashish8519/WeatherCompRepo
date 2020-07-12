package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LandingPage {
	
	public WebDriver driver;
	
	public LandingPage(WebDriver driver) {
		this.driver = driver;
	}
	
	private By moreSubMenu = By.id("h_sub_menu");
	private By weatherLink = By.xpath("//a[contains(@href,'Weather')]");
	
	public WebElement subMenu() {
		return driver.findElement(moreSubMenu);
	}
	
	public WebElement weather() {
		return driver.findElement(weatherLink);
	}
	
}
