import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.min;

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
            //TODO completeHand();
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

    //collects blinds and adds them to the pot
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

    //deals with the pre-flop betting rounds
    public void preFlopBetting() {
        //pre-flop betting starts at the player to the left of the big blind
        //TODO following code might pick an empty seat, fix
        players[smallBlind].setCurrentBet(stakes[0]);
        players[bigBlind].setCurrentBet(stakes[1]);
        int currPlayer = (bigBlind + 1) % players.length;
        while (players[currPlayer] == null) {
            currPlayer = (currPlayer + 1) % players.length;
        }
        currentBet = stakes[1];
        boolean actionOver = false;
        while (!actionOver) {
            if(players[currPlayer] == null) {currPlayer = (currPlayer + 1) % players.length; continue;}
            //TODO complete betting round code
            else if (players[currPlayer].getCurrentBet() == currentBet && currentBet > stakes[1]) actionOver = true;
            else if (players[currPlayer].getCurrentBet() == stakes[1] && currPlayer != bigBlind) actionOver = true;
            else {
                System.out.println("current bet is: " + currentBet);
                System.out.println("your bet: ");
                Scanner sc = new Scanner(System.in);
                int bet = sc.nextInt();
                if (bet < 2 * currentBet) {
                    if (currPlayer == bigBlind && currentBet == stakes[1] && bet == 0) {currPlayer = (currPlayer + 1) %
                            players.length; continue;
                    } else if (players[currPlayer].getChipCount() < currentBet) {
                        bet = players[currPlayer].getChipCount();
                    } else if (bet > currentBet) {
                        bet = 2 * currentBet;
                    } else if (bet != 0) {
                        bet = currentBet;
                    } else {players[currPlayer] = null; currPlayer = (currPlayer + 1) % players.length; continue;}
                }
                players[currPlayer].setChipCount(
                        players[currPlayer].getChipCount() - (bet - players[currPlayer].getCurrentBet()));
                pot += (bet - players[currPlayer].getCurrentBet());
                players[currPlayer].setCurrentBet(bet);
                currentBet = bet;
                currPlayer = (currPlayer + 1) % players.length;
            }
        }
    }
    public void postFlopBetting() {
        int button = (smallBlind - 1) % players.length;
        int currPlayer = smallBlind;
        while (players[currPlayer] == null) currPlayer = (currPlayer + 1) % players.length;
        int firstToAct = currPlayer;
        currentBet = 0;
        for (Player player : players) {
            if (player != null) player.setCurrentBet(0);
        }
        boolean actionOver = false;
        boolean firstAction = true;
        while (!actionOver) {
            if (players[currPlayer] == null) {
                currPlayer = (currPlayer + 1) % players.length;
                continue;
            }
            //TODO complete betting round code
            if (players[currPlayer].getCurrentBet() == currentBet && currentBet > 0) actionOver = true;
            else if (currPlayer == firstToAct && currentBet == 0 && !firstAction) actionOver = true;
            else {
                System.out.println("current bet is: " + currentBet);
                System.out.println("your bet: ");
                Scanner sc = new Scanner(System.in);
                int bet = sc.nextInt();
                if (bet < 2 * currentBet) {
                    if (currPlayer == bigBlind && currentBet == stakes[1] && bet == 0) {currPlayer = (currPlayer + 1) %
                            players.length; continue;
                    } else if (players[currPlayer].getChipCount() < currentBet) {
                        bet = players[currPlayer].getChipCount();
                    } else if (bet > currentBet) {
                        bet = 2 * currentBet;
                    } else if (bet != 0) {
                        bet = currentBet;
                    } else {players[currPlayer] = null; currPlayer = (currPlayer + 1) % players.length; continue;}
                }
                players[currPlayer].setChipCount(
                        players[currPlayer].getChipCount() - (bet - players[currPlayer].getCurrentBet()));
                pot += (bet - players[currPlayer].getCurrentBet());
                players[currPlayer].setCurrentBet(bet);
                currentBet = bet;
                currPlayer = (currPlayer + 1) % players.length;
            }
        }
    }
    public void completeHand() {

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
            }
        }
        board = new ArrayList<Card>();
        deck.joinDeck();
    }
}