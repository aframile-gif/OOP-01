import java.util.Scanner;

public class Ramile_Problem6 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter grade: ");
        double grade = scanner.nextDouble();

        // Ternary operator
        String result = (grade >= 75) ? "PASSED" : "FAILED";

        System.out.println("Result: " + result);

        scanner.close();
        System.out.println("Aliyah Mariel Ramile");
    }
}
