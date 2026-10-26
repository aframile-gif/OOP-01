import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void addStock(int amount) {
        this.stock += amount;
    }

    public boolean sell(int amount) {
        if (this.stock >= amount) {
            this.stock -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name + ", " + String.format("%.2f", price) + ", stock " + stock;
    }
}

public class MP4 {
    private static HashMap<Integer, Product> inventory = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nMenu: 1 Add | 2 Find | 3 Restock | 4 Sell | 5 List | 6 Sort by price | 0 Exit");
            System.out.print("Select an option: ");
            
            int choice;
            try {
                // Fix: Using parseInt(nextLine()) prevents the Scanner newline bug
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (choice == 0) {
                System.out.println("Exiting...");
                break;
            }

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Enter ID: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        
                        if (inventory.containsKey(id)) {
                            System.out.println("Rejected"); // Matches exact test case output
                            break;
                        }

                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine().trim();

                        System.out.print("Enter Price: ");
                        double price = Double.parseDouble(scanner.nextLine().trim());

                        System.out.print("Enter Stock: ");
                        int stock = Integer.parseInt(scanner.nextLine().trim());

                        inventory.put(id, new Product(id, name, price, stock));
                        System.out.println("Added"); // Will successfully print now
                    } catch (NumberFormatException e) {
                        System.out.println("Rejected; invalid number format.");
                    }
                    break;

                case 2:
                    System.out.print("Enter ID to find: ");
                    int searchId = Integer.parseInt(scanner.nextLine().trim());
                    if (inventory.containsKey(searchId)) {
                        System.out.println(inventory.get(searchId));
                    } else {
                        System.out.println("Product not found");
                    }
                    break;

                case 3:
                    System.out.print("Enter ID to restock: ");
                    int restockId = Integer.parseInt(scanner.nextLine().trim());
                    if (inventory.containsKey(restockId)) {
                        System.out.print("Enter quantity to add: ");
                        int amount = Integer.parseInt(scanner.nextLine().trim());
                        inventory.get(restockId).addStock(amount);
                        System.out.println(inventory.get(restockId).getName() + " stock " + inventory.get(restockId).getStock());
                    } else {
                        System.out.println("Product not found");
                    }
                    break;

                case 4:
                    System.out.print("Enter ID to sell: ");
                    int sellId = Integer.parseInt(scanner.nextLine().trim());
                    if (inventory.containsKey(sellId)) {
                        System.out.print("Enter quantity to sell: ");
                        int sellQty = Integer.parseInt(scanner.nextLine().trim());
                        Product p = inventory.get(sellId);
                        if (p.sell(sellQty)) {
                            System.out.println(p.getName() + " stock " + p.getStock());
                        } else {
                            System.out.println("Rejected; stock remains " + p.getStock());
                        }
                    } else {
                        System.out.println("Product not found");
                    }
                    break;

                case 5:
                    if (inventory.isEmpty()) {
                        System.out.println("Inventory is empty.");
                    } else {
                        for (Product p : inventory.values()) {
                            System.out.println("ID " + p.getId() + ": " + p.toString());
                        }
                    }
                    break;

                case 6:
                    if (inventory.isEmpty()) {
                        System.out.println("Inventory is empty.");
                    } else {
                        ArrayList<Product> snapshot = new ArrayList<>(inventory.values());
                        snapshot.sort(Comparator.comparingDouble(Product::getPrice).thenComparingInt(Product::getId));
                        
                        ArrayList<String> names = new ArrayList<>();
                        for (Product p : snapshot) {
                            names.add(p.getName());
                        }
                        System.out.println(String.join(", ", names));
                    }
                    break;

                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }
}
