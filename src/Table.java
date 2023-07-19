import java.util.ArrayList;

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
    private boolean isRunning;
    //private final int ante;


    //instantiates table
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
        if(++playerCount > 1) {
            setBlinds();
            runGame();
        }
        player.setChipCount(chipCount);
    }

    public void removePlayer(int seat) {
        players[seat] = null;
        if(--playerCount >= 2) setBlinds();
    }

    private void setBlinds () {
        for (int i = players.length-1; i >= 0; i--) {
            if(players[i] == null) continue;
            else {
                if(bigBlind == -1) bigBlind = i;
                else {
                    smallBlind = i;
                    break;
                }
            }
        }
    }

    private void moveBlinds() {
        smallBlind = bigBlind;
        for (int i = (bigBlind+1)%players.length; i < players.length; i = (i+1)%players.length) {
            if (players[i] == null) continue;
            else {
                bigBlind = i;
            }
        }
    }

    public void runGame() {
        if (isRunning == false) {
            setBlinds();
            isRunning = true;
        }

        while (playerCount > 1) {
            moveBlinds();
            dealCards();
            initiatePot();
            preFlopBetting();
            dealFlop();
            getHandVals();
            //postFlopBetting();
            dealTurn();
            //postFlopBetting();
            getHandVals();
            dealRiver();
            //postFlopBetting();
            getHandVals();
            //completeHand();
        }
    }

    //deals cards preflop to all players
    //TODO the amount of cards dealt will change if the game is PLO, for example
    public void dealCards () {
        //the deck will be orgnized in a random order before the round starts
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
        board.add(deck.drawCard());
        board.add(deck.drawCard());
        board.add(deck.drawCard());

        for (int i = 0; i < players.length; i++) {
            if (players[i] != (null)) {

                Hand hand = new Hand(players[i].getHoleCards(), board);
                players[i].setHand(hand);
                players[i].getHand().getHandRanking();
            }
        }
    }

    //deals the turn
    public void dealTurn () {
        board.add(deck.drawCard());
        getHandVals();
    }

    //deals the river
    public void dealRiver () {
        board.add(deck.drawCard());
        getHandVals();
    }

    //collects blinds and adds them to the pot
    private void initiatePot () {
        //big blind collection
        if (players[bigBlind].getChipCount() > stakes[1]) {
            pot += stakes[1];
            players[bigBlind].setChipCount(players[bigBlind].getChipCount()-stakes[1]);
        }
        //edge case, if the players stack size is less than the blind
        else {
            pot += players[bigBlind].getChipCount();
            players[bigBlind].setChipCount(0);
        }

        //small blind collection
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
    public void preFlopBetting () {
        //pre-flop betting starts at the player to the left of the big blind
        int currPlayer = (bigBlind+1) % players.length;
        //following two values are used to calculate min bet size, which will always be lastBet-secLastBet
        int lastBet = stakes[1];
        int secLastBet = 0;

        boolean actionOver = false;
        while (actionOver == false) {
            if(players[currPlayer] == null) continue;


        }
    }

    public void getHandVals() {
        for (int i = 0; i < players.length; i++) {
            if (players[i] != (null)) {
                players[i].getHand().getHandRanking();
            }
        }
    }

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
