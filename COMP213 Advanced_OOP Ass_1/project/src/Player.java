
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * The abstract Player class represents general players, contains memebers of
 * player names and cards, provides methods about card itself and attributes,
 * and is inherited by other subclasses.
 *
 * @author Yuhao Wu
 * @version 1.0
 */
abstract public class Player {

    /**
     * The name of the player.
     */
    protected String playerName;

    /**
     * The cards of the player.
     */
    protected ArrayDeque<Card> cards;

    /**
     * Class constructor. Constructs a Player with player name and a list of
     * cards.
     *
     * @param playerName the name of Player
     * @param cards the cards list of Player
     */
    public Player(String playerName, ArrayDeque<Card> cards) {

        this.playerName = playerName;
        this.cards = cards;
    }

    /**
     * Returns a random number between max and min(both inclusively).
     *
     * @param max the upper limit
     * @param min the lower limit
     * @return the generated random
     */
    public int getRandom(int max, int min) {

        int range = max - min + 1;
        return (int) (Math.random() * range) + min - 1;
    }

    /**
     * Shows two players' top cards information related to the attribute.
     *
     * @param players the players in the game
     */
    public static void showTopCards(ArrayList<Player> players) {

        System.out.println("[TOP cards] Here are all top cards: ");
        System.out.println("------------------------------------------------------------------------------------");
        for (Player player : players) {
            Card topCard = player.getTopCard();
            System.out.format("| %-17s| %-7s | %s\n", player.getPlayerName(), topCard.getCardName(),
                    topCard.showAllValue());
        }
        System.out.println("------------------------------------------------------------------------------------");
    }

    /**
     * Shows the two cards to be compared.
     *
     * @param firstPlayer the last winner player
     * @param secondPlayer the player to be challenged by winnner player
     * @param chosenAttribute the attribute of the round
     */
    public static void showTwoCards(Player firstPlayer, Player secondPlayer, String chosenAttribute) {

        System.out.println("\n[System] Next two: ");
        Card firstCard = firstPlayer.getTopCard();
        Card secondCard = secondPlayer.getTopCard();
        System.out.format("-- Player: %-17s| %s value: %d\n", firstPlayer.getPlayerName(),
                chosenAttribute, firstCard.findAttributeValue(chosenAttribute));
        System.out.format("-- Player: %-17s| %s value: %d\n", secondPlayer.getPlayerName(),
                chosenAttribute, secondCard.findAttributeValue(chosenAttribute));
    }

    /**
     * Compares two players' attribute values and returns the Player with bigger
     * one.
     *
     * @param firstPlayer the first player to be compared
     * @param secondPlayer the second player to be compared
     * @param attribute the attribute selected in the round
     * @return the Player with bigger value
     */
    public static Player hasBiggerValue(Player firstPlayer, Player secondPlayer, String attribute) {

        int firstCardValue = firstPlayer.getTopCard().findAttributeValue(attribute);
        int secondCardValue = secondPlayer.getTopCard().findAttributeValue(attribute);
        //return winner, if they are ties, return the 1st one
        if (firstCardValue >= secondCardValue) {
            Game.setLoser(secondPlayer);
        } else {
            Game.setLoser(firstPlayer);
        }
        return (firstCardValue >= secondCardValue) ? firstPlayer : secondPlayer;
    }

    /**
     * Returns if two object(players) are identical.
     *
     * @param obj the object to be compared
     * @return whether two object(players) are identical
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Player)) {
            return false;
        }
        Player o = (Player) obj;
        return o.getPlayerName().equals(this.getPlayerName());
    }

    /**
     * Collects all the cards in the list.
     *
     * @param cards the list of cards
     */
    public void putAtBottom(ArrayDeque<Card> cards) {

        for (Card card : cards) {
            getCards().offerLast(card);
        }
    }

    /**
     * Returns <code>true</code> if this player has no card and is lost.
     *
     * @return <code>true</code> if the player loses the game <code>false</code>
     * otherwise
     */
    public boolean lost() {

        if (getCardsNumber() == 0) {
            System.out.format("\n[Players Out] %s has no card and is out !!!!", getPlayerName());
        }
        return (getCardsNumber() == 0);
    }

    /**
     * Returns <code>true</code> if this player has no card.
     *
     * @return <code>true</code> if the player has no card <code>false</code>
     * otherwise
     */
    public boolean noCard() {
        return (this.getCardsNumber() == 0);
    }

    /**
     * Retuns the top card in the deck of this player.
     *
     * @return the top card of the deck
     */
    public Card getTopCard() {
        return this.getCards().peek();
    }

    /**
     * Returns the top card of this player and loses it.
     *
     * @return the top card of the deck
     */
    public Card loseTopCard() {

        System.out.format("[Card Lost] %s loses a card.\n", getPlayerName());
        return this.getCards().pop();
    }

    /**
     * Returns an Attribute this player chooses. Needs implementation of
     * subclass.
     *
     * @return an Attribute this player chooses
     */
    public abstract String selectAttribute();

    /**
     * Gets the number of cards.
     *
     * @return the cardsNumber
     */
    public int getCardsNumber() {
        return getCards().size();
    }

    /**
     * Gets the name of this player.
     *
     * @return the name of this player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the name of this player.
     *
     * @param playerName this player name to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Gets the cards list of this player.
     *
     * @return the cards list
     */
    public ArrayDeque<Card> getCards() {
        return cards;
    }

}
