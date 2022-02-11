
//Custom comparator class used to sort players depending on their last roll

package comparators;

import model.Player;

import java.util.Comparator;

public class CompareByRoll implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        if (p1.getLastRoll() < p2.getLastRoll()) {
            return 1;
        }
        if (p1.getLastRoll() > p2.getLastRoll()) {
            return -1;
        } else {
            return 0;
        }
    }
}
