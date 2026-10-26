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
            
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter ID Name Price Stock (e.g., 1 Pen 10.00 5): ");
                        String[] addParts = scanner.nextLine().trim().split("\\s+");
                        int id = Integer.parseInt(addParts[0]);
                        
                        if (inventory.containsKey(id)) {
                            System.out.println("Rejected"); 
                            break;
                        }

                        String name = addParts[1];
                        double price = Double.parseDouble(addParts[2]);
                        int stock = Integer.parseInt(addParts[3]);

                        inventory.put(id, new Product(id, name, price, stock));
                        System.out.println("Added");
                        break;

                    case "2":
                        System.out.print("Enter ID to find: ");
                        int searchId = Integer.parseInt(scanner.nextLine().trim());
                        if (inventory.containsKey(searchId)) {
                            System.out.println(inventory.get(searchId));
                        } else {
                            System.out.println("Product not found");
                        }
                        break;

                    case "3":
                        System.out.print("Enter ID and quantity to restock (e.g., 3 4): ");
                        String[] restockParts = scanner.nextLine().trim().split("\\s+");
                        int restockId = Integer.parseInt(restockParts[0]);
                        int addQty = Integer.parseInt(restockParts[1]);
                        
                        if (inventory.containsKey(restockId)) {
                            inventory.get(restockId).addStock(addQty);
                            System.out.println(inventory.get(restockId).getName() + " stock " + inventory.get(restockId).getStock());
                        } else {
                            System.out.println("Product not found");
                        }
                        break;

                    case "4":
                        System.out.print("Enter ID and quantity to sell (e.g., 1 3): ");
                        String[] sellParts = scanner.nextLine().trim().split("\\s+");
                        int sellId = Integer.parseInt(sellParts[0]);
                        int sellQty = Integer.parseInt(sellParts[1]);
                        
                        if (inventory.containsKey(sellId)) {
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

                    case "5":
                        if (inventory.isEmpty()) {
                            System.out.println("Inventory is empty.");
                        } else {
                            for (Product p : inventory.values()) {
                                System.out.println("ID " + p.getId() + ": " + p.toString());
                            }
                        }
                        break;

                    case "6":
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
            } catch (Exception e) {
                // Instantly rejects any malformed string inputs without crashing the program
                System.out.println("Rejected");
            }
        }
    }
}
