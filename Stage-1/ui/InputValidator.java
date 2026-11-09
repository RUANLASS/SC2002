package ui;

import java.util.Scanner;

public class InputValidator {

    private final Scanner sc = new Scanner(System.in);

    public int getIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int val = sc.nextInt();
                if (val >= min && val <= max) {
                    return val;
                }
            } else {
                sc.next(); // Clear non-integer
            }
            System.out.printf(">> Invalid choice! Please enter an integer from %d to %d.\n", min, max);
        }
    }
}
