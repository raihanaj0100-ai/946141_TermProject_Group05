import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StockandProfit {
    // กำหนดตัวแปรและอาเรย์แบบ Global (ใช้ร่วมกันทุกเมธอด)
    static final int MAX = 100;
    static String[] productName = new String[MAX];
    static double[] costPrice = new double[MAX];
    static double[] salePrice = new double[MAX];
    static int[] stockQty = new int[MAX];
    static int[] soldQty = new int[MAX];
    static int totalProduct = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\n========================================");
            System.out.println("   STOCK & PROFIT MANAGEMENT SYSTEM     ");
            System.out.println("========================================");
            System.out.println("1. Add Product");
            System.out.println("2. Record Sales (and Save to File)");
            System.out.println("3. View Stock Status");
            System.out.println("4. Calculate Profit & Summary");
            System.out.println("5. Show Sales Summary from File & Exit");
            System.out.println("----------------------------------------");
            System.out.print("Please select menu (1-5): ");
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = 0; 
            }

            switch (choice) {
                case 1:
                    addProduct(scanner);
                    break;
                case 2:
                    recordSales(scanner);
                    break;
                case 3:
                    viewStockStatus();
                    break;
                case 4:
                    calculateProfit();
                    break;
                case 5:
                    showSalesSummary();
                    System.out.println("\nExiting program. Thank you for using our system!");
                    break;
                default:
                    System.out.println(">> Invalid menu! Please select between 1-5. <<");
            }

        } while (choice != 5);

        scanner.close();
    }

    // 1. เมธอดเพิ่มสินค้า
    public static void addProduct(Scanner scanner) {
        System.out.println("\n--- [1] Add New Product ---");
        if (totalProduct >= MAX) {
            System.out.println(">> Error: Product list is full! <<");
            return;
        }

        System.out.print("Enter product name: ");
        productName[totalProduct] = scanner.nextLine();

        System.out.print("Enter cost price per unit: ");
        costPrice[totalProduct] = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter sale price per unit: ");
        salePrice[totalProduct] = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter initial stock quantity: ");
        stockQty[totalProduct] = Integer.parseInt(scanner.nextLine());

        soldQty[totalProduct] = 0;
        totalProduct++;
        System.out.println(">> Product added successfully! <<");
    }

    // 2. เมธอดบันทึกการขายและสั่งเขียนไฟล์
    public static void recordSales(Scanner scanner) {
        System.out.println("\n--- [2] Record Sales ---");
        if (totalProduct == 0) {
            System.out.println(">> No products in system! <<");
            return;
        }

        System.out.println("Product List:");
        for (int i = 0; i < totalProduct; i++) {
            int currentStock = stockQty[i] - soldQty[i];
            System.out.println((i + 1) + ". " + productName[i] + " (Remaining Stock: " + currentStock + ")");
        }

        System.out.print("Select product number to sell: ");
        int productIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (productIndex >= 0 && productIndex < totalProduct) {
            System.out.print("Enter quantity sold: ");
            int amountSold = Integer.parseInt(scanner.nextLine());

            int currentStock = stockQty[productIndex] - soldQty[productIndex];
            if (amountSold <= currentStock && amountSold > 0) {
                soldQty[productIndex] += amountSold;
                
                // คำนวณยอดเงินรวมของการขายครั้งนี้เพื่อเซฟลงไฟล์
                double totalSaleAmount = amountSold * salePrice[productIndex];
                saveSale(totalSaleAmount); // เรียกใช้งาน File I/O (Write)

                System.out.println(">> Sales recorded and saved to file successfully! <<");
            } else {
                System.out.println(">> Error: Not enough stock or invalid amount! <<");
            }
        } else {
            System.out.println(">> Error: Invalid product number! <<");
        }
    }

    // 3. เมธอด File I/O เขียนข้อมูลลง sales.txt (Append Mode)
    public static void saveSale(double amount) {
        try {
            FileWriter writer = new FileWriter("sales.txt", true);
            writer.write(amount + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving to file.");
            e.printStackTrace();
        }
    }

    // 4. เมธอดดูสถานะสต็อก
    public static void viewStockStatus() {
        System.out.println("\n--- [3] Stock Status ---");
        if (totalProduct == 0) {
            System.out.println(">> No products in system! <<");
        } else {
            System.out.println(String.format("%-15s | %-10s | %-10s | %-10s", "Product", "Initial", "Sold", "Remaining"));
            System.out.println("--------------------------------------------------");
            for (int i = 0; i < totalProduct; i++) {
                int remainingStock = stockQty[i] - soldQty[i];
                System.out.println(String.format("%-15s | %-10d | %-10d | %-10d", 
                    productName[i], stockQty[i], soldQty[i], remainingStock));
            }
        }
    }

    // 5. เมธอดคำนวณกำไร
    public static void calculateProfit() {
        System.out.println("\n--- [4] Profit & Cost Summary ---");
        if (totalProduct == 0) {
            System.out.println(">> No products in system! <<");
        } else {
            double grandTotalProfit = 0;

            System.out.println(String.format("%-15s | %-10s | %-10s | %-12s", "Product", "Profit/Unit", "Qty Sold", "Total Profit"));
            System.out.println("--------------------------------------------------------");
            for (int i = 0; i < totalProduct; i++) {
                double profitPerUnit = salePrice[i] - costPrice[i];
                double totalProfit = profitPerUnit * soldQty[i];
                grandTotalProfit += totalProfit;

                System.out.println(String.format("%-15s | %-10.2f | %-10d | %-12.2f", 
                    productName[i], profitPerUnit, soldQty[i], totalProfit));
            }
            System.out.println("--------------------------------------------------------");
            System.out.println(">>> Grand Total Profit: " + grandTotalProfit + " THB <<<");
        }
    }

    // 6. เมธอด File I/O อ่านข้อมูลสรุปยอดขายจาก sales.txt (รวมอยู่ในเมนูที่ 5)
    public static void showSalesSummary() {
        System.out.println("\n--- [5] Sales Summary from File ---");
        File file = new File("sales.txt");
        if (!file.exists()) {
            System.out.println(">> No sales record file found yet. <<");
            return;
        }

        try {
            Scanner fileScanner = new Scanner(file);
            int count = 0;
            double totalSales = 0;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (!line.trim().isEmpty()) {
                    totalSales += Double.parseDouble(line);
                    count++;
                }
            }
            fileScanner.close();

            System.out.println("Total Transactions = " + count);
            System.out.println("Total Sales from File = " + totalSales + " THB");

        } catch (Exception e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }
}
