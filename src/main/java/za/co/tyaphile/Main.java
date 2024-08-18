package za.co.tyaphile;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static DatabaseManage database;

    public double getDecimalNumber(@NotNull String text) {
        if (text.isEmpty()) return 0;

        String[] details = text.split("\\s+");
        return Double.parseDouble(details[0].replaceAll("[^[0-9]+.[0-9]]", "").trim());
    }

    public String getQuantityOnPrice(@NotNull String text) {
        String[] details = text.split("\\s+");

        if (text.isEmpty() || details.length < 2) return "";
        return details[details.length - 1].trim();
    }

    public String getMeasurementOnItem(@NotNull String text) {
        String[] details = text.split("\\s+");

        List<String> splitter = Arrays.stream(details)
                .map(x -> x.replaceAll("[[0-9]+.[0-9]]", "").trim()).toList();

        if (splitter.isEmpty()) return "";
        return splitter.getLast();
    }

    public Main() {
        browseMonitorShoprite();
    }

    private void browseMonitorShoprite() {
        database = new DatabaseManage();

        String url = "https://www.shoprite.co.za/c-2256/All-Departments";
        AtomicInteger count = new AtomicInteger();


        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.setExperimentalOption("excludeSwitches",
                List.of("disable-popup-blocking"));
//                options.addArguments("--headless");
//                options.addArguments("--window-size=1400,1200");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
//                driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(2));

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> schedule = service.scheduleAtFixedRate(() -> {
            while (true) {

                try {
                    driver.get(url);
                    Document doc = Jsoup.parse(driver.getPageSource());

                    double total = getDecimalNumber(doc.getElementsByClass("total-number-of-results").first().text());
                    total = Math.round(total / 20);

                    browserCatalog((int) total, driver);

                    count.set(0);

                    driver.quit();
                    break;
                } catch (NullPointerException e) {
                    try {
                        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                        FileUtils.copyFile(screenshot, new File("screenshot.png"));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    e.printStackTrace();
                    if (count.incrementAndGet() == 3) {
                        count.set(0);
                        break;
                    }
                }
            }
        }, 0, 1, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            schedule.cancel(true); // Pass true if you want to interrupt the currently executing task
            service.shutdown();
            System.out.println("Shutdown hook triggered.");
            try {
                if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("Forcing shutdown...");
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.out.println("Shutdown interrupted.");
                service.shutdownNow();
                Thread.currentThread().interrupt();
            }
            driver.close();
            driver.quit();
            System.out.println("Scraper shutdown successfully.");
        }));
    }

    private void browserCatalog(int page, WebDriver driver) {
        if (page < 0) {
            return;
        }

        String url = "https://www.shoprite.co.za/c-2256/All-Departments?q=%3Arelevance%3AbrowseAllStoresFacetOff%3AbrowseAllStoresFacetOff&page=" + page;

        driver.navigate().to(url);

        Document doc = Jsoup.parse(driver.getPageSource());
        Elements elements = doc.getElementsByClass("item-product");
        for (Element element : elements) {
            try {
                String productName = element.getElementsByClass("item-product__name").text();
                String price_before = String.valueOf(getDecimalNumber(element.getElementsByClass("before").text()));
                String displayPrice = element.getElementsByClass("now").text();
                String price_now = String.valueOf(getDecimalNumber(displayPrice));
                String bulk = getQuantityOnPrice(displayPrice);

                long barcode = 0;
                double weight = 0;
                String unitOfMeasure = "";
                String link = "https://www.shoprite.co.za" + element.getElementsByClass("item-product__name").select("a").attr("href");

                driver.navigate().to(link);
                doc = Jsoup.parse(driver.getPageSource());
                Elements table = doc.getElementsByClass("pdp__tabs__tab").select("tr");
                for (Element row : table) {
                    Elements tds = row.getElementsByTag("td");
                    if (tds.get(0).text().contains("Main Barcode")) {
                        barcode = Long.parseLong(tds.get(1).text());
                    } else if (tds.get(0).text().contains("Product Weight")) {
                        String w = tds.get(1).text();
                        weight = getDecimalNumber(w);
                        unitOfMeasure = getMeasurementOnItem(w);
                    }
                }

                Elements items = doc.getElementsByClass("pdp__tabs__tab");
                String description = items.select("div").get(0).text();

                String image = "https://www.shoprite.co.za" + doc.getElementsByClass("pdp__image__thumb").first().attr("src");
                database.addProduct(barcode, productName, description, Double.parseDouble(price_before.isEmpty() ? "0" : price_before),
                        Double.parseDouble(price_now), weight, unitOfMeasure, bulk, image, link);
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
        }

        System.out.println("Done: " + url);
        browserCatalog(page - 1, driver);
    }

    public static void main(String[] args) {
        new Main();
    }
}