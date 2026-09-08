import java.util.Scanner;

public class Ramile_Problem8 {
 public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter grade: ");
        double grade = scanner.nextDouble();

        System.out.print("Enter attendance percentage: ");
        double attendance = scanner.nextDouble();

        // Logical AND operator (&&)
        boolean isEligible = (grade >= 85) && (attendance >= 80);

        System.out.println("Eligible: " + isEligible);

        scanner.close();
        System.out.println("Aliyah Mariel Ramile");
    } 
   }
