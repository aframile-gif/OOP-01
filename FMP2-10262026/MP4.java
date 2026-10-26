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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void addStock(int amount) {
        stock += amount;
    }

    public boolean sell(int amount) {
        if (amount > 0 && stock >= amount) {
            stock -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name + ", " +
               String.format("%.2f", price) +
               ", stock " + stock;
    }
}

public class MP4 {

    private static HashMap<Integer, Product> inventory =
            new HashMap<>();

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println(
                "\nMenu: 1 Add | 2 Find | 3 Restock | 4 Sell | " +
                "5 List | 6 Sort by price | 0 Exit"
            );
            System.out.print("Select an option: ");

            try {
                int choice = Integer.parseInt(
                    scanner.nextLine().trim()
                );

                if (choice == 0) {
                    System.out.println("Exiting...");
                    break;
                }

                switch (choice) {

                    case 1:
                        addProduct();
                        break;

                    case 2:
                        findProduct();
                        break;

                    case 3:
                        restockProduct();
                        break;

                    case 4:
                        sellProduct();
                        break;

                    case 5:
                        listProducts();
                        break;

                    case 6:
                        sortProducts();
                        break;

                    default:
                        System.out.println("Invalid menu option.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Rejected; invalid number format.");
            }
        }

        scanner.close();
    }

    // OPTION 1: ADD PRODUCT
    public static void addProduct() {

        System.out.print("Enter ID Name Price Stock: ");
        String input = scanner.nextLine().trim();

        String[] data = input.split("\\s+");

        if (data.length != 4) {
            System.out.println("Rejected; invalid input.");
            return;
        }

        try {
            int id = Integer.parseInt(data[0]);
            String name = data[1];
            double price = Double.parseDouble(data[2]);
            int stock = Integer.parseInt(data[3]);

            if (inventory.containsKey(id)) {
                System.out.println("Rejected");
                return;
            }

            if (!Double.isFinite(price) ||
                price < 0 || stock < 0) {
                System.out.println("Rejected; invalid values.");
                return;
            }

            inventory.put(
                id,
                new Product(id, name, price, stock)
            );

            System.out.println("Added");

        } catch (NumberFormatException e) {
            System.out.println("Rejected; invalid number format.");
        }
    }

    // OPTION 2: FIND PRODUCT
    public static void findProduct() {

        System.out.print("Enter ID to find: ");
        int id = Integer.parseInt(
            scanner.nextLine().trim()
        );

        Product product = inventory.get(id);

        if (product != null) {
            System.out.println(product);
        } else {
            System.out.println("Product not found");
        }
    }

    // OPTION 3: RESTOCK PRODUCT
    public static void restockProduct() {

        System.out.print("Enter ID to restock: ");
        int id = Integer.parseInt(
            scanner.nextLine().trim()
        );

        Product product = inventory.get(id);

        if (product == null) {
            System.out.println("Product not found");
            return;
        }

        System.out.print("Enter quantity to add: ");
        int amount = Integer.parseInt(
            scanner.nextLine().trim()
        );

        if (amount <= 0 ||
            amount > Integer.MAX_VALUE - product.getStock()) {
            System.out.println("Rejected; invalid quantity.");
            return;
        }

        product.addStock(amount);

        System.out.println(
            product.getName() +
            " stock " + product.getStock()
        );
    }

    // OPTION 4: SELL PRODUCT
    public static void sellProduct() {

        System.out.print("Enter ID to sell: ");
        int id = Integer.parseInt(
            scanner.nextLine().trim()
        );

        Product product = inventory.get(id);

        if (product == null) {
            System.out.println("Product not found");
            return;
        }

        System.out.print("Enter quantity to sell: ");
        int amount = Integer.parseInt(
            scanner.nextLine().trim()
        );

        if (product.sell(amount)) {
            System.out.println(
                product.getName() +
                " stock " + product.getStock()
            );
        } else {
            System.out.println(
                "Rejected; stock remains " +
                product.getStock()
            );
        }
    }

    // OPTION 5: LIST PRODUCTS
    public static void listProducts() {

        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        ArrayList<Product> products =
            new ArrayList<>(inventory.values());

        products.sort(Comparator.comparingInt(Product::getId));

        for (Product product : products) {
            System.out.println(
                "ID " + product.getId() +
                ": " + product
            );
        }
    }

    // OPTION 6: SORT BY PRICE
    public static void sortProducts() {

        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        ArrayList<Product> products =
            new ArrayList<>(inventory.values());

        products.sort(
            Comparator.comparingDouble(Product::getPrice)
                      .thenComparingInt(Product::getId)
        );

        ArrayList<String> names = new ArrayList<>();

        for (Product product : products) {
            names.add(product.getName());
        }

        System.out.println(String.join(", ", names));
    }
}
