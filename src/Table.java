import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Scanner;

//each table is a specific game with a specific # of seats
//players can sit at the seats
//table also functions as a dealer, dealing cards and collecting/calculating the pot
public class Table {
    private final char tableType;
    private final int seatCount;
    private int pot;
    private final int[] stakes;
    private final String gameType;
    private Player[] players;
    private ArrayList<Card> board;
    private int playerCount;
    private int foldCount;
    private Deck deck;
    private int bigBlind;
    private int smallBlind;
    private int currentBet;
    private boolean isRunning;
    //private final int ante;


    //initiates table
    public Table (char tableType, int seatCount, int[] stakes, String gameType) {
        this.tableType = tableType;
        this.seatCount = seatCount;
        this.stakes = stakes;
        this.gameType = gameType;

        players = new Player[seatCount];
        board = new ArrayList<Card>();
        playerCount = 0;
        foldCount = 0;
        deck = new Deck();
        bigBlind = -1;
        smallBlind = -1;
        isRunning = false;
    }

    //adds a player to the players list, gives them a chip count and a specific seat
    public void addPlayer (Player player, int seat, int chipCount) {
        if (players[seat] == null) players[seat] = player;

        player.setChipCount(chipCount);
        playerCount++;
        // assumes player is in the hand at the start of the game
        player.setInHand(true);
    }

    //removes player at the giver seat
    public void removePlayer(int seat) {
        players[seat] = null;
    }

    //gathers and executes all the functions needed to run ring poker game
    public void runGame() {
        //starts the game and sets the blinds
        if (isRunning == false) {
            setBlinds();
            isRunning = true;
        }

        //game will run while there are at least 2 people seated at the table
        while (playerCount > 1) {
            moveBlinds();
            dealCards();
            initiatePot();
            preFlopBetting();
            dealFlop();
            getHandVals();
            postFlopBetting();
            dealTurnOrRiver();
            postFlopBetting();
            getHandVals();
            dealTurnOrRiver();
            postFlopBetting();
            getHandVals();
            completeHand();
            clearTable();
        }
    }

    //sets the BB and SB
    private void setBlinds () {
        for (int i = 0; i < players.length; i++) {
            if(players[i] == null) continue;
            else {
                if(smallBlind == -1) smallBlind = i;
                else {
                    bigBlind = i;
                    break;
                }
            }
        }
    }

    //moves the BB and SB to the next player
    private void moveBlinds() {
        //SB is set to person who was just BB
        smallBlind = bigBlind;
        //rotates clockwise using modulus until the next player is found, assigns them the BB
        for (int i = (bigBlind+1) % players.length; i < players.length; i = (i+1)%players.length) {
            if (players[i] == null) continue;
            else {
                bigBlind = i;
                break;
            }
        }
    }

    //
    //deals cards preflop to all players
    public void dealCards () {
        //the deck will be organized in a random order before the round starts
        deck.shuffleCards();

        //deals cards to every seat with an active player in it
        for (int i = 0; i < players.length; i++) {
            if (players[i] != (null)) {
                //creates an arraylist where the two dealt cards will be added
                Card[] cards = new Card[2];
                cards[0] = deck.drawCard();
                cards[1] = deck.drawCard();
                //the array is set as the player's hole cards
                players[i].setHoleCards(cards);
            }
        }
    }

    //deals the flop out
    public void dealFlop () {
        //adds 3 cards (flop) to the board
        board.add(deck.drawCard());
        board.add(deck.drawCard());
        board.add(deck.drawCard());

        //iterates through players, gets their hand ranking, and sets it
        for (int i = 0; i < players.length; i++) {
            if (players[i] != (null)) {

                //creates new Hand and assigns it to the player
                Hand hand = new Hand(players[i].getHoleCards(), board);
                players[i].setHand(hand);
                players[i].getHand().getHandRanking();
            }
        }
    }

    //deals the turn or river card
    public void dealTurnOrRiver () {
        board.add(deck.drawCard());
        getHandVals();
    }

    // collects blinds and adds them to the pot
    private void initiatePot () {
        //BB collection
        if (players[bigBlind].getChipCount() > stakes[1]) {
            pot += stakes[1];
            players[bigBlind].setChipCount(players[bigBlind].getChipCount()-stakes[1]);
        }
        //edge case, if the players stack size is less than the blind
        else {
            pot += players[bigBlind].getChipCount();
            players[bigBlind].setChipCount(0);
        }

        //SB collection
        if (players[smallBlind].getChipCount() > stakes[0]) {
            pot += stakes[0];
            players[smallBlind].setChipCount(players[smallBlind].getChipCount()-stakes[0]);
        }
        //edge case, if the players stack size is less than the blind
        else {
            pot += players[smallBlind].getChipCount();
            players[smallBlind].setChipCount(0);
        }
    }
    // function for running player actions preflop and postflop
    public void playerAction(int currPlayer, int previousBet, boolean isPreflop) {
        int firstToAct = currPlayer;
        boolean actionOver = false;
        boolean firstAction = true;
        while (!actionOver) {
            if (foldCount == playerCount - 1) {completeHand(); break;}
            // skips any empty seats or folded players
            if (players[currPlayer] == null || !players[currPlayer].getInHand()) {currPlayer = (currPlayer + 1)
                    % players.length; continue;}
            // moves on to the flop if any of these conditions are met
            else if (isPreflop && players[currPlayer].getCurrentBet() == currentBet && currentBet >
                    stakes[1]) actionOver = true;
            else if (isPreflop && players[currPlayer].getCurrentBet() == stakes[1] && currPlayer !=
                    bigBlind) actionOver = true;
            else if (!isPreflop && players[currPlayer].getCurrentBet() == currentBet && currentBet
                    > 0) actionOver = true;
            else if (!isPreflop && currPlayer == firstToAct && currentBet == 0 && !firstAction)
                actionOver = true;
            else {
                System.out.println("current bet is: " + currentBet);
                System.out.println("your bet: ");
                Scanner sc = new Scanner(System.in);
                int bet = sc.nextInt();
                // if block handling cases where a bet is less than a min-raise. Assumes a bet of 0 is a fold and a bet
                // equaling the current bet is a call
                int minBet = (currentBet - previousBet) + currentBet;
                if (bet != players[currPlayer].getChipCount()) {
                    while (bet != 0 && bet != currentBet && bet < minBet) {
                        System.out.println("invalid bet");
                        System.out.println("your new bet: ");
                        sc = new Scanner(System.in);
                        bet = sc.nextInt();
                    }
                    if (bet == 0 && currentBet > 0) {
                        foldCount ++;
                        players[currPlayer].setInHand(false);
                    }
                    else if (bet == players[currPlayer].getChipCount() || bet >= minBet) {
                        previousBet = currentBet;
                        currentBet = bet;
                    }
                }
                else {
                    if (bet > currentBet) {
                        previousBet = currentBet;
                        currentBet = bet;
                    }
                }
                players[currPlayer].setChipCount(
                        players[currPlayer].getChipCount() - (bet -
                                players[currPlayer].getCurrentBet()));
                pot += (bet - players[currPlayer].getCurrentBet());
                players[currPlayer].setCurrentBet(bet);
                currPlayer = (currPlayer + 1) % players.length;
                firstAction = false;
            }
        }
    }

    //deals with the pre-flop betting rounds
    public void preFlopBetting() {
        // pre-flop betting starts at the player to the left of the big blind
        players[smallBlind].setCurrentBet(stakes[0]);
        players[bigBlind].setCurrentBet(stakes[1]);
        int previousBet = 0;
        // initializes currPlayer index to be one more than the big blind, wrapping around players.length and
        // skipping any nulls
        int currPlayer = (bigBlind + 1) % players.length;
        while (players[currPlayer] == null) {
            currPlayer = (currPlayer + 1) % players.length;
        }
        currentBet = stakes[1];
        playerAction(currPlayer, 0, true);
        if (playerCount == 2) {
            int temp = bigBlind;
            bigBlind = smallBlind;
            smallBlind = temp;
        }
    }

    public void postFlopBetting() {
        int button = (smallBlind - 1) % players.length;
        // Initializes currPlayer index to be the small blind, wrapping around players.length and
        // skipping any nulls
        int currPlayer = smallBlind;
        while (players[currPlayer] == null) currPlayer = (currPlayer + 1) % players.length;
        currentBet = 0;
        int previousBet = 0;
        for (Player player : players) {
            if (player != null) player.setCurrentBet(0);
        }
        playerAction(currPlayer, 0, false);
    }
    public void completeHand() {
        // Initializing the best made hand rank
        HandRanks max_rank = null;
        for (Player player : players) {
            if (player == null || !player.getInHand()) { continue; }
            else if (max_rank == null) max_rank = player.getHand().getHandRanking();
            else if (player.getHand().getHandRanking().getRanking() > max_rank.getRanking())
                max_rank = player.getHand().getHandRanking();
        }
        // Appending all players with hand rank equal to the max rank
        ArrayList<Player> potential_winners = new ArrayList();
        for (Player player : players) {
            if (player != null && player.getInHand() &&
                    player.getHand().getHandRanking() == max_rank) potential_winners.add(player);
        }
        // Initialize winners and find winner(s) within potential_winners array list
        ArrayList<Player> winners = new ArrayList<>();
        for (Player player : potential_winners) {
            if (winners.size() == 0) {winners.add(player); continue;}
            int hand_pos = 0;
            boolean decided = false;
            // Comparing cards at index hand_pos within the current player in potential_winners and the
            // first player in winners
            while (hand_pos < 5 && !decided) {
                int currWinnerVal = winners.get(0).getHand().getFiveCardHand()[hand_pos].getVal();
                int currPlayerVal = player.getHand().getFiveCardHand()[hand_pos].getVal();
                if (currWinnerVal > currPlayerVal) decided = true;
                else if (currWinnerVal == currPlayerVal) hand_pos ++;
                else {
                    winners.clear();
                    decided = true;
                }
            }
            // decided is true in two cases: we have found a better hand or a worse hand. winners array list is updated
            // to just contain the current player in potential_winners if their hand is better. Otherwise, we continue to
            // the next iteration since a player with a worse hand will not be a winner.
            if (decided && winners.size() == 0) {
                winners.add(player);
            }
            // if we iterated through the whole hand and decided is false, then we know the current player in
            // potential_winners has an equal strength hand to the current winner(s), so that player is added.
            else if (hand_pos == 5) winners.add(player);
        }
        // Chip stacks of players in winners are updated to contain pot/winners.size() additional chips.
        for (Player winner : winners) {
            winner.setChipCount(winner.getChipCount() + (pot / winners.size()));
        }
    }

    //Goes through the list of players and reassigns Hand value after turn and river
    public void getHandVals() {
        for (int i = 0; i < players.length; i++) {
            if (players[i] != (null)) {
                players[i].getHand().getHandRanking();
            }
        }
    }

    //Resets Player Hands, the Table board, and rejoins dead cards with to the deck
    public void clearTable() {
        for (int i = 0; i < players.length; i++) {
            if (players[i] != (null)) {
                players[i].setHand(null);
                players[i].setInHand(true);
            }
        }
        board = new ArrayList<Card>();
        deck.joinDeck();
        pot = 0;
        foldCount = 0;
        if (playerCount == 2) {
            int temp = bigBlind;
            bigBlind = smallBlind;
            smallBlind = temp;
        }
    }
}