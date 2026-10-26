import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

class Product {
    // Encapsulated fields
    private int id, stock;
    private String name;
    private double price;

    public Product(int id, String name, double price, int stock) {
        this.id = id; this.name = name; this.price = price; this.stock = stock;
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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.print("\nMenu: 1 Add | 2 Find | 3 Restock | 4 Sell | 5 List | 6 Sort | 0 Exit\nSelect: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;

            try {
                if (choice.equals("5") || choice.equals("6")) {
                    handleReports(choice);
                    continue;
                }

                // Consolidated input reading (space-separated)
                System.out.print("Enter Input (Add: ID Name Price Stock | Others: ID [Amount]): ");
                String[] inputs = sc.nextLine().trim().split("\\s+");
                int id = Integer.parseInt(inputs[0]);

                switch (choice) {
                    case "1":
                        if (inventory.containsKey(id)) throw new IllegalArgumentException("Rejected");
                        inventory.put(id, new Product(id, inputs[1], Double.parseDouble(inputs[2]), Integer.parseInt(inputs[3])));
                        System.out.println("Added");
                        break;
                    case "2":
                        System.out.println(findProduct(id));
                        break;
                    case "3":
                        Product p1 = findProduct(id);
                        p1.addStock(Integer.parseInt(inputs[1]));
                        System.out.println(p1.getName() + " stock " + p1.getStock());
                        break;
                    case "4":
                        Product p2 = findProduct(id);
                        if (p2.sell(Integer.parseInt(inputs[1]))) System.out.println(p2.getName() + " stock " + p2.getStock());
                        else System.out.println("Rejected; stock remains " + p2.getStock());
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage().equals("Rejected") ? "Rejected; duplicate ID." : e.getMessage());
            } catch (Exception e) {
                System.out.println("Rejected; invalid input.");
            }
        }
    }

    private static Product findProduct(int id) {
        Product p = inventory.get(id);
        if (p == null) throw new IllegalArgumentException("Product not found");
        return p;
    }

    private static void handleReports(String choice) {
        if (inventory.isEmpty()) { 
            System.out.println("Inventory is empty."); 
            return; 
        }
        if (choice.equals("5")) {
            for (Product p : inventory.values()) System.out.println("ID " + p.getId() + ": " + p);
        } else {
            ArrayList<Product> list = new ArrayList<>(inventory.values());
            list.sort(Comparator.comparingDouble(Product::getPrice).thenComparingInt(Product::getId));
            ArrayList<String> names = new ArrayList<>();
            for (Product p : list) names.add(p.getName());
            System.out.println(String.join(", ", names));
        }
    }
}
