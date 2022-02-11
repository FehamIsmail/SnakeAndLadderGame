package game.snakeandladdergame;

import animation_controller.AnimationController;
import comparators.CompareByOrder;
import comparators.CompareByRoll;
import model.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class used to control the flow of the game. It is the class, which coordinates how the game will work.
 * @author Ismail Feham
 */
public class LadderAndSnake {

    private boolean playerHasBeenRemoved = false;
    private AnimationController animationController;
    private GameController controller;
    private int round = 1;
    private boolean isAnimating = false;
    private boolean isFinished = false;
    private boolean firstRoll = true;
    private int winningPosition = 1;
    private final int row = 10;
    private final int column = 10;
    Case[][] cases = new Case[row][column];
    Case startingCase = new Case();
    private int numberOfPlayers;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> winningPlayers = new ArrayList<>();
    private ArrayList<ArrayList<Player>> listOfDupedPlayers = new ArrayList<>();
    /**
     * String used to create ladders and snakes, seperated by commas, first number is the head, second is the tail. 'P' marks the end of the list.
     * For simplicity, XX is considered as 100;
     */
    private final String GAME_SEQUENCE = "01-38,04-14,09-31,16-06,21-42,28-84,36-44,48-30,51-67,64-60,71-91,79-19,80-XX,93-68,95-24,97-76,98-78P";

    /** LadderAndSnake's constructor
     * @param numberOfPlayers number of players (between 2 and 4)
     * @param controller GameController object
     */
    public LadderAndSnake(int numberOfPlayers, GameController controller) {
        this.controller = controller;
        this.numberOfPlayers = numberOfPlayers;
        this.animationController = new AnimationController(this);
        initializePlayers();
        initializeCases();
    }

    /**
     * Creates cases each assigned in the array 'cases'.
     * Cases are created and assigned in a 'S' pattern, which is how the players passes through the game board.
     * Adds all the players in the case (0)
     */
    public void initializeCases(){
        int counter = 0;
        for(int j = 0; j < (row); j++){
            if(j%2 == 0){
                for(int i = 0;i<(column);i++){
                    Case c = new Case();
                    cases[j][i]= c;
                    if(assignLaddersAndSnakes(c, counter)) counter++;
                }
            }else{
                for(int i = column-1;i>=0;i--){
                    Case c = new Case();
                    cases[j][i]= c;
                    if(assignLaddersAndSnakes(c, counter)) counter++;
                }
            }
        }
        startingCase.getListOfPlayers().addAll(players);
    }

    /**
     * Loops through the GAME_SEQUENCE String, if a starting position is equal to a case's position. The case is now
     * 'special'. Assigns a destination to each 'special' case. Check Case class documentation for more information
     * about special cases.
     * @param c Case object
     * @param counter int counter. Integer used to determine the ending condition of the while-loop
     * @return boolean checks if the case's position coincides with the starting position of the GAME_SEQUENCE.
     *         In other words, return true if the case is special.
     */
    private boolean assignLaddersAndSnakes(Case c, int counter) {
        int index = 0, head, tail;
        while (counter != 0) {
            if (GAME_SEQUENCE.charAt(index) == 'P') {
                return false;
            }
            if (GAME_SEQUENCE.charAt(index) == ',') {
                counter--;
            }
            index++;
        }
        head = Integer.parseInt(GAME_SEQUENCE.substring(index, index + 2));
        String tailString = GAME_SEQUENCE.substring(index + 3, index + 5);
        if (tailString.equals("XX")) {
            tail = 100;
        } else {
            tail = Integer.parseInt(tailString);
        }
        if (head == c.getPosition()) {
            c.setSpecial(true);
            c.setDestination(tail);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Initializes all the players.
     * Creates and places a player in the game's list of players and assigns a pawn to each player.
     */
    private void initializePlayers(){
        for(int i = 0;i<numberOfPlayers;i++){
            Player p = new Player();
            p.setPawn(controller.pawns[i]);
            controller.pawns[i].setVisible(true);
            players.add(p);
        }
    }

    /**
     * Rolls and returns a random value between 1-6 inclusively
     * @return the random value (int)
     */
    public int flipDice(){
        return (int)(Math.random()*30/5) + 1;
    }

    /**
     * Method used to start the game. Determines order and rolls for each pawn as long as the game is not finished.
     */
    public void start(){
        //Calls the method to determine the order of playing
        determineOrder();
        //Keep rolling pawns as long as the game is not finished
        while(!isFinished){
            if(round == 1 || playerHasBeenRemoved){
                controller.print("\nRound " + round + ": ", "green");
                playerHasBeenRemoved = false;
            }
            rollPawns();
            round++;
        }
        controller.button_roll.setDisable(true);
    }

    /**
     * Checks if this specific player has finished the game
     * @param p Player object
     */
    public void checkIfFinished(Player p) {
        if(p.getPosition() == 100) {
            p.setWinningPosition(winningPosition);
            players.remove(p);
            winningPlayers.add(p);
            p.getPawn().setVisible(false);

            if (winningPosition == numberOfPlayers) {
                isFinished = true;
                winningPlayers.add(players.get(0));
            }
        }
    }

    /**
     * Checks if all the players have finished the game
     * @return boolean, returns true if all the players have finished the game
     */
    public boolean checkIfFinished() {
        if (winningPosition == numberOfPlayers) {
            isFinished = true;
            System.out.println("Game is finished");
            return true;
        }
        return false;
    }

    /**
     * Prints a message once a player has finished the game
     * @param p Player object
     */
    public void printFinishedPlayer(Player p){
        if(p.getPosition() == 100) {
            controller.print(p.getName() + " has won " + getWinningPositionToString() + " place!", "orange");
            winningPosition++;
            if (winningPosition == numberOfPlayers) {
                controller.print(players.get(0).getName() + " has finished last place", "orange");
            }
        }
    }

    /**
     * Rolls and moves each pawn present in the game's list of players.
     * Note: the list of players will change everytime a player has finished the game.
     */
    public void rollPawns(){
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if(checkIfFinished()){
                System.out.println("finished, stop rolling");
                break;}
            if(playerHasBeenRemoved){
                while(players.indexOf(p)!=0){
                    controller.button_roll.setDisable(true);
                    rollForThisPlayer(p);
                    movePawn(p);
                }
                break;
            }
            Player lastPlayer = null;
            if(i == 0 && round == 2){
                lastPlayer = players.get(players.size()-1);
                controller.button_roll.setDisable(!(lastPlayer.getLastRoll() == 1 && lastPlayer.getPosition() == 38));
            }
            else if(i > 0 && round == 1){
                lastPlayer = players.get(i-1);
                controller.button_roll.setDisable(!(lastPlayer.getLastRoll() == 1 && lastPlayer.getPosition() == 38));
            }else{
                controller.button_roll.setDisable(true);
            }
            rollForThisPlayer(p);
            movePawn(p);
        }
    }

    /**
     * Updates the player's position, the case, and animates the pawn
     * @param p Player object
     */
    private void movePawn(Player p) {
        int start = p.getPosition();
        findCase(p.getPosition()).getListOfPlayers().remove(p);
        findCase(p.getPosition()).getListOfPlayers().add(p);
        animationController.animatePawn(p, p.getPawn(), start, start + p.getLastRoll());

    }

    /**
     * Checks if a player has landed on a special case
     * @param p Player object
     * @return true if the player has landed on a special case
     */
    public boolean checkSpecialAnimation(Player p){
        Case c = findCase(p.getPosition());
        p.getPosition();
        if(c.isSpecial()){
            animationController.specialAnimation(p, c.getDestination());
            return true;
        }return false;
    }

    //

    /**
     * Waits the roll button to activate and roll the dice for a specific player
     * @param p Player object
     */
    public void rollForThisPlayer(Player p){
        if(!checkIfFinished()) {
            if (firstRoll || !isOrderDetermined() || (players.indexOf(p) == 0 && round == 1)) {
                controller.customPrintRolls(p);
            }
            controller.setPaused(true);
            if (!isAnimating) {
                controller.button_roll.setDisable(false);
            }
            controller.pause();
            p.setLastRoll(controller.getRoll());
            controller.print((p.getName() + " has rolled " + p.getLastRoll()) + "\n", "#70f5ff");
        }

    }

    /**
     * Method used to roll for all players, int start and int end determines what position the players are competing
     * For ex: a list contains 2 players, rollForEachPlayer(list, 2, 3); means that these 2 players will compete for
     * the third and fourth place.
     * Note: Not to be confused with the method 'findDuplicates'. They both have the same parameters.
     * This method will call the method 'findDuplicates' to find the players who have rolled the same dice number.
     * @param list list of players. This parameter allows for this method to be recursive since a new lists can be
     *             created from this method. As such, these new lists can now be used as a parameter to call this method.
     * @param start the starting place the players are competing
     * @param end the ending place the players are competing
     */
    public void rollForEachPlayer(ArrayList<Player> list, int start, int end){
        if(!firstRoll && !isOrderDetermined()){
            controller.print("\nA tie was achieved between: ", "yellow");
            for(Player p : list){
                controller.print("- "+ p.getName());
            }
            controller.print("\n");
        }
        for(Player p : list){
            rollForThisPlayer(p);
        }
        if(!isOrderDetermined()){
            Collections.sort(list, new CompareByRoll());
            findDuplicates(list, start, end);
        }
    }

    /**
     * Retrieves the next player present in the game's player list
     * @param p Player object
     * @return the retrieve Player object
     */
    public Player findNextPlayer(Player p){
        int index = players.indexOf(p);
        if(index == players.size()-1){
            return players.get(0);
        }else{
            return players.get(index+1);
        }
    }

    /**
     * Retrieves a case given its position.
     * @param position int position of a case
     * @return the Case object
     */
    public Case findCase(int position){
        if(position == 0){
            return startingCase;
        }
        for(int j = 0; j < row; j++){
            for (int i = 0; i < column; i++) {
                if(cases[j][i].getPosition() == position){
                    return cases[j][i];
                }
            }
        }
        return null;
    }

    /**
     * Checks is the order is determined. The order is determined if each player's Integer order is non-null.
     * @return true if the order has been determined
     */
    public boolean isOrderDetermined(){
        boolean orderDetermined = true;
        for(Player p : players){
            if(p.getOrder() == null){
                orderDetermined = false;
            }
        }
        return orderDetermined;
    }

    //
    //

    /**
     * Method used to find the players who rolled the same and orders them.
     * Used to determine the order of each player
     * @param list list of players
     * @param start the starting place the players are competing
     * @param end the ending place the players are competing
     */
    public void findDuplicates(ArrayList<Player> list, int start, int end){
        Player previous = list.get(0);
        int previousRoll = 0;
        int currentRoll = 0;
        int order = start;
        for(int i = 1;i<list.size();i++){
            Player current = list.get(i);
            if(previous.equals(list.get(0)) && previous.compareRoll(current) != 0){
                previous.setOrder(start);
                order++;
            }
            if (current.compareRoll(previous) != 0) {
                if(i != list.size()-1){
                    if(current.compareRoll(list.get(i+1)) != 0){
                        current.setOrder(order);
                        order++;
                    }
                }else{
                    current.setOrder(order);
                }
            }
            if (current.compareRoll(previous) == 0) {
                currentRoll = current.getLastRoll();
                if(previousRoll == 0|| currentRoll != previousRoll){
                    ArrayList<Player> list2 = new ArrayList<>();
                    for (Player p : list) {
                        if (p.getLastRoll() == currentRoll) {
                            list2.add(p);
                            order++;
                        }
                    }
                    previousRoll = currentRoll;
                    listOfDupedPlayers.add(0, list2);
                }
            }
            previous = current;
        }
        firstRoll=false;
            if(!isOrderDetermined()){
                try {
                    for (ArrayList<Player> dupedList : listOfDupedPlayers) {
                        if (dupedList.get(0).getOrder() != null && isOrderDetermined()) {
                            break;
                        }
                        if (dupedList.get(0).getOrder() != null && !isOrderDetermined()) {
                            continue;
                        }
                        int localEnd = list.indexOf(dupedList.get(dupedList.size() - 1));
                        int localStart = list.indexOf(dupedList.get(0));
                        rollForEachPlayer(dupedList, localStart, localEnd);
                    }
                }catch(Exception e){
                    System.out.println("Nothing to worry. Expected error.");
                }
            }
        else{
            printOrder();
        }
    }

    //

    /**
     * Converts the winningPosition variable to a String and returns it
     * @return the converted String object
     */
    public String getWinningPositionToString(){
        String s = "";
        switch(winningPosition){
            case 1:
                s = "first";
                break;
            case 2:
                s = "second";
                break;
            case 3:
                s = "third";
                break;
        }
        return s;
    }

    /**
     * Prints the order of each player
     */
    public void printOrder(){
        sortByOrder();
        controller.print("\nReached final decision on order of playing: ", "yellow");
        int i = 1;
        for(Player p : players){
            controller.print(i + ". " + p.getName());
            i++;
        }
        controller.print("\n");
    }

    /**
     * Prints the ending message once every player but one has finished the game
     */
    public void printEndingMessage() {
        //Printing leaderboard
        controller.print("Leaderboard: ", "green");
        for (int i = 0; i < winningPlayers.size(); i++) {
            Player p = winningPlayers.get(i);
            controller.print(i+1 + ". " + p.getName());
        }
        //Farewell Message
        controller.print("\nThank you for playing!\n","orange");

    }

    /**
     * Sorts the players list by their order
     */
    public void sortByOrder(){
        Collections.sort(players, new CompareByOrder());
    }
    /**
     * Calls the rollForEachPlayer method
     */
    public void determineOrder(){
        //Rolls for all players
        rollForEachPlayer(players, 0, numberOfPlayers-1);
    }

    //Getters and setters
    public boolean isAnimating() {
        return isAnimating;
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getRound() {
        return round;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public GameController getController() {
        return controller;
    }
}

