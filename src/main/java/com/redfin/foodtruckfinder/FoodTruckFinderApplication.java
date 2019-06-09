package com.redfin.foodtruckfinder;

import com.redfin.foodtruckfinder.domain.FoodTruck;
import com.redfin.foodtruckfinder.service.FoodTruckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

@SpringBootApplication
public class FoodTruckFinderApplication implements CommandLineRunner {

    @Resource(name = "FoodTruckService")
    protected FoodTruckService foodTruckService;

   private List<FoodTruck> foodTrucks = new ArrayList<>();

    private static Logger LOG = LoggerFactory
            .getLogger(FoodTruckFinderApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        new SpringApplicationBuilder(FoodTruckFinderApplication.class)
                .logStartupInfo(false)
                .run(args);
//        SpringApplication.run(FoodTruckFinderApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }


    @Override
    public void run(String... args) {
        foodTrucks = foodTruckService.getCurrentlyAvailableFoodTrucks();
        Double listSize = Double.valueOf((foodTrucks.size()));
        int pages = new Double(Math.ceil(listSize / 10)).intValue();
        if(pages > 1) {
            int currentPageIndex = 0;
            int maxPageIndex = pages - 1;
            int maxItems = (listSize.intValue() - 1);
            printPage(currentPageIndex,pages,maxItems);
            promptUser(currentPageIndex,maxPageIndex,maxItems);
        } else {
            foodTrucks.forEach(FoodTruck::print);
        }
    }


    private void promptUser(int currentPageIndex, int maxPageIndex,int maxItems) {
        System.out.println("===============================");
        System.out.println("Enter 1 for Page Down");
        System.out.println("Enter 2 for Page Up");
        System.out.println("Enter 0 to exit");
        System.out.println("===============================");
        Scanner userInputScanner = new Scanner(System.in);
        String userSelection = userInputScanner.next();
        boolean isInValid = false;
        switch (userSelection) {
            case "1":
                if(currentPageIndex > 0) {
                    currentPageIndex--;
                }
                break;
            case "2":
                if(currentPageIndex < maxPageIndex) {
                    currentPageIndex++;
                }
                break;
            case "0":
                return;
            default :
                isInValid = true;
        }
        if(isInValid) {
            System.out.println("Invalid input:\""+userSelection+"\" Please try again");
            promptUser(currentPageIndex,maxPageIndex,maxItems);
        } else {
            printPage(currentPageIndex, (maxPageIndex + 1),maxItems);
            promptUser(currentPageIndex,maxPageIndex,maxItems);
        }
    }


    private void printPage(int pageIndex, int maxPages,int maxItems) {
        int currentPage = pageIndex + 1;
        System.out.println("===============================");
        System.out.println("Showing Page "+currentPage+" of "+maxPages);
        System.out.println("===============================");
        int pageStartIndex = pageIndex * 10;
        for(int i=0; i < 10;i++) {
            int potentialIndex = pageStartIndex + i;
            if(potentialIndex <= maxItems) {
                FoodTruck foodTruck = foodTrucks.get(potentialIndex);
                System.out.print((potentialIndex+1)+") ");
                foodTruck.print();
            }
            else {
                break;
            }
        }
    }
}
