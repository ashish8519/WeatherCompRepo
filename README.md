# WeatherCompRepo
Selenium Project to compare City Temp from UI and API

Steps performed
Step1:  We are navigating to Weather's page on NDTV website and capturing the temp for the no of cities stored in excel and parsed through DataProvider and also capturing the scrren shot.
Step2: We are building the Rest Assured API call as per the city name, zip code, lat & lon, city code etc and capturing the temp.
Step3: Comparing the Temp from UI and API with a buffer of +/- 2. In case they matched, passed else failed


Note: Created a single @Test annotation and performed the above steps.
We can create different API request like Post, Put, Delete as well by adding POJO classes to build the body and code to handle pathparameter, header.
