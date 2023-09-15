package com.prompter.service;

import com.prompter.dto.response.CrawlingResponse;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlingService {

    private final WebDriver driver;

    public CrawlingResponse process(String url) throws JSONException {;

        JSONObject info = new JSONObject();
        // JavascriptExecutor js = (JavascriptExecutor) driver;
        // String url = "https://wannabeguru.tistory.com/entry/%EC%B1%97GPT-%ED%99%9C%EC%9A%A9%ED%95%B4%EC%84%9C-%EC%B7%A8%EC%97%85%ED%95%98%EB%8A%94-%EB%B2%95";

        driver.get(url);

        try {Thread.sleep(500);} catch (InterruptedException e) {}

        // 현재 페이지의 소스코드 가져오기
        Document doc = Jsoup.parse(driver.getPageSource());

        long startTime = System.currentTimeMillis();
        Elements elements = doc.getElementsByClass("contents_style");

        StringBuilder sb = new StringBuilder();
//        doc.getAllElements().forEach(
//             element -> {
//                 if (!ObjectUtils.isEmpty(element.getElementsByTag("p"))) {
//                     for (Element p : element.getElementsByTag("p")) {
//                         if(p.text().isEmpty()) continue;
//                         log.info("p :{}", p.text());
//                         sb.append(p.text());
//                     }
//                 }
//             }
//             );

        for (Element element : elements) {
            for (Element p : element.getElementsByTag("p")) {
                if (p.text().isEmpty()) continue;
//                log.info("p : {}", p.text());
                sb.append(p.text());
            }
        }
        long endTime = System.currentTimeMillis();


        // 결과 출력
        info.put("text", sb);
        System.out.println(info);
        log.info("수행 시간 : {}", (endTime - startTime) / 1000);

        // driver.quit();

        return CrawlingResponse.from(sb.toString());
    }

    public JSONObject process1() throws JSONException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
//        options.addArguments("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36");
        options.addArguments("disable-gpu");
        options.addArguments("--disable-gpu");
        options.addArguments("lang=ko_KR");
        options.addArguments("window-size=1920x1080");

//        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);

//        ChromeDriver driver = new ChromeDriver(options);

        // 정체 정보가 들어갈 Json
        JSONObject info = new JSONObject();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String url = "https://wannabeguru.tistory.com/entry/%EC%B1%97GPT-%ED%99%9C%EC%9A%A9%ED%95%B4%EC%84%9C-%EC%B7%A8%EC%97%85%ED%95%98%EB%8A%94-%EB%B2%95";
//        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36").get();
//        Document doc = Jsoup.connect(url).get();

        driver.get(url);

        try {Thread.sleep(1000);} catch (InterruptedException e) {}

        Document doc = Jsoup.parse(driver.getPageSource());

        long startTime = System.currentTimeMillis();
        Elements elements = doc.getElementsByClass("contents_style");
        StringBuilder sb = new StringBuilder();
        for (Element element : elements) {
            for (Element p : element.getElementsByTag("p")) {
                if (p.text().isEmpty()) continue;
                log.info("p : {}", p.text());
//                info.put("text", p.text());
                sb.append(p.text());
                sb.append("\n");
            }
        }
        long endTime = System.currentTimeMillis();


        // 결과 출력
        info.put("text", sb);
        System.out.println(info);
        log.info("수행 시간 : {}", (endTime - startTime) / 1000);

        driver.quit();

        return info;

//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    public CrawlingResponse process2() throws JSONException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("disable-gpu");
        options.addArguments("--disable-gpu");
        options.addArguments("lang=ko_KR");
        options.addArguments("window-size=1920x1080");

//        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);

//        ChromeDriver driver = new ChromeDriver(options);

        JSONObject info = new JSONObject();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String url = "https://wannabeguru.tistory.com/entry/%EC%B1%97GPT-%ED%99%9C%EC%9A%A9%ED%95%B4%EC%84%9C-%EC%B7%A8%EC%97%85%ED%95%98%EB%8A%94-%EB%B2%95";
//        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36").get();
//        Document doc = Jsoup.connect(url).get();

        driver.get(url);

        try {Thread.sleep(500);} catch (InterruptedException e) {}

        Document doc = Jsoup.parse(driver.getPageSource());

        long startTime = System.currentTimeMillis();
        Elements elements = doc.getElementsByClass("contents_style");
        StringBuilder sb = new StringBuilder();
        for (Element element : elements) {
            for (Element p : element.getElementsByTag("p")) {
                if (p.text().isEmpty()) continue;
                log.info("p : {}", p.text());
                sb.append(p.text());
            }
        }
        long endTime = System.currentTimeMillis();


        // 결과 출력
        info.put("text", sb);
        System.out.println(info);
        log.info("수행 시간 : {}", (endTime - startTime) / 1000);

        driver.quit();

        return CrawlingResponse.from(sb.toString());
    }

    public CrawlingResponse processNaver() throws JSONException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("disable-gpu");
        options.addArguments("--disable-gpu");
        options.addArguments("lang=ko_KR");
        options.addArguments("window-size=1920x1080");

//        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);

//        ChromeDriver driver = new ChromeDriver(options);

        JSONObject info = new JSONObject();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String url = "https://blog.naver.com/samonimdaek/223059674471";


        driver.get(url);

        try {Thread.sleep(1000);} catch (InterruptedException e) {}

        Document doc = Jsoup.parse(driver.getPageSource());

        long startTime = System.currentTimeMillis();
        Elements elements = doc.getElementsByClass("se-module se-module-text");
        StringBuilder sb = new StringBuilder();
        for (Element element : elements) {
            Elements subElements = element.getElementsByClass("se-text-paragraph se-text-paragraph-align-center ");
            for (Element subElement : subElements) {
                Elements elementsByClass = subElement.getElementsByClass("se-fs-fs16 se-ff-system   ");
                for (Element byClass : elementsByClass) {
                    if (byClass.text().isEmpty()) continue;
                        log.info("span : {}", byClass.text());
                        sb.append(byClass.text());
                }
            }
        }
        long endTime = System.currentTimeMillis();


        // 결과 출력
        info.put("text", sb);
        System.out.println(info);
        log.info("수행 시간 : {}", (endTime - startTime) / 1000);

        driver.quit();

        return CrawlingResponse.from(sb.toString());
    }

    public CrawlingResponse processGoogle() throws JSONException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("disable-gpu");
        options.addArguments("--disable-gpu");
        options.addArguments("lang=ko_KR");
        options.addArguments("window-size=1920x1080");

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);

        JSONObject info = new JSONObject();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String url = "https://aimee.kr/magazine/5";


        driver.get(url);

        try {Thread.sleep(1000);} catch (InterruptedException e) {}

        Document doc = Jsoup.parse(driver.getPageSource());

        long startTime = System.currentTimeMillis();
        Elements elements = doc.getElementsByClass("MagazineContentDetail__MainContents-sc-fg72qx-3 kOZBDH");
        StringBuilder sb = new StringBuilder();
        for (Element element : elements) {
            Elements subElements = element.getElementsByTag("p");
            for (Element subElement : subElements) {
                Elements elementsByClass = subElement.getElementsByTag("span");
                for (Element byClass : elementsByClass) {
                    if (byClass.text().isEmpty()) continue;
                    log.info("span : {}", byClass.text());
                    sb.append(byClass.text());
                }
            }
        }
        long endTime = System.currentTimeMillis();


        // 결과 출력
        info.put("text", sb);
        System.out.println(info);
        log.info("수행 시간 : {}", (endTime - startTime) / 1000);

        driver.quit();

        return CrawlingResponse.from(sb.toString());
    }

}
