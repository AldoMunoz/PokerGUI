import java.util.ArrayList;

public class Player {
    private String username;
    private int chipCount;
    private Card[] holeCards;
    //player's made hand
    private Hand hand;
    //checks to see if player is live in the hand
    private boolean inHand;
    //true when player is sitting
    private int currentBet;
    //player's current bet
    private boolean isActive;
    //TODO shot-clock and time bank

    public Player(String username, int chipTotal) {
        this.username = username; currentBet = 0;
    }

    //sets a players hand
    public void setHoleCards (Card[] cards) {
        this.holeCards = cards;
    }

    public void clearHoleCards () {
        this.holeCards = null;
    }

    public Card[] getHoleCards () {
        return holeCards;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public int getChipCount() {
        return chipCount;
    }

    public void setChipCount(int chipCount) {
        this.chipCount = chipCount;
    }
    public void setCurrentBet(int bet) {currentBet = bet;}
    public int getCurrentBet() {return currentBet;}

    public void setIsActive (boolean isActive) {
        this.isActive = isActive;
    }

    public void setInHand (boolean inHand) { this.inHand = inHand; }


}
