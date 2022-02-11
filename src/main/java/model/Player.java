package model;

import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Class used to create Players.
 * Players are the participants of this game\
 * @author Ismail Feham
 */
public class Player{


    private ImageView pawn;  //Pawn's image
    private int position;
    private int winningPosition = 0;
    private int lastRoll;
    private Integer order; //Variable used to know the order of a player
    private String name;
    //variables used to determine the direction of the pawn
    private boolean up = false;
    private boolean right = true;
    private String color;
    private static int counter = 1;
    private int id;

    /**
     * Player constructor
     * player's id is incremented every time a player is created
     */
    public Player() {
        this.position = 0;
        this.id = counter;
        counter++;
    }

    /**
     * Updates the player's position
     * @param n
     */
    public void move(int n) {
        setPosition(n);
    }

    /**
     * Determines the direction of the player, which depends on its position.
     */
    private void determineDirection(){
        up = ((this.position-1)%10 == 0);
        double a = (this.position-1)/10;
        int firstDigit = (int)a;
        right = firstDigit%2 == 0;
    }


    /**
     * @param o Object o, which will be compared to this player.
     *          Object o will get explicitly cast to a Player object
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    /**
     * Compares this player to another player depending on their last roll.
     * @param p Player object
     * @return the result of the comparison. 0 if they are equal. Returns 1 or -1 if they are not equal.
     */
    public int compareRoll(Player p) {
        if (this.lastRoll < p.lastRoll) {
            return 1;
        }
        if (this.lastRoll > p.lastRoll) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Sets position and updates direction
     */
    public void setPosition(int position) {
        this.position += position;
        determineDirection();
    }

    /**
     * Method similar to setPosition. However instead of incrementing the position with the integer passed as a parameter,
     * the player's position is directly equal to this integer.
     * @param position integer position to set as position
     */
    public void setAbsolutePosition(int position){
            this.position = position;
            determineDirection();
    }

    @Override
    public String toString() {
        return "Player{" + "name= " + name + " position=" + position + ", id=" + id + ", lastRoll=" + lastRoll + '}';
    }

    public int getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    public void setLastRoll(int lastRoll) {
        this.lastRoll = lastRoll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public ImageView getPawn() {
        return pawn;
    }

    public void setPawn(ImageView pawn) {
        this.pawn = pawn;
    }

    public int getWinningPosition() {
        return winningPosition;
    }

    public void setWinningPosition(int winningPosition) {
        this.winningPosition = winningPosition;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isRight() {
        return right;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
