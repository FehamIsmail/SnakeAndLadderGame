
//Custom comparator class used to sort players depending on their order

package comparators;

import model.Player;

import java.util.Comparator;

public class CompareByOrder implements Comparator<Player> {
    @Override
    public int compare(Player o1, Player o2) {
        return o1.getOrder().compareTo(o2.getOrder());
    }
}
