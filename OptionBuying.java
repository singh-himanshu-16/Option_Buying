import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays;

public class OptionBuying {
    static int profitPercentage = 0;
    static int stoplossPercentage = 0;

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the profit percentage");
        profitPercentage = sc.nextInt();
        System.out.println("Enter the stoploss percentage");
        stoplossPercentage = sc.nextInt();
        sc.close();

        OptionBuying obj1 = new OptionBuying();

        // Reading all the CSV files from the Folder
        String path = "C:\\Users\\HIMANSHU-PC\\Downloads\\2021_200\\historical_data_2021_200";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        int numberOfFiles = listOfFiles.length;

        // Default file name
        String pre = "C:\\Users\\HIMANSHU-PC\\Downloads\\2021_200\\historical_data_2021_200\\";

        // Iterating both files one by one
        int counter = 0;
        while (numberOfFiles != 0) {
            String file1 = listOfFiles[counter++].getName();
            String file2 = listOfFiles[counter++].getName();
            String file1_send = pre + file1;
            String file2_send = pre + file2;

            String displayName = file1.substring(7,17);
            System.out.println("------------For " + displayName + " ------------");

            obj1.readCsvFile(file1_send, file2_send);
            numberOfFiles = numberOfFiles - 2;
        }
    }

    public void readCsvFile(String file1, String file2) throws Exception {

        // For CE Data
        List<List<String>> data_CE = new ArrayList<>();// list of lists to store data
        FileReader fr_CE = new FileReader(file2);
        BufferedReader br_CE = new BufferedReader(fr_CE);

        // Reading until we run out of lines
        String line_CE = br_CE.readLine();
        while (line_CE != null) {
            List<String> lineData_CE = Arrays.asList(line_CE.split(","));// splitting lines
            data_CE.add(lineData_CE);
            line_CE = br_CE.readLine();
        }
        br_CE.close();

        // For PE Data
        List<List<String>> data_PE = new ArrayList<>();// list of lists to store data
        FileReader fr_PE = new FileReader(file1);// Changed file2 to file1 because in folder first file is PE file.
        BufferedReader br_PE = new BufferedReader(fr_PE);

        // Reading until we run out of lines
        String line_PE = br_PE.readLine();
        while (line_PE != null) {
            List<String> lineData_PE = Arrays.asList(line_PE.split(","));// splitting lines
            data_PE.add(lineData_PE);
            line_PE = br_PE.readLine();
        }
        br_PE.close();

        // Calculating Entry Price
        List<String> list1 = data_CE.get(1);
        List<String> list2 = data_PE.get(1);

        double callEntryPrice = Double.parseDouble(list1.get(10));
        double putEntryPrice = Double.parseDouble(list2.get(10));
        double entryPrice = callEntryPrice + putEntryPrice;

        String positionTime = list1.get(0);
        System.out.println("Position taken on [" + positionTime + "] at entryprice = " + entryPrice);
        System.out.println();

        strategyImplementation(entryPrice, data_CE, data_PE);

    }

    public static void strategyImplementation(double entryPrice, List<List<String>> data_CE,
            List<List<String>> data_PE) {

        double profitPrice = (entryPrice * profitPercentage) / 100;
        System.out.println("Estimated Profit --> " + profitPrice);

        double targetPrice = entryPrice + profitPrice;
        System.out.println("Target price --> " + targetPrice);

        double lossPrice = (entryPrice * stoplossPercentage) / 100;
        System.out.println("Bearable loss --> " + lossPrice);

        double stoplossPrice = entryPrice - lossPrice;
        System.out.println("Stoploss price --> " + stoplossPrice);
        System.out.println();

        int length_CE = data_CE.size();
        int length_PE = data_PE.size();

        int loop_size = length_CE < length_PE ? length_CE : length_PE;

        for (int i = 2; i < loop_size; i++) {
            List<String> list1 = data_CE.get(i);
            List<String> list2 = data_PE.get(i);

            double callCurrentPrice = Double.parseDouble(list1.get(8));
            double putCurrentPrice = Double.parseDouble(list2.get(8));

            double currentPrice = callCurrentPrice + putCurrentPrice;

            if (currentPrice >= targetPrice) {
                String time = list1.get(0);
                System.out.println("Target Achieved on [" + time + "] at currentPrice = " + currentPrice);
                System.out.println();
                break;
            }
            if (currentPrice <= stoplossPrice) {
                String time = list1.get(0);
                System.out.println("Stoploss Hit on [" + time + "] at currentPrice = " + currentPrice);
                System.out.println();
                break;
            }
        }

    }
}