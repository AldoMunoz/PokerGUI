import java.util.ArrayList;
//TODO should methods and vars be private or public?
//TODO should we use an Array or ArrayList?


public class Main {
    public static void main(String[] args) {
        Player player1 = new Player("john",45810);
        Player player2 = new Player("adam", 25000);
        Player player3 = new Player("jack", 50000);
        Table table1 = new Table('C', 6, new int[]{5, 10}, "NLH");

        table1.addPlayer(player1, 0, 10000);
        table1.addPlayer(player2, 1, 10000);
        table1.addPlayer(player3, 3, 10000);

        table1.runGame();
    }
}
