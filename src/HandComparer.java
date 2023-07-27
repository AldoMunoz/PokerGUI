import java.util.ArrayList;

public class HandComparer {
    private ArrayList<Player> players;

    public HandComparer(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Player> findBestHand() {
        ArrayList<Player> winner = null;
        HandRanks handRank = players.get(0).getHand().getHandRanking();

        //can't have 2 Royals, always chopped pot
        if(handRank == HandRanks.ROYAL_FLUSH) return players;
        else if (handRank == HandRanks.STRAIGHT_FLUSH) winner = compareStraightFlush();
        else if (handRank == HandRanks.FOUR_OF_A_KIND) winner = compareFourOfAKind();
        else if (handRank == HandRanks.FULL_HOUSE) winner =compareFullHouse();
        else if (handRank == HandRanks.FLUSH) winner = compareFlush();
        else if (handRank == HandRanks.STRAIGHT) winner = compareStraight();
        else if (handRank == HandRanks.THREE_OF_A_KIND) winner = compareThreeOfAKind();
        else if (handRank == HandRanks.TWO_PAIR) winner = compareTwoPair();
        else if (handRank == HandRanks.ONE_PAIR) winner = compareOnePair();
        else if (handRank == HandRanks.HIGH_CARD) winner = compareHighCard();
        return winner;
    }

    //TODO make sure that for all these methods, it works for >2 players, not just 2 players

    //TODO go card by card and see who has the highest five cards
    private ArrayList<Player> compareHighCard() {
        return null;
    }

    //TODO see who has the better one pair, if not compare the next 3 high cards
    private ArrayList<Player> compareOnePair() {
        return null;
    }

    //TODO compare each pair, then compare the 5th high card
    private ArrayList<Player> compareTwoPair() {
        return null;
    }

    //TODO compare 3 of a kind, then check 2 high cards
    private ArrayList<Player> compareThreeOfAKind() {
        return null;
    }

    //TODO just check the first card to see who has the higher straight
    private ArrayList<Player> compareStraight() {
        return null;
    }

    //TODO just check the first card to see who has higher flush
    private ArrayList<Player> compareFlush() {
        return null;
    }

    //TODO compare trips, then compare the pair
    private ArrayList<Player> compareFullHouse() {
        return null;
    }

    //TODO Compare the first card value of the quad, then compare the 5th high card
    private ArrayList<Player> compareFourOfAKind() {
        return null;
    }

    //TODO just compare first card of straight flush
    private ArrayList<Player> compareStraightFlush() {
        return null;
    }
}
