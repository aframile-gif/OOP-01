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
        int choice = -1;
        while (choice != 0) {
            System.out.println("\nMenu: 1 Add | 2 Find | 3 Restock | 4 Sell | 5 List | 6 Sort by price | 0 Exit");
            System.out.print("Select an option: ");
            choice = readInt();

            switch (choice) {
                case 1: addProduct(); break;
                case 2: findProduct(); break;
                case 3: restockProduct(); break;
                case 4: sellProduct(); break;
                case 5: listProducts(); break;
                case 6: sortProducts(); break;
                case 0: System.out.println("Exiting..."); break;
                default: System.out.println("Invalid menu option.");
            }
        }
    }

    private static void addProduct() {
        System.out.print("Enter ID: ");
        int id = readInt();
        if (id == -1) return;
        if (inventory.containsKey(id)) {
            System.out.println("Rejected; duplicate ID.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Rejected; name cannot be empty.");
            return;
        }

        System.out.print("Enter Price: ");
        double price = readDouble();
        if (price < 0) {
            System.out.println("Rejected; price must be positive.");
            return;
        }

        System.out.print("Enter Stock: ");
        int stock = readInt();
        if (stock < 0) {
            System.out.println("Rejected; stock must be positive.");
            return;
        }

        inventory.put(id, new Product(id, name, price, stock));
        System.out.println("Added");
    }

    private static void findProduct() {
        System.out.print("Enter ID to find: ");
        int id = readInt();
        Product p = inventory.get(id);
        if (p != null) {
            System.out.println(p.toString());
        } else {
            System.out.println("Product not found");
        }
    }

    private static void restockProduct() {
        System.out.print("Enter ID to restock: ");
        int id = readInt();
        Product p = inventory.get(id);
        if (p != null) {
            System.out.print("Enter quantity to add: ");
            int qty = readInt();
            if (qty > 0) {
                p.addStock(qty);
                System.out.println(p.getName() + " stock " + p.getStock());
            } else {
                System.out.println("Rejected; invalid quantity.");
            }
        } else {
            System.out.println("Product not found");
        }
    }

    private static void sellProduct() {
        System.out.print("Enter ID to sell: ");
        int id = readInt();
        Product p = inventory.get(id);
        if (p != null) {
            System.out.print("Enter quantity to sell: ");
            int qty = readInt();
            if (qty > 0) {
                if (p.sell(qty)) {
                    System.out.println(p.getName() + " stock " + p.getStock());
                } else {
                    System.out.println("Rejected; stock remains " + p.getStock());
                }
            } else {
                System.out.println("Rejected; invalid quantity.");
            }
        } else {
            System.out.println("Product not found");
        }
    }

    private static void listProducts() {
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        for (Product p : inventory.values()) {
            System.out.println("ID " + p.getId() + ": " + p.toString());
        }
    }

    private static void sortProducts() {
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        ArrayList<Product> snapshot = new ArrayList<>(inventory.values());
        snapshot.sort(Comparator.comparingDouble(Product::getPrice).thenComparingInt(Product::getId));
        
        ArrayList<String> names = new ArrayList<>();
        for (Product p : snapshot) {
            names.add(p.getName());
        }
        System.out.println(String.join(", ", names));
    }

    private static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }
}
