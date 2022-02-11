
package model;

import java.util.ArrayList;

/**
 * Case class used to define the squares on the game board.
 * @author Ismail Feham
 */
public class Case {
    //Boolean used to determine if this case is a head/tail of a ladder/snake
    //A case is flagged as "special" if the case has a destination, which means it is the head of a ladder/snake
    private boolean isSpecial;
    private int destination;

    ArrayList<Player> listOfPlayers = new ArrayList<>();
    private int position;
    private static int counter = 0;

    /**
     * Case constructor
     * Each case's position is incremented after each object creation.
     */
    public Case() {
        this.position = counter;
        counter++;
    }

    /**
     * Retrieves the absolute coordinates of this case
     * @return Coordinate object
     */
    public Coordinate getCoordinates(){
        //Digits read from left to right, ex: 10 => firstDigit = 1, secondDigit = 0
        int firstDigit = getFirstDigit();
        int secondDigit = position - (firstDigit*10);
        int x,y;
        if(secondDigit == 0){
            y = (10 - firstDigit)*100;
            if(firstDigit%2 == 0){
                x = 0;
            }else{
                x = 900;
            }
        }else{
            y = (9 - firstDigit)*100;
            if(firstDigit%2 == 0){
                x = 100*(secondDigit-1);
            }else{
                x = 1000 - 100*(secondDigit);
            }
        }
        return new Coordinate(x,y);
    }

    /**
     * Retrieves the first digit of this case's position.
     * The first digit is the digit on the left of a two-digit number
     * @return
     */
    public int getFirstDigit(){
        double a = position/10;
        return (int)a;
    }

    @Override
    public String toString() {
        return "Case (" + position + ')';
    }

    //Getters and setters
    public ArrayList<Player> getListOfPlayers() {
        return listOfPlayers;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getDestination() {
        return destination;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
