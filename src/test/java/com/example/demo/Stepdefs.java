package com.example.demo;

//import com.example.rentit.sales.rest.dtos.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class Stepdefs {
    WebDriver customer;
    RestTemplate restTemplate = new RestTemplate();

    static {
        System.setProperty("webdriver.chrome.driver", "C:/Users/ankit/node_modules/chromedriver/lib/chromedriver/chromedriver.exe");
    }

    @Before
    public void setup() {
        customer = new ChromeDriver();
    }

    @After
    public void tearoff() {
        // Comment the following line if you want to check the application's final state on the browser
        customer.close();
    }

    @Given("^the following plant catalog$")
    public void the_following_plant_catalog(DataTable table) throws Exception {
        List<PlantInventoryEntry> entries = table.asMaps(String.class, String.class)
                .stream()
                .map(row -> PlantInventoryEntry.of(Long.parseLong(row.get("id")), row.get("name"), row.get("description"), new BigDecimal(row.get("price"))))
                .collect(Collectors.toList());

        PlantInventoryEntry[] result = restTemplate.postForObject("http://localhost:8090/api/entries", entries, PlantInventoryEntry[].class);
    }

    @Given("^the following inventory$")
    public void the_following_inventory(DataTable items) throws Exception {
    }

    @Given("^a customer is in the \"([^\"]*)\" web page$")
    public void a_customer_is_in_the_web_page(String arg1) throws Exception {
        customer.get("http://localhost:8080/#/");
    }

    @Given("^no purchase order exists in the system$")
    public void no_purchase_order_exists_in_the_system() throws Exception {
    }

    @When("^the customer queries the plant catalog for an \"([^\"]*)\" available from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void the_customer_queries_the_plant_catalog_for_an_available_from_to(String plantName, String startDate, String endDate) throws Exception {
        customer.findElement(By.id("name")).sendKeys(plantName);
        customer.findElement(By.id("start-date")).sendKeys(startDate);
        customer.findElement(By.id("end-date")).sendKeys(endDate);
        customer.findElement(By.id("submit-query")).click();
    }

    @Then("^(\\d+) plants are shown$")
    public void plants_are_shown(int numberOfPlants) throws Exception {
        Thread.sleep(3000);
        List<?> rows = customer.findElements(By.className("table-row"));
        assertThat(rows).hasSize(numberOfPlants);
    }

    @When("^the customer selects a \"([^\"]*)\"$")
    public void the_customer_selects_a(String plantDescription) throws Exception {
        WebElement row = customer.findElement(By.xpath(String.format("//tr/td[contains(text(), '%s')]", plantDescription)));
        WebElement selectPlantButton = row.findElement(By.xpath("//a[contains(text(), 'Select plant')]"));
        selectPlantButton.click();
    }

    @Then("^a purchase order should be created with a total price of (\\d+\\.\\d+)$")
    public void a_purchase_order_should_be_created_with_a_total_price_of(double price) throws Exception {

        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}