import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class App {

    static String[] startupShuffle(String[] tiles) {
        List<String> stringList = Arrays.asList(tiles);
		Collections.shuffle(stringList);
		stringList.toArray(tiles);
        System.out.println("\nTiles shuffled");
        return tiles;
    }

    static String pickJoker(String[] tilesLocal) {
        int jokerIndex, rank;
        String joker = "", color = "";
        jokerIndex = (int)(Math.random() * tilesLocal.length);
        while (tilesLocal[jokerIndex].equals("sahte-okey")) {
            jokerIndex = (int)(Math.random() * tilesLocal.length);
        }
        rank = Integer.parseInt(tilesLocal[jokerIndex].split("-")[1]);
        rank = rank == 13 ? 1 : rank + 1;
        color = tilesLocal[jokerIndex].split("-")[0];
        System.out.println("\nTile picked: " + tilesLocal[jokerIndex]);
        System.out.println("Joker is " + color + "-" + rank);
        joker = color + "-" + rank;
        return joker;
    }

    static ArrayList<Player> deal(String[] tilesShuffled) {
        Player p1 = new Player("Player 1", null);
        Player p2 = new Player("Player 2", null);
        Player p3 = new Player("Player 3", null);
        Player p4 = new Player("Player 4", null);
        p1.next = p2; p2.next = p3; p3.next = p4; p4.next = p1;
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(p1); players.add(p2); players.add(p3); players.add(p4);

        int randomPlayerIndex = (int)(Math.random() * 4);
        System.out.println("\nDealing player: " + players.get(randomPlayerIndex).name + "\n");
        players.get(randomPlayerIndex).hand = Arrays.copyOfRange(tilesShuffled, 0, 15);
        Player dealingPlayer = players.remove(randomPlayerIndex);
        players.clear();
        players.add(dealingPlayer);

        int tilesFrom = 15;
        int tilesTo = 29;
        String[] hand;
        Player nextPlayer = dealingPlayer.next;
        for (int i = 0; i < 3; i++) {
            hand = new String[14];
            hand = Arrays.copyOfRange(tilesShuffled, tilesFrom, tilesTo);
            tilesFrom = tilesFrom + 14;
            tilesTo = tilesTo + 14;
            nextPlayer.hand = hand;
            players.add(nextPlayer);
            nextPlayer = nextPlayer.next;
        }
        return players;
    }

    static String[] orderHand(String[] hand, String joker) {
        String[] playerHand = hand;

        for (int i = 0; i < hand.length; i++) {
            for (int j = i + 1; j < hand.length; j++) {
                String tmpTile;
                Boolean isFakeJoker = playerHand[i].split("-")[1].equals("okey");
                Boolean isNextFakeJoker = playerHand[j].split("-")[1].equals("okey");
                int jokerRank = Integer.parseInt(joker.split("-")[1]);
                int tileRank = isFakeJoker ? jokerRank : Integer.parseInt(playerHand[i].split("-")[1]);
                int nextTileRank = isNextFakeJoker ? jokerRank : Integer.parseInt(playerHand[j].split("-")[1]);
                if (tileRank > nextTileRank) {
                    tmpTile = playerHand[i];
                    playerHand[i] = playerHand[j];
                    playerHand[j] = tmpTile;
                }
            }
        }
        coloredPrintHand(playerHand);
        return playerHand;
    }

    static void coloredPrintHand(String[] hand) {
        String[] coloredHand = new String[hand.length];
        final String RESET = "\u001B[0m";
        final String BLUE = "\u001B[34m";
        final String YELLOW = "\u001B[33m";
        final String BLACK = "\u001B[30m";
        final String RED = "\u001B[31m";
        String tileColor;
        for (int i = 0; i < hand.length; i++) {
            if (!hand[i].equals("sahte-okey")) {
                tileColor = hand[i].split("-")[0];
                switch (tileColor) {
                    case "mavi":
                        coloredHand[i] = BLUE + hand[i] + RESET;
                        break;
                    case "sarı":
                        coloredHand[i] = YELLOW + hand[i] + RESET;
                        break;
                    case "kırmızı":
                        coloredHand[i] = RED + hand[i] + RESET;
                        break;
                    case "siyah":
                        coloredHand[i] = BLACK + hand[i] + RESET;
                        break;
                    default:
                        break;
                }
            } else {
                coloredHand[i] = hand[i];
            }
        }
        System.out.println(Arrays.toString(coloredHand));
    }

    static Map<String, ArrayList> groupTilesByColor(String[] hand, String joker) {
        System.out.println();
        ArrayList<String> yellows = new ArrayList<String>();
        ArrayList<String> blues = new ArrayList<String>();
        ArrayList<String> blacks = new ArrayList<String>();
        ArrayList<String> reds = new ArrayList<String>();
        Map<String, ArrayList> group = new HashMap<String, ArrayList>();
        String itemColor = "";
        for (String handItem : hand) {
            Boolean isFakeOkey = handItem.equals("sahte-okey");
            itemColor = isFakeOkey ? joker.split("-")[0] : handItem.split("-")[0];
            if(isFakeOkey) handItem = joker;                    // To count value of sahte-okey
            switch (itemColor) {
                case "sarı":
                    yellows.add(handItem);
                    break;
                case "mavi":
                    blues.add(handItem);
                    break;
                case "siyah":
                    blacks.add(handItem);
                    break;
                case "kırmızı":
                    reds.add(handItem);
                    break;
                default:
                    break;
            }
        }
        group.put("yellows", yellows);
        group.put("blues", blues);
        group.put("blacks", blacks);
        group.put("reds", reds);
        return group;
    }

    static Map<String, Integer[]> checkSeries(ArrayList yellows, ArrayList blues, ArrayList blacks, ArrayList reds) {
        Map<String, ArrayList> colorGroups = new HashMap<String, ArrayList>();
        colorGroups.put("Yellows", yellows);
        colorGroups.put("Blues", blues);
        colorGroups.put("Blacks", blacks);
        colorGroups.put("Reds", reds);
        Map<String, Integer[]> seriesCount = new HashMap<String, Integer[]>();
        Integer tripleSeriesCount = 0, quadSeriesCount = 0, fiveOrMoreSeriesCount = 0;
        Integer[] incrementValues;
        Integer seriesCounter = 0;
        for (Map.Entry<String, ArrayList> colorGroup : colorGroups.entrySet()) {                        // For every color
            incrementValues = new Integer[colorGroup.getValue().size()];
            seriesCounter = 0; tripleSeriesCount = 0; quadSeriesCount = 0; fiveOrMoreSeriesCount = 0;
            for (int i = 0; i < colorGroup.getValue().size() - 1; i++) {                                // For every tile of that color
                String rankNext = colorGroup.getValue().get(i + 1).toString().split("-")[1];
                String rank = colorGroup.getValue().get(i).toString().split("-")[1];
                incrementValues[i] = Integer.parseInt(rankNext) - Integer.parseInt(rank);
                if (incrementValues[i] == 1) seriesCounter = seriesCounter + 1;
                else if (incrementValues[i] > 1) seriesCounter = 0;
                if (seriesCounter == 2 && incrementValues[i] != 0) {
                    tripleSeriesCount++;
                }
                else if (seriesCounter == 3 && incrementValues[i] != 0) {
                    quadSeriesCount++;
                    tripleSeriesCount = 0;
                }
                else if (seriesCounter > 3) {
                    fiveOrMoreSeriesCount++;
                    quadSeriesCount = 0;
                }
            }
            seriesCount.put(colorGroup.getKey(), new Integer[] { tripleSeriesCount, quadSeriesCount, fiveOrMoreSeriesCount });
            // System.out.println("incrementValues: " + Arrays.toString(incrementValues));
            // System.out.println("seriesCounter: " + seriesCounter + ", tripleSeriesCount: " + tripleSeriesCount + ", quadSeriesCount: " + quadSeriesCount);
        }
        return seriesCount;
    }

    static int[] checkGroups(String[] hand, String joker) {
        int tripleGroupCount = 0;
        int quadGroupCount = 0;
        for (int i = 1; i < 14; i++) {
            ArrayList<String> rankColors = new ArrayList<String>();
            String tileColor = "";
            String tileRank = "0";
            for (String tile : hand) {
                Boolean isFakeOkey = tile.equals("sahte-okey");
                tileColor = isFakeOkey ? joker.split("-")[0] : tile.split("-")[0];
                tileRank = isFakeOkey ? joker.split("-")[1] : tile.split("-")[1];
                if (Integer.parseInt(tileRank) == i) {
                    if (!rankColors.contains(tileColor)) rankColors.add(tileColor);
                }
            }
            if (rankColors.size() == 3) {
                tripleGroupCount++;
            } else if (rankColors.size() == 4) {
                quadGroupCount++;
            }
        }
        return (new int[] { tripleGroupCount, quadGroupCount });
    }

    static int calculateHandScore (Map<String, Integer[]> series, int[] groups) {
        int score = 0;
        for (Map.Entry<String, Integer[]> serie : series.entrySet()) {
            score = score + (serie.getValue()[0] * 1) + (serie.getValue()[1] * 2) + (serie.getValue()[2] * 3);
        }
        score = score + (groups[0] * 1) + (groups[1] * 2); 
        return score;
    }

    static String findMaxScoredPlayer (ArrayList<Player> players) {
        int[] scores = new int[] { players.get(0).score, players.get(1).score, players.get(2).score, players.get(3).score };
        int maxScore = scores[0];
        int maxIndex = 0;
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxIndex = i;
            }
        }
        return players.get(maxIndex).name;
    }

    public static void main(String[] args) throws Exception {
        String jokerTile;
        ArrayList<Player> players = new ArrayList<Player>();
        String[] tiles = {
            "sarı-1", "sarı-2", "sarı-3", "sarı-4", "sarı-5", "sarı-6", "sarı-7", "sarı-8", "sarı-9", "sarı-10", "sarı-11", "sarı-12", "sarı-13",
            "mavi-1", "mavi-2", "mavi-3", "mavi-4", "mavi-5", "mavi-6", "mavi-7", "mavi-8", "mavi-9", "mavi-10", "mavi-11", "mavi-12", "mavi-13",
            "siyah-1", "siyah-2", "siyah-3", "siyah-4", "siyah-5", "siyah-6", "siyah-7", "siyah-8", "siyah-9", "siyah-10", "siyah-11", "siyah-12", "siyah-13",
            "kırmızı-1", "kırmızı-2", "kırmızı-3", "kırmızı-4", "kırmızı-5", "kırmızı-6", "kırmızı-7", "kırmızı-8", "kırmızı-9", "kırmızı-10", "kırmızı-11", "kırmızı-12", "kırmızı-13",
            "sahte-okey",
            "sarı-1", "sarı-2", "sarı-3", "sarı-4", "sarı-5", "sarı-6", "sarı-7", "sarı-8", "sarı-9", "sarı-10", "sarı-11", "sarı-12", "sarı-13",
            "mavi-1", "mavi-2", "mavi-3", "mavi-4", "mavi-5", "mavi-6", "mavi-7", "mavi-8", "mavi-9", "mavi-10", "mavi-11", "mavi-12", "mavi-13",
            "siyah-1", "siyah-2", "siyah-3", "siyah-4", "siyah-5", "siyah-6", "siyah-7", "siyah-8", "siyah-9", "siyah-10", "siyah-11", "siyah-12", "siyah-13",
            "kırmızı-1", "kırmızı-2", "kırmızı-3", "kırmızı-4", "kırmızı-5", "kırmızı-6", "kırmızı-7", "kırmızı-8", "kırmızı-9", "kırmızı-10", "kırmızı-11", "kırmızı-12", "kırmızı-13",
            "sahte-okey"
        };
        Map<String, ArrayList> colorGroups = new HashMap<String, ArrayList>();
        Map<String, Integer[]> seriesCounts = new HashMap<String, Integer[]>();
        int[] groupCounts;
        tiles = startupShuffle(tiles);
        jokerTile = pickJoker(tiles);
        players = deal(tiles);
        for (Player player : players) {
            System.out.println("\n-------------------- " + player.name + "--------------------");
            player.hand = orderHand(player.hand, jokerTile);
            colorGroups = groupTilesByColor(player.hand, jokerTile);
            seriesCounts = checkSeries(colorGroups.get("yellows"), colorGroups.get("blues"), colorGroups.get("blacks"), colorGroups.get("reds"));
            groupCounts = checkGroups(player.hand, jokerTile);
            player.score = calculateHandScore(seriesCounts, groupCounts);
        }
        System.out.println("\n\u001B[32m" + "Closest hand to win: " + findMaxScoredPlayer(players) + "\u001B[0m");
        System.out.println("\n");
    }
}
