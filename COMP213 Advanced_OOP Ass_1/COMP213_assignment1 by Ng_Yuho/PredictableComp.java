
import java.util.ArrayDeque;

/**
 * The PredicatbleComp class inherits from the Player class, and implements the
 * abstract method selectAttribute().
 *
 * @author Yuhao Wu
 * @version 1.0
 */
public class PredictableComp extends Player {

    /**
     * the first attribute this computer selects
     */
    private final String fixedAttribute;

    /**
     * Class constructor. Constructs a Player with player name and a list of
     * cards.
     *
     * @param playerName the name of Player
     * @param cards the cards list of Player
     */
    public PredictableComp(String playerName, ArrayDeque<Card> cards) {
        super("PredictableC_" + playerName, cards);

        System.out.format("[New Player] A Predictable-Computer Player: %s, joins...... ", getPlayerName());
        this.fixedAttribute = "Attribute_1";
        System.out.print("Setting the 1st Attribute as default...");
    }

    /**
     * Returns the constant Attribute this Computer player selects.
     *
     * @return the attribute this player selects
     */
    @Override
    public String selectAttribute() {
        System.out.println("[Default] Attribute_1 as default...");
        return fixedAttribute;
    }
}
