public class Player {
    String name;
    String[] hand;
    Player next;
    Integer score;

    public Player(String name, String[] hand) {
        this.name = name;
        this.hand = hand;
    }

    public Player(String name, String[] hand, Player next, Integer score) {
        this.name = name;
        this.hand = hand;
        this.next = next;
        this.score = score;
    }
}