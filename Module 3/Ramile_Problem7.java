import java.util.Scanner;

public class Ramile_Problem7 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first integer (a): ");
        int a = scanner.nextInt();

        System.out.print("Enter second integer (b): ");
        int b = scanner.nextInt();

        System.out.print("Enter third integer (c): ");
        int c = scanner.nextInt();

        // Nested ternary operator
        int largest = (a >= b) ? ((a >= c) ? a : c) : ((b >= c) ? b : c);

        System.out.println("Largest number: " + largest);

        scanner.close();
        System.out.println("Aliyah Mariel Ramile");
    }
}
