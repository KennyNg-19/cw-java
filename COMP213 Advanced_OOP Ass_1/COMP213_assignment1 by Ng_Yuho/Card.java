
import java.util.HashMap;
import java.util.Map;

/**
 * The Card class represents cards, contains memebers card names and attributes,
 * and provides methods about card itself and attributes.
 *
 * @author Yuhao Wu
 * @version 1.0
 */
public class Card {

    /**
     * the name of this card
     */
    private final String cardName;

    /**
     * the attributes of this card
     */
    private final HashMap<String, Integer> attributes;

    /**
     * Class sole constructor. Constructs a Card with card name and attribute
     * numbers.
     *
     * @param cardName the name of the Card.
     * @param attriNumber the number of attributes of the Card.
     */
    public Card(String cardName, int attriNumber) {
        this.cardName = cardName;
        this.attributes = new HashMap<String, Integer>();
        for (int i = 1; i <= attriNumber; i++) {
            String attributeName = "Attribute_" + String.valueOf(i);
            int value = (int) (Math.random() * 10);
            attributes.put(attributeName, value);
        }
    }

    /**
     * Checks if a Card contains the Attribute.
     *
     * @param newAttriName the Attribute to be checked
     * @return whether the Card contains this Attribute
     */
    public boolean containAttri(String newAttriName) {

        return getAttributes().containsKey(newAttriName);
    }

    /**
     * Returns the value of the attribute.
     *
     * @param attributeName the attribute of which value to be found
     * @return the value of the attribute
     */
    public int findAttributeValue(String attributeName) {
        int value = 0;
        for (Map.Entry<String, Integer> oneAttribute : getAttributes().entrySet()) {
            if (oneAttribute.getKey().equals(attributeName)) {
                value = oneAttribute.getValue();
            }
        }
        return value;
    }

    /**
     * Returns all the attributes' names of this card.
     *
     * @return a String about attributes' name
     */
    public String showAllAttri() {

        String attributeList = "";
        int index = getAttributesNumber();
        for (String attribute : getAttributes().keySet()) {
            attributeList = index
                    + " = "
                    + attribute
                    + ", \n"
                    + attributeList;
            index--;
        }
        return attributeList;
    }

    /**
     * Returns all attributes'values of this card.
     *
     * @return a String containing the attributes' names.
     */
    public String showAllValue() {

        String attributeList = "";
        for (Map.Entry<String, Integer> attribute : getAttributes().entrySet()) {
            attributeList = attribute.getKey() + ": " + attribute.getValue() + " | " + attributeList;
        }
        return attributeList;
    }

    /**
     * Returns the card name of this card.
     *
     * @return the cardName name
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Returns the list of attributes of this card.
     *
     * @return the attributes the list of attributes
     */
    public HashMap<String, Integer> getAttributes() {
        return attributes;
    }

    /**
     * Returns the number of attributes in this card.
     *
     * @return the number of attributes
     */
    public int getAttributesNumber() {
        return getAttributes().size();
    }

}
