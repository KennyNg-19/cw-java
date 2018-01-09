
/**
 * Represents an attribute in a card.
 * Contains memebers attribute name and value.
 * Provides methods set() and get().
 *
 * @author Yuhao Wu
 * @version 1.0
 */
public class Attribute {

    /**
     * The name of this attribute.
     */
    private String attriName;
    
    private int attriValue;

    /**
     * Class constructor. Construct an Attribute with an attribute name and
     * value.
     *
     * @param attriName the name of the Attribute.
     * @param attriValue the value of the Attribute.
     */
    public Attribute(String attriName, int attriValue) {
        this.attriName = attriName;
        this.attriValue = attriValue;
    }

    /**
     * Returns if two object(Attributes) are identical.
     *
     * @param obj the object to be compared
     * @return whether two object(Attributes) are identical
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        Attribute a = (Attribute) obj;
        return a.getAttriName().equals(this.getAttriName());
    }

    /**
     * Returns the name of this Attribute.
     *
     * @return the attriName
     */
    public String getAttriName() {
        return attriName;
    }

    /**
     * Sets the name of this Attribute.
     *
     * @param attriName the attriName to set
     */
    public void setAttriName(String attriName) {
        this.attriName = attriName;
    }

    /**
     * Returns the value of this Attribute.
     *
     * @return the attriValue
     */
    public int getAttriValue() {
        return attriValue;
    }

    /**
     * Sets the value of this Attribute.
     *
     * @param attriValue the attriValue to set
     */
    public void setAttriValue(int attriValue) {
        this.attriValue = attriValue;
    }

}
