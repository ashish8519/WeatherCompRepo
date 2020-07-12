package ndtv;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import resources.ExtentReporterNG;
import resources.base;

public class Listeners extends base implements ITestListener {
	
	ExtentReports extents = ExtentReporterNG.getReportObject();
	ExtentTest test;
	
	public void onTestStart(ITestResult result) {
		// TODO Auto-generated method stub
		test = extents.createTest(result.getMethod().getMethodName());
		test.info("Test Started_"+result.getParameters()[0]);
		
	}

	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		test.log(Status.PASS, "Test Passed_"+result.getParameters()[0]);
	}

	public void onTestFailure(ITestResult result) {
		// TODO Auto-generated method stub
	
		WebDriver driver = null;
		String testMethodName = result.getMethod().getMethodName();
				
		testMethodName = result.getParameters()[0].toString()+"_"+testMethodName;
		
		try {
			driver = (WebDriver)result.getTestClass().getRealClass().getDeclaredField("driver").get(result.getInstance());
		} catch (Exception e) {
			
		}
		
		try {
			test.fail(result.getThrowable());
			test.addScreenCaptureFromPath(getScreenshotPath(testMethodName, driver),result.getMethod().getMethodName());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onTestFailedWithTimeout(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub
		
	}

	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub
		test.info("Test is complete");
		extents.flush();
	}

}