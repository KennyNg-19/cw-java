
import java.util.ArrayDeque;

/**
 * The RandomComp class inherits from the Player class, and implements the
 * abstract method selectAttribute().
 *
 * @author Yuhao Wu
 * @version 1.0
 */
public class RandomComp extends Player {

    /**
     * Class constructor. Constructs a random computer player with player name
     * and a list of cards.
     *
     * @param playerName the name of this player
     * @param cards the cards of this player
     */
    public RandomComp(String playerName, ArrayDeque<Card> cards) {
        super("RandomC_" + playerName, cards);

        System.out.format("[System] A Random_Computer Player: %s, joins...... ", getPlayerName());
    }

    /**
     * Returns the random index of Attribute this Computer player chooses.
     *
     * @return the Attribute with a random index
     */
    @Override
    public String selectAttribute() {
        int attributesNumber = getTopCard().getAttributesNumber();
        System.out.println("[Random]...Selects Attribute_" + attributesNumber);
        return "Attribute_" + getRandom(1, attributesNumber);
    }
}
