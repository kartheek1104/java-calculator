import java.util.Scanner;

public class Calculator {

    public static double add(double[] numbers) {
        double sum = 0;
        for (double num : numbers) {
            sum += num;
        }
        return sum;
    }

    public static double subtract(double[] numbers) {
        double result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result -= numbers[i];
        }
        return result;
    }

    public static double multiply(double[] numbers) {
        double result = 1;
        for (double num : numbers) {
            result *= num;
        }
        return result;
    }

    public static double divide(double[] numbers) {
        double result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] == 0) {
                System.out.println("Error: Division by zero.");
                return Double.NaN;
            }
            result /= numbers[i];
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        System.out.println("=== Java Console Calculator ===");

        while (keepRunning) {
            System.out.println("\nChoose an operation:");
            System.out.println("1. Addition (+)");
            System.out.println("2. Subtraction (-)");
            System.out.println("3. Multiplication (*)");
            System.out.println("4. Division (/)");
            System.out.println("5. Exit");

            System.out.print("Enter choice (1-5): ");
            int choice = scanner.nextInt();

            if (choice == 5) {
                keepRunning = false;
                System.out.println("Exiting calculator. Goodbye!");
                continue;
            }

            System.out.print("How many numbers? ");
            int count = scanner.nextInt();
            if (count < 2) {
                System.out.println("Please enter at least 2 numbers.");
                continue;
            }

            double[] numbers = new double[count];
            for (int i = 0; i < count; i++) {
                System.out.print("Enter number " + (i + 1) + ": ");
                numbers[i] = scanner.nextDouble();
            }

            double result = 0;

            switch (choice) {
                case 1:
                    result = add(numbers);
                    break;
                case 2:
                    result = subtract(numbers);
                    break;
                case 3:
                    result = multiply(numbers);
                    break;
                case 4:
                    result = divide(numbers);
                    break;
                default:
                    System.out.println("Invalid choice! Try again.");
                    continue;
            }

            System.out.println("Result: " + result);
        }

        scanner.close();
    }
}
