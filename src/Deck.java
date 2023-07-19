import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

public class Deck {
    private ArrayList<Card> cards;
    private ArrayList<Card> deadCards;

    //adds the cards to the deck
    //initiates deck list and dead cards list
    public Deck() {
        cards = new ArrayList<>(EnumSet.allOf(Card.class));
        deadCards = new ArrayList<>();
    }

    //removes the card from the deck and moves it to the "dead" deck
    public Card drawCard() {
        deadCards.add(cards.get(0));
        return cards.remove(0);
    }

    //shuffle the contents of cards ArrayList in random order
    public void shuffleCards () {
        Collections.shuffle(cards);
    }

    //joins the deck with the deadCards after hand is over
    public void joinDeck () {
        cards.addAll(deadCards);
        deadCards.removeAll(cards);
    }
}
