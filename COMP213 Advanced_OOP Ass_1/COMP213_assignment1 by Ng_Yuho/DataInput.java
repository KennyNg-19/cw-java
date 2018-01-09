
import java.util.Scanner;

/**
 * The InputOutput class implements several method 
 * taking the human input from the keyboard.
 *
 * @author Shuffle
 * @version 1.0
 */
public class DataInput {

    static Scanner scanner = new Scanner(System.in);

    /**
     * Returns the name user inputs.
     * 
     * @return the name String
     */
    public static String inputName() {
        System.out.println("[System] Name ?");
        return inputString();
    }

    /**
     * Returns the integer the user inputs,
     * if input is not a positive integer, 
     * keep asking until the input is valid.
     * 
     * @return the integer the user inputs
     */
    public static int inputInteger() {

        System.out.print(">>> ");
        int number = 0;
        while (true) {
            class Input { // local class in the while-loop

                public int number;

                Input(String s) {
                    try {
                        number = Integer.parseInt(s);
                        if (number <= 0) {
                            System.out.print("[Warning] Input is not a positive Integer, please enter again:\n");
                            System.out.print(">>> ");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[Warning] Input is not an Integer, please enter again: ");
                        System.out.print(">>> ");
                    }
                }
            }
            String s = scanner.nextLine();
            Input input = new Input(s);
            number = input.number;
            if (number > 0) {
                break;
            }
        }
        return number;
    }

    /**
     * Returns the String the user inputs.
     * 
     * @return the String input
     */
    public static String inputString() {
        System.out.print(">>> ");
        return scanner.nextLine();
    }

}
