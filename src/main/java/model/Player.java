package model;

import javafx.scene.image.ImageView;

import java.util.Objects;

public class Player{


    private ImageView pawn;  //Pawn's image
    private int position;
    private int winningPosition;
    private int lastRoll;
    private Integer order; //Variable used to know the order of a player
    private String name;
    //variables used to determine the direction of the pawn
    private boolean up = false;
    private boolean right = true;
    private String color;
    private static int counter = 1;
    private int id;

    public Player() {
        this.position = 0;
        this.id = counter;
        counter++;
    }

    public void move(int n) {
        setPosition(n);
    }

    private void determineDirection(){
        up = ((this.position-1)%10 == 0);
        double a = (this.position-1)/10;
        int firstDigit = (int)a;
        right = firstDigit%2 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    public int compareRoll(Player p2) {
        if (this.lastRoll < p2.lastRoll) {
            return 1;
        }
        if (this.lastRoll > p2.lastRoll) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setAbsolutePosition(int position){
            this.position = position;
            determineDirection();
    }

    @Override
    public String toString() {
        return "Player{" + "name= " + name + " position=" + position + ", id=" + id + ", lastRoll=" + lastRoll + '}';
    }

    //Getters and setters
    //Sets position and updates direction
    public void setPosition(int position) {
        this.position += position;
        determineDirection();
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
