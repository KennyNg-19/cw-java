
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Game class implements the interactive game procedure by quite a few
 * global attributes and methods.
 *
 * @author Yuhao Wu
 * @version 1.0
 */
public class Game {

    private final static int ATTRIBUTES_IN_POKER = 4;
    private final static int ATTRIBUTES_IN_UNO = 3;

    private static int decidedCardsNumber;
    private static String deckType;
    private static ArrayList<Player> players;
    private static int humanNumber;
    private static int computersNumber;
    private static int gameRound;
    private static Player loser;
    private static Player nextAttributeMaker;

    /**
     * Runs the whole game procedure, from setup, then start to fianl over
     * stages.
     */
    public static void runGame() {
        System.out.println("===================================================Setup===================================================");
        System.out.println("[System] Welcome to the Top Trumps Card game");
        //when initialize, add the user in the game
        players = new ArrayList<>();
        initializeUser();

        //ask user to add more players
        boolean enoughPlayers = false;
        do {
            boolean answerNo = false;
            while (!answerNo) {
                enoughPlayers = (getHumanNumber() >= 1 && getComputersNumber() >= 1);//Here to be changed as time goes by
                Game.showPlayersNumber();
                System.out.println("[System] Would you need more players? y/n");
                if (enoughPlayers) {
                    System.out.println("[Note] (Enough players, you can start now by entering \"n\")");
                }
                String answer = DataInput.inputString();
                switch (answer) {
                    case "y":
                        //add a new player
                        Game.addOnePlayer();
                        break;
                    case "n":
                        if (enoughPlayers) {
                            answerNo = true;
                        } else {
                            System.out.println("[Warning] Sorry, at least 1 human + 1 computer players, you need more players...");
                        }
                        break;
                    default:
                        System.out.println("[Warning] Invalid, please input again");
                        break;
                }
            }
        } while (!enoughPlayers);

        //game starts
        Game.startGame();
        Game.gameRound = 1;

        //Until there is onl one player
        while (!(getPlayersNumber() == 1)) {
            Game.startOneRoundRobin();
        }
        System.out.println("===================================================End===================================================");
        System.out.format("\n[System] Winner is %s, with totally %d cards, Congratulations !\n\n",
                getPlayers().get(0).getPlayerName(), getPlayers().get(0).getCards().size());
    }

    /**
     * Initializes the user as a player including deciding the unified players'
     * cards number, and deck type in this game.
     */
    public static void initializeUser() {

        String userName = DataInput.inputName();
        //set cards number
        decideCardsNumber();

        //ask choosing deck type
        decideDeck();
        getPlayers().add(new Human(userName, deal()));
        increaseHumanNumber();
    }

    /**
     * Sets the number of cards in the game.
     */
    public static void decideCardsNumber() {
        System.out.println("[System] How many cards per player would you like to deal? (better LESS than 10)");
        boolean valid = false;
        do {
            int cardNumber = DataInput.inputInteger();
            if (cardNumber > 10) {
                System.out.println("[Warning] The card number is too large, please make sure less than 10 !");
            } else {
                setDecidedCardsNumber(cardNumber);
                valid = true;
            }
        } while (!valid);
    }

    /**
     * Deals the cards to each player.
     *
     * @return the cards dealt
     */
    public static ArrayDeque<Card> deal() {
        ArrayDeque<Card> fullDeck = new ArrayDeque<>(getDecidedCardsNumber());
        int attriNumber = (getDeckType().equals("poker")) ? ATTRIBUTES_IN_POKER : ATTRIBUTES_IN_UNO;
        for (int i = 0; i < getDecidedCardsNumber(); i++) {
            Card card = new Card("Player" + (getPlayersNumber() + 1) + "_Card_" + String.valueOf(i + 1), attriNumber);
            fullDeck.offer(card);
        }
        return fullDeck;
    }

    /**
     * Choose the deck type out of the existing two cards.
     */
    public static void decideDeck() {

        boolean validDeckType = false;
        int choice;
        while (!validDeckType) {
            System.out.println("[System] Which following deck of Card do you want to play ? Here are:\n"
                    + "1 = Poker with 4 attributes,\n"
                    + "2 = Uno with 3 attributes,\n"
                    + "enter the index:");

            choice = DataInput.inputInteger();
            if (choice == 1 || choice == 2) {
                switch (choice) {
                    //set cards type
                    case 1:
                        setDeckType("poker");
                        System.out.format("[System] For this game, you choose the deck of <%s> with 4 attributes per card.\n", getDeckType());
                        validDeckType = true;
                        break;
                    case 2:
                        setDeckType("uno");
                        System.out.format("[System] For this game, you choose the deck of <%s> with 3 attributes per card.\n", getDeckType());
                        validDeckType = true;
                        break;
                    default:
                        System.out.println("[Warning] Invalid, please input again");
                }
            } else {
                System.out.println("[Warning] Not found that type, please try again");
            }
        }

    }

    /**
     * Starts the playing cards after the setup stage.
     */
    public static void startGame() {

        System.out.println("===================================================Start===================================================");
        System.out.format("[System] We will play <%s>\n", getDeckType());
        //show the players and their name
        System.out.format("[System] We have %d players: \n", getPlayersNumber());
        showPlayerStates();
        System.out.println("\n[Rules] \n"
                + "1. In the 1st round, the 1st player would be the default Attribute-decider;\n"
                + "while in later rounds, the winner of last round will be so.\n"
                + "2. If there are ties in a round, simply choose as the winner\n"
                + "the player who is closest to the first player that was entered in the game");
    }

    /**
     * Shows the real-time player decks' states every round.
     */
    public static void showPlayerStates() {
        System.out.println("\n        ------------------Now, Players-----------------");
        int num = 1;
        for (Player player : Game.getPlayers()) {
            System.out.format("        Player %d: %-20s ", num, player.getPlayerName());
            System.out.print(" | ");
            System.out.format("has %d card(s) ", player.getCardsNumber());
            System.out.println("| ");
            System.out.println("        ------------------------------------------------");
            num++;
        }
    }

    /**
     * Starts one round, changing the states of each players.
     */
    public static void startOneRoundRobin() {

        String roundAttribute;

        System.out.format("\n================================================Round %d===================================================\n", gameRound);
        if (gameRound == 1) {
            Human firstPlayer = (Human) Game.getPlayers().get(0);
            setNextAttributeMaker(firstPlayer);
            roundAttribute = decideAttribute();
            getRoundWinner(roundAttribute);
        } else {
            //After the 1st round, the winner decide the attribute
            roundAttribute = decideAttribute();
            getRoundWinner(roundAttribute);
        }
        increaseRound();
        removeLoser();
    }

    /**
     * Returns the attribute selected every round.
     *
     * @return the attribute to be selected
     */
    public static String decideAttribute() {
        System.out.println("[System]<" + getDeckType() + "> has following attributes:\n"
                + getNextAttributeMaker().getTopCard().showAllAttri());
        System.out.format("[Nominated Player] The attribute-decider is the Player: \n!!! %s !!!\n",
                getNextAttributeMaker().getPlayerName());

        //start to choose and search for the attribute
        System.out.println("[System] Choose an attribute from as above in this round, enter its Index: ");
        String decidedAttribute = getNextAttributeMaker().selectAttribute();
        System.out.format("\n[Attribute] " + decidedAttribute + " to be played with!\n");
        return decidedAttribute;
    }

    /**
     * Returns the winner of the round depending the comparison of all card
     * values.
     *
     * @param chosenAttribute the attribute selected by nominated player
     * @return the winner player of this round
     */
    public static Player getRoundWinner(String chosenAttribute) {

        //make something on players lists, so copy first
        Player winner = getPlayers().get(0);
        ArrayDeque<Card> cardCollection = new ArrayDeque<>();

        //start comparison 
        Player.showTopCards(getPlayers());
        System.out.println("[System] Comparing....");
        for (int num = 1; num < getPlayersNumber(); num++) {
            Player attackedPlayer = getPlayers().get(num);

            Player.showTwoCards(winner, attackedPlayer, chosenAttribute);
            //compare by pair           
            winner = Player.hasBiggerValue(winner, attackedPlayer, chosenAttribute);
            System.out.println("[Result] " + winner.getPlayerName() + "'s is bigger");
            Card lostCard = getLoser().getTopCard();
            cardCollection.offer(lostCard);
            getLoser().loseTopCard();
        }
        cardCollection.offer(winner.getCards().poll());
        System.out.format("\n================================================Round Winner: %s=============================================\n",
                winner.getPlayerName());
        winner.putAtBottom(cardCollection);
        showPlayerStates();
        setNextAttributeMaker(winner);//modify the next maker
        return winner;
    }

    /**
     * Adds a new Player into the game.
     */
    public static void addOnePlayer() {

        Player player = null;
        boolean validIndex = false;
        do {
            System.out.println("[System] Which type of players would you like?\n"
                    + "Human = h,\n"
                    + "Computer =c,\n"
                    + "enter h/c");
            String choice = DataInput.inputString();
            switch (choice) {
                case "h":
                    player = generatOneHumanPlayer();
                    validIndex = true;
                    break;
                case "c":
                    player = generateOneComputerPlayer();
                    validIndex = true;
                    break;
                default:
                    System.out.println("[Warning] Invalid, please input again");
            }
        } while (!validIndex);
        Game.getPlayers().add(player);
    }

    /**
     * Constructs a Human player by its name and dealing cards.
     *
     * @return the Human player initialized
     */
    public static Human generatOneHumanPlayer() {
        String playerName = DataInput.inputName();
        increaseHumanNumber();
        return new Human(playerName, deal());
    }

    /**
     * Constructs a Computer player by its name and dealing cards.
     *
     * @return the one of two types of Computer player initialized
     */
    public static Player generateOneComputerPlayer() {
        String playerName = DataInput.inputName();
        Player player = null;
        boolean generated = false;
        do {
            System.out.println("[System] Which type of Computer player would you like ?\n"
                    + "1 = Predictable, always selects the 1st attribute on their card\n"
                    + "2 = Random, always radomly selects one of attributes on their card\n"
                    + "3 = Smart,[Intelligent !!!] always selects the highest valued attribute on their card, be careful !");
            int choice = DataInput.inputInteger();
            switch (choice) {
                case 1:
                    player = new PredictableComp(playerName, deal());
                    generated = true;
                    break;
                case 2:
                    player = new RandomComp(playerName, deal());
                    generated = true;
                    break;
                case 3:
                    player = new SmartComp(playerName, deal());
                    generated = true;
                    break;
                default:
                    System.out.println("[Warning] Invalid, please input again");
            }
        } while (!generated);
        increaseComputersNumber();
        return player;
    }

    /**
     * Removes the player(s) with no cards every round.
     */
    public static void removeLoser() {
        System.out.println("");
        for (Iterator<Player> iterator = getPlayers().iterator(); iterator.hasNext();) {
            Player next = iterator.next();
            if (next.lost()) {
                if (next instanceof Human) {
                    humanNumber--;
                } else {
                    computersNumber--;
                }
                iterator.remove();
            }
        }
        showPlayersNumber();
    }

    /**
     * Shows the real-time players'number in the game.
     */
    public static void showPlayersNumber() {
        System.out.print("\n**********************************************************************");
        System.out.format("\n[Existing Players] Now there are %d player(s): %d human and %d computer(s)\n",
                getPlayersNumber(), getHumanNumber(), getComputersNumber());
        System.out.print("**********************************************************************\n");
    }

    /**
     * Gets the real-time number of the players in the game.
     *
     * @return the playersNumber the real-time number of players
     */
    public static int getPlayersNumber() {
        return getComputersNumber() + getHumanNumber();
    }

    /**
     * Gets the real-time number of the Human players in the game.
     *
     * @return the humanNumber the real-time number of the Human players
     */
    public static int getHumanNumber() {
        return humanNumber;
    }

    /**
     * Increases the total number of players in the game.
     */
    public static void increaseHumanNumber() {
        Game.humanNumber++;
    }

    /**
     * Gets the real-time number of Computer players.
     *
     * @return the computersNumber
     */
    public static int getComputersNumber() {
        return computersNumber;
    }

    /**
     * Increases the total number of Computer players in the game.
     */
    public static void increaseComputersNumber() {
        Game.computersNumber++;
    }

    /**
     * Gets the name of deck type decided by user player.
     *
     * @return the deckType the deck user selected
     */
    public static String getDeckType() {
        return deckType;
    }

    /**
     * Sets the deck type in this game.
     *
     * @param aDeckType the deckType to set
     */
    public static void setDeckType(String aDeckType) {
        deckType = aDeckType;
    }

    /**
     * Gets the player deciding the attribute next round.
     *
     * @return the nextAttributeMaker
     */
    public static Player getNextAttributeMaker() {
        return nextAttributeMaker;
    }

    /**
     * Sets the player deciding the attribute next round.
     *
     * @param nextAttributeMaker the nextAttributeMaker to set
     */
    public static void setNextAttributeMaker(Player nextAttributeMaker) {
        Game.nextAttributeMaker = nextAttributeMaker;
    }

    /**
     * Gets the cards number decided by the user player.
     *
     * @return the decidedCardsNumber
     */
    public static int getDecidedCardsNumber() {
        return Game.decidedCardsNumber;
    }

    /**
     * Sets the cards number decided by the user player.
     *
     * @param decidedCardsNumber the decidedCardsNumber to set
     */
    public static void setDecidedCardsNumber(int decidedCardsNumber) {
        Game.decidedCardsNumber = decidedCardsNumber;
    }

    /**
     * Gets the list of players.
     *
     * @return the players
     */
    public static ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Increases the number of the round.
     */
    public static void increaseRound() {
        Game.gameRound++;
    }

    /**
     * Gets the loser in the .
     *
     * @return the loser the player who loses in the comparison
     */
    public static Player getLoser() {
        return loser;
    }

    /**
     * Sets the loser player in the comparison.
     *
     * @param loser the player who loses in the comparison
     */
    public static void setLoser(Player loser) {
        Game.loser = loser;
    }

}
