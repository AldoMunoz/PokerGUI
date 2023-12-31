import java.util.*;

//Hand is all about finding the poker hand an individual has.
//Hand will take in the player's hole cards and the community cards to find the handRanking
//the actual 5 card hand will also be determined here
public class Hand {
    //hole cards
    private Card[] playerCards;
    //communityCards
    private ArrayList<Card> communityCards;
    //enum with the hand strength, x High to Royal
    private HandRanks handRanking;
    //5 cards that make up the players hand
    private Card[] fiveCardHand;

    //a board and a hand are passed when creating a new hand
    public Hand(Card[] playerCards, ArrayList<Card> communityCards) {
        this.playerCards = playerCards;
        this.communityCards = communityCards;
        this.fiveCardHand = new Card[5];
    }

    //will return the HandRanks enum of the players hand
    public HandRanks getHandRanking() {
        //Creates array of cards, adds player cards and community cards to the array
        Card[] cards = new Card[playerCards.length+communityCards.size()];
        cards[0] = playerCards[0];
        cards[1] = playerCards[1];
        for (int i = 2; i < communityCards.size()+2; i++) {
            cards[i] = communityCards.get(i-2);
        }

        //Sorts the cards from Hi->Lo
        reverseInsertionSort(cards);

        //Goes through hand ranks from hi->lo to find the best possible hand
        if (isRoyalFlush(cards)) {
            handRanking = HandRanks.ROYAL_FLUSH;
            return handRanking;
        }
        else if(isStraightFlush(cards)) {
            handRanking = HandRanks.STRAIGHT_FLUSH;
            return handRanking;
        }
        else if (isQuads(cards)) {
            handRanking = HandRanks.FOUR_OF_A_KIND;
            return handRanking;
        }
        else if (isFullHouse(cards)) {
            handRanking = HandRanks.FULL_HOUSE;
            return handRanking;
        }
        else if (isFlush(cards)) {
            handRanking = HandRanks.FLUSH;
            return handRanking;
        }
        else if (isStraight(cards)) {
            handRanking = HandRanks.STRAIGHT;
            return handRanking;
        }
        else if (isThreeOfAKind(cards)) {
            handRanking = HandRanks.THREE_OF_A_KIND;
            return handRanking;
        }
        else if(isTwoPair(cards)) {
            handRanking = HandRanks.TWO_PAIR;
            return handRanking;
        }
        else if (isPair(cards)) {
            handRanking = HandRanks.ONE_PAIR;
            return handRanking;
        }
        else {
            isHighCard(cards);
            handRanking = HandRanks.HIGH_CARD;
            return handRanking;
        }
    }

    //Sorts the cards using insertion sort in descending order
    private void reverseInsertionSort (Card[] cards) {
        for (int i = 1; i < cards.length; i++) {
            Card key = cards[i];
            int j = i - 1;

            // Shift elements to the left until the correct position for the key is found
            while (j >= 0 && cards[j].ordinal() < key.ordinal()) {
                cards[j + 1] = cards[j];
                j--;
            }
            cards[j + 1] = key;
        }
    }

    private boolean isRoyalFlush(Card[] cards) {
        //checks is a flush exists, and if the last card of that flush is a 10
        if(isFlush(cards) && getFiveCardHand()[4].getVal() == 10) return true;
        return false;
     }

    private boolean isStraightFlush (Card[] cards) {
        //first check to see if flush exists, and designates target suit.
        //this is done to solve ordinal problems
        int clubs = 0;
        int spades = 0;
        int hearts = 0;
        int diamonds = 0;
        //targetSuit = 'x' acts as null
        char targetSuit = 'x';

        //for loop will count occurrences of a suit, break when minimum of 5 is reached
        for (int i = 0; i < cards.length; i++) {
            if (cards[i].getSuit() == 's') {
                if (spades == 4) {
                    targetSuit = 's';
                    break;
                }
                spades++;
            }
            else if (cards[i].getSuit() == 'c') {
                if (clubs == 4) {
                    targetSuit = 'c';
                    break;
                }
                clubs++;
            }
            else if (cards[i].getSuit() == 'h') {
                if (hearts == 4) {
                    targetSuit = 'h';
                    break;
                }
                hearts++;
            }
            else if (cards[i].getSuit() == 'd') {
                if (diamonds == 4) {
                    targetSuit = 'd';
                    break;
                }
                diamonds++;
            }
        }

        //if there is no flush, then we can return false here
        if(targetSuit == 'x') return false;

        //if not, array is made to return straight flush if found
        Card[] strFlush = new Card[5];
        int count = 0;

        for (int i = 0; i < cards.length; i++) {
            //if the current card is not the target suit, we will ignore this card
            if(cards[i].getSuit() != targetSuit) continue;

            //find starting (highest) value card in the straight flush
            if(count == 0) {
              strFlush[count] = cards[i];
              count++;
            }
            //checks if the current card is one value lower than the previous one
            else if (cards[i].getVal()+1 == cards[count-1].getVal()) {
                count++;
                strFlush[count] = cards[i];
            }
            //if not, count is reset, current card is the new highest card
            else {
                count = 0;
                strFlush = new Card[5];
                strFlush[count] = cards[i];
            }

            //if count reaches 4, then the straight flush is complete
            if(count == 4) {
                setFiveCardHand(strFlush);
                return true;
            }
        }

        //edge case, checks for straight flush wheel
        if(count == 3 && cards[3].getVal() == 2) {
            for (int i = 0; i < 4; i++) {
                if(cards[i].getVal() == 14 && cards[i].getSuit() == targetSuit) {
                    strFlush[4] = cards[i];
                    setFiveCardHand(strFlush);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isQuads(Card[] cards) {
        int count = 1;
        int currVal = cards[0].getVal();
        Card[] quads = new Card[5];

        for (int i = 1; i < cards.length; i++) {
            //first if case is when quads are found
            if(cards[i].getVal() == currVal && count == 3) {
                quads[0] = cards[i-count];
                quads[1] = cards[i-count+1];
                quads[2] = cards[i-count+2];
                quads[3] = cards[i];
                //following conditions find the high card (will either be first card or next card after quads
                if(cards[0].getVal() != currVal) quads[4] = cards[0];
                else quads[4] = cards[i+1];
                setFiveCardHand(quads);
                return true;
            }
            //increases count if same value card is found
            else if(cards[i].getVal() == currVal && count < 3) count++;
            //current card is not the same as previous, count is reset
            else {
                currVal = cards[i].getVal();
                count = 1;
            }
        }
        return false;
    }

    private boolean isFullHouse(Card[] cards) {
        //check if three of a kind exists
        if(isThreeOfAKind(cards));
        else return false;

        int tripVal = getFiveCardHand()[0].getVal();
        int pairVal = 0;
        //looks for a pair
        for (int i = 0; i < cards.length; i++) {
            //when we encounter the trip value, we skip them by adding 2+1 to i
            if (cards[i].getVal() == tripVal) i += 2;
            //finds the first non-trip value card
            else if (pairVal == 0) {
                pairVal = cards[i].getVal();
            }
            //if a pair is found, they are made the 4th and 5th cards of the hand and returned true
            else if(cards[i].getVal() == pairVal) {
                changeFiveCardHandVal(3, cards[i-1]);
                changeFiveCardHandVal(4, cards[i]);
                return true;
            }
            //if the pair card does not match the current card, the pair card is set to the current card
            else {
                pairVal = cards[i].getVal();
            }
        }
        return false;
    }

    private boolean isFlush (Card[] cards) {
        //arrayList is used since the size of the list is unknown and changing
        ArrayList<Card> sFlush = new ArrayList<>();
        ArrayList<Card> cFlush = new ArrayList<>();
        ArrayList<Card> hFlush = new ArrayList<>();
        ArrayList<Card> dFlush = new ArrayList<>();
        Card[] ans = new Card[0];

        //counts each suit, returns true when 5 of the same suit is found
        for (Card card : cards) {
            if (card.getSuit() == 's') {
                sFlush.add(card);
                if (sFlush.size() == 5) {
                    setFiveCardHand(sFlush.toArray(ans));
                    return true;
                }
            } else if (card.getSuit() == 'c') {
                cFlush.add(card);
                if (cFlush.size() == 5) {
                    setFiveCardHand(cFlush.toArray(ans));
                    return true;
                }
            } else if (card.getSuit() == 'h') {
                hFlush.add(card);
                if (hFlush.size() == 5) {
                    setFiveCardHand(hFlush.toArray(ans));
                    return true;
                }
            } else {
                dFlush.add(card);
                if (dFlush.size() == 5) {
                    setFiveCardHand(dFlush.toArray(ans));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isStraight(Card[] cards) {
        int count = 1;
        Card[] straight = new Card[5];
        straight[0] = cards[0];

        for (int i = 1; i < cards.length; i++) {
            //if the current card is equal to the previous, we will ignore this card
            if(cards[i].getVal() == cards[i-1].getVal()) continue;
            //checks if the current card is one less than the previous one
            else if(cards[i].getVal()+1 == cards[i-1].getVal()) {
                straight[count] = cards[i];
                count++;
                //5 cards in a row have been found
                if(count == 5) {
                    setFiveCardHand(straight);
                    return true;
                }
            }
            //if thd current card is not one less than the previous, the array is reinitialized and count is set back to 1
            else {
                straight = new Card[5];
                straight[0] = cards[i];
                count = 1;
            }
        }

        //edge case, checks for wheel
        if(count == 4 && cards[0].getVal() == 14 && cards[cards.length-1].getVal() == 2) {
            straight[count] = cards[0];
            setFiveCardHand(straight);
            return true;
        }
        return false;
    }

    private boolean isThreeOfAKind(Card[] cards) {
        int count = 1;
        int tripVal = cards[0].getVal();

        //looks for the first instance of three of the same value
        for (int i = 1; i < cards.length; i++) {
            //when found, the three of a kind is added to the first 3 positions of final hand
            if(cards[i].getVal() == tripVal && count == 2) {
                Card[] threeOfAKind = new Card[5];
                threeOfAKind[0] = cards[i-2];
                threeOfAKind[1] = cards[i-1];
                threeOfAKind[2] = cards[i];

                //the last two positions, which will be high cards, are determined by iterating the list again
                int pos = 3;
                for (int j = 0; pos < 5; j++) {
                    //when we encounter the trip value, we skip them by adding 2+1 to j
                    if (cards[j].getVal() == tripVal) j += 2;
                    //if the current value isn't the trip value, it gets added to the five card hand
                    else {
                        threeOfAKind[pos] = cards[j];
                        pos++;
                    }
                }
                setFiveCardHand(threeOfAKind);
                return true;
            }
            //this finds a pair
            else if(cards[i].getVal() == tripVal && count == 1) count++;
            //if the next card is not equal to the previous, a new tripVal is set
            else {
                tripVal = cards[i].getVal();
                count = 1;
            }
        }
        return false;
    }

    private boolean isTwoPair(Card[] cards) {
        int pairCount = 0;
        int currVal = cards[0].getVal();
        Card[] twoPair = new Card[5];

        for (int i = 1; i < cards.length; i++) {
            //finds the first pair, adds them to final hand
            if(cards[i].getVal() == currVal && pairCount == 0) {
                twoPair[0] = cards[i-1];
                twoPair[1] = cards[i];
                pairCount++;
            }
            //finds the second pair, adds them to final hand
            else if (cards[i].getVal() == currVal && pairCount == 1) {
                twoPair[2] = cards[i-1];
                twoPair[3] = cards[i];
                pairCount++;
                break;
            }
            else currVal = cards[i].getVal();
        }

        //adds the 5th high card if 2 pair is found
        if (pairCount == 2) {
            for (int i = 0; i < cards.length; i++) {
                if(cards[i].getVal() != twoPair[0].getVal() && cards[i].getVal() != twoPair[2].getVal()) {
                    twoPair[4] = cards[i];
                    setFiveCardHand(twoPair);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPair(Card[] cards) {
        int currVal = cards[0].getVal();

        for (int i = 1; i < cards.length; i++) {
            //looks for the first instance of a matching card
            if(cards[i].getVal() == currVal) {
                Card[] pair = new Card[5];
                pair[0] = cards[i-1];
                pair[1] = cards[i];

                //fills in the hand with 3 high cards
                int pos = 2;
                for (int j = 0; pos < 5; j++) {
                    if(cards[j].getVal() == currVal) j++;
                    else {
                        pair[pos] = cards[j];
                        pos++;
                    }
                }
                setFiveCardHand(pair);
                return true;
            }
            else currVal = cards[i].getVal();
        }
        return false;
    }

    //returns the 5 highest cards
    private void isHighCard(Card[] cards) {
        fiveCardHand = Arrays.copyOfRange(cards, 0, 5);
    }

    private void setFiveCardHand (Card[] cards) {
        this.fiveCardHand = cards;
    }

    private Card[] getFiveCardHand () {
        return fiveCardHand;
    }

    //used for isFullHouse, might get rid of later
    private void changeFiveCardHandVal(int pos, Card card) {
        fiveCardHand[pos] = card;
    }

    public void printFiveCardHand() {
        for (int i = 0; i < fiveCardHand.length; i++) {
            System.out.println(fiveCardHand[i]);
        }
        System.out.println();
    }
}


