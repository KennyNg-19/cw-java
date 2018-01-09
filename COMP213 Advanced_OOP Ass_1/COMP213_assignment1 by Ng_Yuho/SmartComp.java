
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * The SmartComp class inherits from the Player class, and implements the
 * abstract method selectAttribute().
 *
 * @author Yuhao Wu
 * @version 1.0
 */
public class SmartComp extends Player {

    private String highestValue;

    /**
     * Class constructor. Constructs a Player with player name and a list of
     * cards.
     *
     * @param playerName the name of Player
     * @param cards the cards list of Player
     */
    public SmartComp(String playerName, ArrayDeque<Card> cards) {
        super("SmartC_" + playerName, cards);

        System.out.format("[New Player] A Smart-Computer Player: %s, joins...... ", getPlayerName());
    }

    /**
     * Returns the Attribute with highest value.
     *
     * @return the attribute this player selects
     */
    @Override
    public String selectAttribute() {

        HashMap<String, Integer> attributes = this.getTopCard().getAttributes();
        int max = 0;
        String maxAttribute = null;
        for (Map.Entry<String, Integer> attribute : attributes.entrySet()) {
            int value = attribute.getValue();
            if (value >= max) {
                max = value;
                maxAttribute = attribute.getKey();
            }
        }
        System.out.println("[Searching]...Searching the one with highest value");
        return maxAttribute;
    }

    /**
     *
     * @return the highestValue
     */
    public String getHighestValue() {
        return highestValue;
    }

    /**
     * @param highestValue the highestValue to set
     */
    public void setHighestValue(String highestValue) {
        this.highestValue = highestValue;
    }

}
