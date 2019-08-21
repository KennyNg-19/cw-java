
import java.util.ArrayDeque;

/**
 * The Human class inherits from the Player class, and implements the abstract
 * method selectAttribute().
 *
 * @author Yuhao Wu
 * @version 1.0
 */
public class Human extends Player {

    /**
     * Class sole constructor. Constructs a Human player instance with the name
     * and a cards list.
     *
     * @param playerName the name of this player
     * @param cards the cards of this player
     */
    public Human(String playerName, ArrayDeque<Card> cards) {

        super("Human_" + playerName, cards);
        System.out.format("\n[System] A Human player: %s, joins...... ", getPlayerName());
    }

    /**
     * Returns an Attribute this Human player chooses.
     *
     * @return the Attribute this player choose
     */
    @Override
    public String selectAttribute() {
        int index;
        String chosenAttribute = null;
        boolean validInput = false;
        while (!validInput) {
            index = DataInput.inputInteger();
            if ((index - 1) < this.getTopCard().getAttributes().size()) {
                chosenAttribute = "Attribute_" + index;
                validInput = true;
            } else {
                System.out.println("[Warning] The index is out of bound, input again");
            }
        }
        return chosenAttribute;
    }
}
