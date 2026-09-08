import java.util.Scanner;

public class Ramile_Problem9 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first integer (a): ");
        int a = scanner.nextInt();

        System.out.print("Enter second integer (b): ");
        int b = scanner.nextInt();

        // Bitwise operations
        int bitwiseAnd = a & b;
        int bitwiseOr = a | b;
        int bitwiseXor = a ^ b;
        int bitwiseNotA = ~a;
        int leftShift = a << 1;
        int rightShift = a >> 1;

        System.out.println("AND=" + bitwiseAnd + "; OR=" + bitwiseOr + "; XOR=" + bitwiseXor + "; NOT a=" + bitwiseNotA + "; a<<1=" + leftShift + "; a>>1=" + rightShift);

        scanner.close();
        System.out.println("Aliyah Mariel Ramile");
    }
}

