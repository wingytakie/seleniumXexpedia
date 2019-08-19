import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpediaSingleSearch {

    WebDriver driver;
    WebDriverWait wait;
    private SimpleDateFormat expediaFormat = new SimpleDateFormat("MM/dd/yyyy");

    private String type; //Type of purchase: 'flight', 'hotel', 'package'
    private String orig = "JFK"; //Origin Airport, location
    private String dest = "DFW"; //Destination Airport, location
    private String depart = "10/10/2019"; //Departure Date in MM/DD/YYYY format
    private String retrn = "10/20/2019" ; //Departure Date in MM/DD/YYYY format
    private boolean car = true, hotel = false, flight = false;
    private int adults = 1, children = 3, infants = 0;
    private int[] childAge = new int[] {5,2,4,6}; //list of children age
    private int[] infantAge = new int[] {1, 0}; //list of infant age. 1 or 0 (Under 1)
    private String rental = "air"; // choose between "rental" for retnal car OR "air" for airport transportation
    private String pickTime = "4:00 am", dropTime = "11:00 am" ; //Pickup / Dropoff time for Rental car | Airport Transport will use these time for pickup/dropoff


    @BeforeEach
    public void init(){
        System.setProperty("webdriver.chrome.driver", "Path for chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.expedia.com/");
        wait = new WebDriverWait(driver, 10);
    }



    @Test
    void expedia() throws ParseException{
        //Expedia only allowing booking between 1 - 6 travelers for each room.
        if ((adults + children + infants) <= 6 && (adults + children + infants) >= 1 && (adults >= 1)) {
            if (flight == true) {
                if (car == true) {
                    if (hotel == true) {
                        type = "package";
                        bundle();
                    } else if (hotel == false) {
                        type = "package";
                        flightCar();
                    }
                } else if (car == false) {
                    if (hotel == true) {
                        type = "package";
                        flightHotel();
                    } else if (hotel == false) {
                        if (retrn != null || retrn != "") {
                            type = "flight";
                            flight(depart, retrn);
                        } else {
                            type = "flight";
                            flight(depart);
                        }
                    }
                }
            } else if (flight == false) {
                if (car == true) {
                    if (hotel == true) {
                        type = "package";
                        hotelCar();
                    } else if (hotel == false) {
                        type = "car";
                        if (rental == "rental") {
                            carOnly();
                        } else if (rental == "air"){
                            airTrans();
                        }
                    }
                } else if (car == false) {
                    if (hotel == true) {
                        type = "hotel";
                        hotel();
                    } else if (hotel == false) {
                        // Cruise
                        //cruise();
                    }
                }
            }
        }
    }

    private void flight(String depart, String retrn) throws ParseException {
        if (checkDate()){
            WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='" + type + "']"));
            typeOfPurchase.click();
            WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*/label[contains(@id,'roundtrip')]")));
            typeOfTrip.click();

            WebElement origin = driver.findElement(By.xpath("//*[@id='flight-origin-hp-flight']"));
            origin.click();
            origin.sendKeys(orig);
            WebElement originAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + orig + "')]")));
            originAirport.click();

            WebElement destination = driver.findElement(By.xpath("//*[@id='flight-destination-hp-flight']"));
            destination.click();
            destination.sendKeys(dest);
            WebElement destAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + dest + "')]")));
            destAirport.click();

            WebElement departureDate = driver.findElement(By.xpath("//*[@id='flight-departing-hp-flight']"));
            departureDate.click();
            departureDate.sendKeys(depart);

            WebElement returningDate = driver.findElement(By.xpath("//*[@id='flight-returning-hp-flight']"));
            returningDate.click();
            returningDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            returningDate.sendKeys(retrn);

            driver.findElement(By.xpath("//*[@id='traveler-selector-hp-flight']")).click();
            if (adults > 1) {
                WebElement adult = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-flight']//button)[3]"));
                for (int a = 1; a < adults; a++) {
                    adult.click();
                }
            }
            if (children > 0) {
                WebElement child = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-flight']//button)[5]"));
                for (int a = 0; a < children; a++) {
                    child.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@id='flight-age-select-" + (a + 1) + "-hp-flight'])[1]"));
                    age.click();
                    age.sendKeys(String.valueOf(childAge[a]));
                }
            }

            if (infants > 0) {
                WebElement infant = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-flight']//button)[7]"));
                for (int a = 0; a < infants; a++) {
                    infant.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-infant-age-1-" + (a + 1) + "'])[1]"));
                    age.click();
                    if (infantAge[a] == 0) {
                        age.sendKeys("Under 1");
                    } else {
                        age.sendKeys("1");
                    }
                }
            }
            WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[1]/.."));
            submitSearch.click();
        }
    }
    private void flight(String depart){
        WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='"+type+"']"));
        typeOfPurchase.click();
        WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*/label[contains(@id,'one-way')]")));
        typeOfTrip.click();

        WebElement origin = driver.findElement(By.xpath("//*[@id='flight-origin-hp-flight']"));
        origin.click();
        origin.sendKeys(orig);
        WebElement originAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'"+orig+"')]")));
        originAirport.click();

        WebElement destination = driver.findElement(By.xpath("//*[@id='flight-destination-hp-flight']"));
        destination.click();
        destination.sendKeys(dest);
        WebElement destAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'"+dest+"')]")));
        destAirport.click();

        WebElement departureDate = driver.findElement(By.xpath("//*[@id='flight-departing-single-hp-flight']"));
        departureDate.click();
        departureDate.sendKeys(depart);


        driver.findElement(By.xpath("//*[@id='traveler-selector-hp-flight']")).click();
        if (adults > 1){
            WebElement adult = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-flight']//button)[3]"));
            for (int a = 1; a < adults; a++){
                adult.click();
            }
        }
        if (children > 0){
            WebElement child = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-flight']//button)[5]"));
            for (int a = 0; a < children; a++){
                child.click();
                WebElement age = driver.findElement(By.xpath("(//*[@id='flight-age-select-"+(a+1)+"-hp-flight'])[1]"));
                age.click();
                age.sendKeys(String.valueOf(childAge[a]));
            }
        }

        if (infants > 0){
            WebElement infant = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-flight']//button)[7]"));
            for (int a = 0; a < infants; a++){
                infant.click();
                WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-infant-age-1-"+(a+1)+"'])[1]"));
                age.click();
                if (infantAge[a] == 0){
                    age.sendKeys("Under 1");
                } else {
                    age.sendKeys("1");
                }
            }
        }
        WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[1]/.."));
        submitSearch.click();
    }

    private void hotel() throws ParseException{
        if(checkDate()){
            WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='"+type+"']"));
            typeOfPurchase.click();

            WebElement destination = driver.findElement(By.xpath("//*[@id='hotel-destination-hp-hotel']"));
            destination.click();
            destination.sendKeys(dest);
            WebElement destAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'"+dest+"')]")));
            destAirport.click();

            WebElement departureDate = driver.findElement(By.xpath("//*[@id='hotel-checkin-hp-hotel']"));
            departureDate.click();
            departureDate.sendKeys(depart);

            WebElement returningDate = driver.findElement(By.xpath("//*[@id='hotel-checkout-hp-hotel']"));
            returningDate.click();
            returningDate.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
            returningDate.sendKeys(retrn);

            driver.findElement(By.xpath("//*[@id='traveler-selector-hp-hotel']")).click();
            if (adults > 2){
                WebElement adult = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-hotel']//button)[3]"));
                for (int a = 2; a < adults; a++){
                    adult.click();
                }
            }
            if (children > 0){
                WebElement child = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-hotel']//button)[5]"));
                for (int a = 0; a < children; a++){
                    child.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-child-age-1-"+(a+1)+"'])[3]"));
                    age.click();
                    age.sendKeys(String.valueOf(childAge[a]));
                }
            }

            WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[2]/.."));
            submitSearch.click();
        }
    }

    private void bundle() throws ParseException{
        if (checkDate()) {
            WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='" + type + "']"));
            typeOfPurchase.click();

            WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='fhc-fhc-hp-package']/..")));
            typeOfTrip.click();

            WebElement origin = driver.findElement(By.xpath("//*[@id='package-origin-hp-package']"));
            origin.click();
            origin.sendKeys(orig);
            WebElement originAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + orig + "')]")));
            originAirport.click();

            WebElement destination = driver.findElement(By.xpath("//*[@id='package-destination-hp-package']"));
            destination.click();
            destination.clear();
            destination.sendKeys(dest);
            WebElement destAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + dest + "')]")));
            destAirport.click();

            WebElement departureDate = driver.findElement(By.xpath("//*[@id='package-departing-hp-package']"));
            departureDate.click();
            departureDate.sendKeys(depart);

            WebElement returningDate = driver.findElement(By.xpath("//*[@id='package-returning-hp-package']"));
            returningDate.click();
            returningDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            returningDate.sendKeys(retrn);

            driver.findElement(By.xpath("//*[@id='traveler-selector-hp-package']")).click();
            if (adults > 2) {
                WebElement adult = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[3]"));
                for (int a = 2; a < adults; a++) {
                    adult.click();
                }
            }
            if (children > 0) {
                WebElement child = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[5]"));
                for (int a = 0; a < children; a++) {
                    child.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-child-age-1-" + (a + 1) + "'])[6]"));
                    age.click();
                    age.sendKeys(String.valueOf(childAge[a]));
                }
            }
            if (infants > 0) {
                WebElement infant = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[7]"));
                for (int a = 0; a < infants; a++) {
                    infant.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-infant-age-1-" + (a + 1) + "'])[4]"));
                    age.click();
                    if (infantAge[a] == 0) {
                        age.sendKeys("Under 1");
                    } else {
                        age.sendKeys("1");
                    }
                }
            }
            WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[3]/.."));
            submitSearch.click();
        }
}

    public void flightHotel() throws ParseException{
        if (checkDate()) {
            WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='" + type + "']"));
            typeOfPurchase.click();

            WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='fh-fh-hp-package']/..")));
            typeOfTrip.click();

            WebElement origin = driver.findElement(By.xpath("//*[@id='package-origin-hp-package']"));
            origin.click();
            origin.sendKeys(orig);
            WebElement originAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + orig + "')]")));
            originAirport.click();

            WebElement destination = driver.findElement(By.xpath("//*[@id='package-destination-hp-package']"));
            destination.click();
            destination.clear();
            destination.sendKeys(dest);
            WebElement destAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + dest + "')]")));
            destAirport.click();

            WebElement departureDate = driver.findElement(By.xpath("//*[@id='package-departing-hp-package']"));
            departureDate.click();
            departureDate.sendKeys(depart);

            WebElement returningDate = driver.findElement(By.xpath("//*[@id='package-returning-hp-package']"));
            returningDate.click();
            returningDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            returningDate.sendKeys(retrn);

            driver.findElement(By.xpath("//*[@id='traveler-selector-hp-package']")).click();
            if (adults > 2) {
                WebElement adult = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[3]"));
                for (int a = 2; a < adults; a++) {
                    adult.click();
                }
            }
            if (children > 0) {
                WebElement child = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[5]"));
                for (int a = 0; a < children; a++) {
                    child.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-child-age-1-" + (a + 1) + "'])[6]"));
                    age.click();
                    age.sendKeys(String.valueOf(childAge[a]));
                }
            }
            if (infants > 0) {
                WebElement infant = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[7]"));
                for (int a = 0; a < infants; a++) {
                    infant.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-infant-age-1-" + (a + 1) + "'])[4]"));
                    age.click();
                    if (infantAge[a] == 0) {
                        age.sendKeys("Under 1");
                    } else {
                        age.sendKeys("1");
                    }
                }
            }

            WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[3]/.."));
            submitSearch.click();
        }
    }
    public void flightCar() throws ParseException{
        if (checkDate()) {
            WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='" + type + "']"));
            typeOfPurchase.click();

            WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='fc-fc-hp-package']/..")));
            typeOfTrip.click();

            WebElement origin = driver.findElement(By.xpath("//*[@id='package-origin-FC-hp-package']"));
            origin.click();
            origin.sendKeys(orig);
            WebElement originAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + orig + "')]")));
            originAirport.click();

            WebElement destination = driver.findElement(By.xpath("//*[@id='package-destination-FC-hp-package']"));
            destination.click();
            destination.clear();
            destination.sendKeys(dest);
            WebElement destAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + dest + "')]")));
            destAirport.click();

            WebElement departureDate = driver.findElement(By.xpath("//*[@id='package-fc-departing-hp-package']"));
            departureDate.click();
            departureDate.sendKeys(depart);

            WebElement returningDate = driver.findElement(By.xpath("//*[@id='package-fc-returning-hp-package']"));
            returningDate.click();
            returningDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            returningDate.sendKeys(retrn);

            driver.findElement(By.xpath("//*[@id='traveler-selector-hp-package-fc']")).click();
            if (adults > 1) {
                WebElement adult = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package-fc']//button)[3]"));
                for (int a = 1; a < adults; a++) {
                    adult.click();
                }
            }
            if (children > 0) {
                WebElement child = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package-fc']//button)[5]"));
                for (int a = 0; a < children; a++) {
                    child.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-child-age-1-" + (a + 1) + "'])[4]"));
                    age.click();
                    age.sendKeys(String.valueOf(childAge[a]));
                }
            }
            if (infants > 0) {
                WebElement infant = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package-fc']//button)[7]"));
                for (int a = 0; a < infants; a++) {
                    infant.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-infant-age-1-" + (a + 1) + "'])[3]"));
                    age.click();
                    if (infantAge[a] == 0) {
                        age.sendKeys("Under 1");
                    } else {
                        age.sendKeys("1");
                    }
                }
            }
            WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[3]/.."));
            submitSearch.click();
        }
    }
    public void hotelCar() throws ParseException{
        if (checkDate()) {
            WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='" + type + "']"));
            typeOfPurchase.click();

            WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='hotel-car-package-type-hp-package']/..")));
            typeOfTrip.click();

            WebElement destination = driver.findElement(By.xpath("//*[@id='hotel-destination-hp-package']"));
            destination.click();
            destination.clear();
            destination.sendKeys(dest);
            WebElement destAirport = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + dest + "')]")));
            destAirport.click();

            WebElement departureDate = driver.findElement(By.xpath("//*[@id='package-hc-departing-hp-package']"));
            departureDate.click();
            departureDate.sendKeys(depart);

            WebElement returningDate = driver.findElement(By.xpath("//*[@id='package-hc-returning-hp-package']"));
            returningDate.click();
            returningDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            returningDate.sendKeys(retrn);

            driver.findElement(By.xpath("//*[@id='traveler-selector-hp-package']")).click();
            if (adults > 2) {
                WebElement adult = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[3]"));
                for (int a = 2; a < adults; a++) {
                    adult.click();
                }
            }
            if (children > 0) {
                WebElement child = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[5]"));
                for (int a = 0; a < children; a++) {
                    child.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-child-age-1-" + (a + 1) + "'])[6]"));
                    age.click();
                    age.sendKeys(String.valueOf(childAge[a]));
                }
            }
            if (infants > 0) {
                WebElement infant = driver.findElement(By.xpath("(//*[@id='traveler-selector-hp-package']//button)[7]"));
                for (int a = 0; a < infants; a++) {
                    infant.click();
                    WebElement age = driver.findElement(By.xpath("(//*[@data-gcw-storeable-name='gcw-infant-age-1-" + (a + 1) + "'])[4]"));
                    age.click();
                    if (infantAge[a] == 0) {
                        age.sendKeys("Under 1");
                    } else {
                        age.sendKeys("1");
                    }
                }
            }
            WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[3]/.."));
            submitSearch.click();
        }
    }

    public void carOnly() throws ParseException{
        if (checkDate()) {
            WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='" + type + "']"));
            typeOfPurchase.click();

            WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Rental Cars']/..")));
            typeOfTrip.click();

            WebElement carPickUp = driver.findElement(By.xpath("//*[@id='car-pickup-hp-car']"));
            carPickUp.click();
            carPickUp.clear();
            carPickUp.sendKeys(orig);
            WebElement pickupLocation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + orig + "')]")));
            pickupLocation.click();

            WebElement carDropOff = driver.findElement(By.xpath("//*[@id='car-dropoff-hp-car']"));
            carDropOff.click();
            carDropOff.clear();
            carDropOff.sendKeys(dest);
            WebElement dropoffLocation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-value,'" + dest + "')]")));
            dropoffLocation.click();

            WebElement pickupDate = driver.findElement(By.xpath("//*[@id='car-pickup-date-hp-car']"));
            pickupDate.click();
            pickupDate.sendKeys(depart);

            WebElement pickupTime = driver.findElement(By.xpath("//*[@id='car-pickup-time-hp-car']"));
            pickupTime.click();
            pickupTime.sendKeys(pickTime);

            WebElement returningDate = driver.findElement(By.xpath("//*[@id='car-dropoff-date-hp-car']"));
            returningDate.click();
            returningDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            returningDate.sendKeys(retrn);

            WebElement dropoffTime = driver.findElement(By.xpath("//*[@id='car-dropoff-time-hp-car']"));
            dropoffTime.click();
            dropoffTime.sendKeys(dropTime);

            WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[4]/.."));
            submitSearch.click();
        }
    }

    public void airTrans() throws ParseException {
        WebElement typeOfPurchase = driver.findElement(By.xpath("//*[@data-lob ='" + type + "']"));
        typeOfPurchase.click();

        WebElement typeOfTrip = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Airport Transportation']/..")));
        typeOfTrip.click();

        String pickUp = null;
        String dropOff = null;
        String date = null;
        String time = null;
        String pickDropTime = null;
        String index = null;

        String serviceType;
        if (pickTime == "" || pickTime == null) {
            serviceType = "to";
            driver.findElement(By.xpath("//*[@id='car-gt-toairport-hp-car']/..")).click();
            pickUp = "//*[@id='hotel-origin-hp-car']";
            dropOff = "//*[@id='flight-hp-car']";
            date = "//input[@id='gt-origin-hotel-departure-date-hp-car']";
            time = "//*[@id='gt-hotel-departure-time-hp-car']";
            pickDropTime = "//*[@id='gt-hotel-departure-time-hp-car']/option[text()='" + dropTime + "']";
            index = "2";
        } else if (dropTime == "" || dropTime == null) {
            serviceType = "from";
            driver.findElement(By.xpath("//*[@id='car-gt-fromairport-hp-car']")).click();
            pickUp = "//*[@id='flight-origin-hp-car']";
            dropOff = "//*[@id='hotel-hp-car']";
            date = "//input[@id='gt-origin-flight-arrival-date-hp-car']";
            time = "//*[@id='gt-pickup-time-hp-car']";
            pickDropTime = "//*[@id='gt-pickup-time-hp-car']/option[text()='" + pickTime + "']";
            index = "1";
        } else {
            serviceType = "round";
            driver.findElement(By.xpath("//*[@id='car-gt-fromairport-hp-car']")).click();
            driver.findElement(By.xpath("//*[@id='gt-roundtrip-hp-car']")).click();
            pickUp = "//*[@id='flight-origin-hp-car']";
            dropOff = "//*[@id='hotel-hp-car']";
            date = "//input[@id='gt-origin-flight-arrival-date-hp-car']";
            time = "//*[@id='gt-pickup-time-hp-car']";
            pickDropTime = "//*[@id='gt-pickup-time-hp-car']/option[text()='" + pickTime + "']";
            index = "1";
        }

        WebElement locationPickUp = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pickUp)));
        locationPickUp.click();
        locationPickUp.clear();
        locationPickUp.sendKeys(orig);
        WebElement pickupLocation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@id='typeaheadDataPlain']/li)[1]")));
        pickupLocation.click();

        WebElement locationDropOff = driver.findElement(By.xpath(dropOff));
        locationDropOff.click();
        locationDropOff.clear();
        locationDropOff.sendKeys(dest);
        WebElement dropoffLocation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@id='typeaheadDataPlain']/li)[1]")));
        dropoffLocation.click();

        WebElement pickupDate = driver.findElement(By.xpath(date));
        pickupDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        pickupDate.sendKeys(depart);

        WebElement pickupTime = driver.findElement(By.xpath(time));
        pickupTime.click();
        driver.findElement(By.xpath(pickDropTime)).click();

        if (adults > 1) {
            WebElement adult = driver.findElement(By.xpath("(//*[@id='flight-adults-hp-car'])[" + index + "]"));
            adult.click();
            adult.sendKeys(String.valueOf(adults));
        }
        if (children > 0) {
            WebElement child = driver.findElement(By.xpath("(//*[@id='gt-flight-children-hp-car'])[" + index + "]"));
            child.click();
            child.sendKeys(String.valueOf(children));
        }
        if (infants > 0) {
            WebElement infant = driver.findElement(By.xpath("(//*[@id='gt-flight-infants-hp-car'])[" + index + "]"));
            infant.click();
            infant.sendKeys(String.valueOf(infants));
        }
        if (checkDate() && serviceType == "round") {
            WebElement returnDate = driver.findElement(By.xpath("//*[@id='gt-hotel-departure-date-hp-car']"));
            returnDate.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            returnDate.sendKeys(retrn);
            WebElement roundTripTime = driver.findElement(By.xpath("//*[@id='gt-dropoff-time-hp-car']"));
            roundTripTime.click();
            driver.findElement(By.xpath("//*[@id='gt-dropoff-time-hp-car']/option[text()='"+dropTime+"']")).click();
        }
        WebElement submitSearch = driver.findElement(By.xpath("(//span[text()='Search'])[4]/.."));
        submitSearch.click();
    }

    public boolean checkDate() throws ParseException{
        Date date1 = expediaFormat.parse(depart);
        Date date2 = expediaFormat.parse(retrn);
        if(date2.compareTo(date1) > 0) {
            return true;
        } else {
            System.out.println("Invalid Date, return date must be after departure");
            return false;
        }
    }
}
