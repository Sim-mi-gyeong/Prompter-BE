package com.example.demo.service;


import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlingExample {
    private WebDriver driver;

    private static final String url = "https://wannabeguru.tistory.com/entry/%EC%B1%97GPT-%ED%99%9C%EC%9A%A9%ED%95%B4%EC%84%9C-%EC%B7%A8%EC%97%85%ED%95%98%EB%8A%94-%EB%B2%95";

    public JSONObject process() {
        System.setProperty("webdriver.chrome.driver", "/Users/simmigyeong/Downloads/demo/chromedriver");
        //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)

        driver = new ChromeDriver();
        //브라우저 선택

        try {
            getDataList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.close();	//탭 닫기
        driver.quit();	//브라우저 닫기
        return null;
    }


    /**
     * data 가져오기
     */
    private List<String> getDataList() throws InterruptedException {
        List<String> list = new ArrayList<>();

        driver.get(url);
        Thread.sleep(1000);

        List<WebElement> elements = driver.findElements(By.cssSelector("#sentence-example-list .sentence-list li"));
        for (WebElement element : elements) {
            System.out.println("----------------------------");
            System.out.println(element);	//⭐
        }

        return list;
    }

}