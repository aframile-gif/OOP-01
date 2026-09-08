import java.util.Scanner;

public class Ramile_Problem10 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        System.out.print("Enter discount percentage: ");
        double discountPercent = scanner.nextDouble();

        System.out.print("Enter amount paid: ");
        double amountPaid = scanner.nextDouble();

        // calculations
        double gross = price * quantity;
        double discount = gross * (discountPercent / 100.0);
        double afterDiscount = gross - discount;
        double vat = afterDiscount * 0.12;
        double finalBill = afterDiscount + vat;
        double balance = amountPaid - finalBill;

        // Ternary operator 
        String paymentStatus = (amountPaid >= finalBill) ? "SUFFICIENT PAYMENT" : "INSUFFICIENT PAYMENT";
        String balanceLabel = (amountPaid >= finalBill) ? "Change=" : "Difference=";

        System.out.println("Gross=" + gross + "; Discount=" + discount + "; After Discount=" + afterDiscount + "; VAT=" + vat + "; Final Bill=" + finalBill + "; " + balanceLabel + balance + "; " + paymentStatus);

        scanner.close();
        System.out.println("Aliyah Mariel Ramile");
    }
}
