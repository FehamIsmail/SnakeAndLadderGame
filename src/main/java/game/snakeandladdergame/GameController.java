
package game.snakeandladdergame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import model.CustomText;
import model.Player;

import java.util.ArrayList;

/**
 * Controller of the JavaFXML  game
 * Used to implement handlers of different buttons and controls
 * Handles and controls the GUI side of the game
 * @author Ismail Feham
 */
public class GameController {
    public Button button_play, button_roll, button_start; //Buttons
    public Pane gameboard; //Pane which contains the image of the game board
    public TextField txtfield; //Container to retrieve the user's inputs
    public TextFlow txtflow; //Right-hand side console log
    public VBox vbox; //Contains the buttons, textfield, and textflow
    public BorderPane borderpane; //The pane which holds all the other panes
    public ScrollPane scrollpane;
    public ImageView img_player_1, img_player_2, img_player_3, img_player_4;
    public ImageView[] pawns;

    private String input;
    private final Object PAUSE_KEY = new Object();
    private boolean isPaused = false;
    private int roll;

    private LadderAndSnake game;

    /**
     * This method is used to set the name of players inputed by the user
     * It also checks for invalid inputs
     */
    private void retrievePlayerNames() {
        boolean retrieved = false;
        for (int i = 0; i <getPlayers().size(); i++) {
            print(("Enter Player "+ (i+1)+ "'s name:"));
            while(!retrieved){
                pause();
                if(input.length() > 12){
                    print("Please enter a name with less than 12 characters", "red");
                    continue;
                }
                if(input.length() < 3){
                    print("Please enter a name with more than 2 characters", "red");
                    continue;
                }
                boolean isEqual = false;
                for(Player p : game.getPlayers()){
                    if(p.getName() != null){
                        if(p.getName().equals(input)){
                            print("This name was already used by another player", "red");
                            isEqual = true;
                        }
                    }
                }
                if(isEqual) continue;
                retrieved = true;
            }
            getPlayers().get(i).setName(input);
            retrieved = false;
        }
        setNameColors();
        txtfield.setDisable(true);
    }

    /**
     * Sets the colors of the players depending on the order of their names entered.
     */
    public void setNameColors(){
        String color = "";
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player p = game.getPlayers().get(i);
            switch(i) {
                case 0:
                    color = "#fb201c";
                    break;
                case 1:
                    color = "#3091d3";
                    break;
                case 2:
                    color = "#4aab29";
                    break;
                case 3:
                    color = "#ae3297";
                    break;
            }
            p.setColor(color);
        }
    }

    /**
     * Retrieves and returns the number of players
     * Checks for invalid inputs. Records the invalid inputs and warns the user at his third attempt.
     * If the players has entered an invalid input four times. The program will exit.
     * @return the number of players
     */
    public int retrieveNumberOfPlayers(){
        int counter = 1;
        boolean retrieved = false;
        print("The game will begin...", "green");
        print("Enter the number of players (2 to 4)");
        try{
            while(!retrieved){
                pause();
                if(checkIsInteger(input) && Integer.parseInt(input) > 1 && Integer.parseInt(input) < 5){
                    retrieved = true;
                    return Integer.parseInt(input);
                }else{
                    if(counter == 4){
                        print("\nYou have entered an invalid number 4 times","#e80000");
                        print("The program will now exit in 3 seconds\n","#e80000");
                        txtfield.setDisable(true);
                        button_start.setDisable(true);
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> Platform.exit()));
                        timeline.play();

                    }else if(counter == 3){
                        print("Bad attempt (" + counter + ") Please enter a number from 2 to 4\nThis is your last attempt!", "#e80000");
                        counter++;
                    }else {
                        print("Bad attempt (" + counter + ") Please enter a number from 2 to 4", "#e80000");
                        counter++;
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("The Program has exited");
        }
        return 0;
    }

    /**
     * Retrieves the input entered by the user
     * @return the text in the text field as a String object
     */
    public String retrieveInput(){
        return txtfield.getText();
    }

    /**
     * Method executed when the start button is activated
     * Starts the game
     */
    public void setStartButtonAction(ActionEvent actionEvent) {
        print("\nNow deciding which player will start playing:", "yellow");
        button_roll.setVisible(true);
        button_start.setVisible(false);
        button_start.setManaged(false);
        game.start();
    }

    /**
     * Method executed when the user preses 'enter' while in the text field
     * Retrieves the user's input
     */
    public void setTextFieldAction(ActionEvent actionEvent) {
        input = retrieveInput();
        print(input, "#70f5ff");
        txtfield.clear();
        if(isPaused) resume();
    }


    /**
     * Method executed when the start button is activated
     * Initialize buttons, panes, and the game
     * Retrieves the number of players in the game
     */
    public void setPlayButtonAction(ActionEvent actionEvent) {
        //Initializing buttons, panes, and the game
        pawns = new ImageView[] {img_player_1, img_player_2, img_player_3, img_player_4};
        txtfield.setDisable(false);

        button_play.setVisible(false);
        button_play.setManaged(false);

        button_start.setVisible(true);
        button_start.setManaged(true);
        button_start.setDisable(true);

        button_roll.managedProperty().bindBidirectional(button_roll.visibleProperty());
        button_roll.setDisable(true);
        scrollpane.vvalueProperty().bind(txtflow.heightProperty());

        //Retrieves and initializes the class LadderAndSnake
        int numberOfPlayers = retrieveNumberOfPlayers();
        boolean onlyOneWinner;
        if(numberOfPlayers == 2) onlyOneWinner = true;
        else{
            onlyOneWinner = retrieveGameMode();
        }
        game = new LadderAndSnake(numberOfPlayers, this, onlyOneWinner);
        print(("Game is played by " + (game.getPlayers().size()) + " players"), "green");
        retrievePlayerNames();

        button_start.setDisable(false);
    }

    /**
     * There will be two game modes available to the players:
     *  - One winner only, meaning as soon as one player finishes the reaches the end of the game, the game will terminate.
     *  - Multiple winners, as the name suggests, there can be multiple winners. Note: If the game is played with only 2
     *  players. The game will automatically start with 'one winner only' game mode.
     * @return boolean, true if one winner only, false for multiple winners
     */
    public boolean retrieveGameMode(){

        print("Do you wish to have one only one winner or multiple winners?\n" +
                "Press (1) for one winner only\n" +
                "Press (2) for multiple winners");

        while(true){
            pause();
            if(input.equals("1"))return true;
            if(input.equals("2"))return false;
            else{
                print("\nYou have entered an invalid input", "#e80000");
                print("Press (1) for one winner only\n" + "Press (2) for multiple winners");
            }
        }

    }

    /**
     * Method executed when the start button is activated
     * It rolls the dice and unpauses the game if it was paused
     */
    public void setRollButtonAction(ActionEvent actionEvent) {
        roll = game.flipDice();
        if(isPaused){
            resume();
        }
    }
    /**
     * Pauses the game.
     * Used to wait for the user to input a value before executing the rest of the code
     * Helpful to roll the dice of players and to retrieve their names.
     */
    public void pause() {
        isPaused = true;
        Platform.enterNestedEventLoop(PAUSE_KEY);
    }

    /**
     * Resumes the game
     * Used when the user has entered an input and executes the rest of the code
     */
    public void resume() {
        isPaused = false;
        Platform.exitNestedEventLoop(PAUSE_KEY, null);
    }

    /**
     * Prints a text on the TextFlow
     *
     * @param text text as a String object to display in the TextFlow
     * @param color color of the text. The color can the actual color for ex: "blue," "green," and "yellow"
     *              The color can also be a hex value of the format "#AAAAAA"
     */
    public void print(String text, String color){
        txtflow.getChildren().add(new CustomText(text,color));
        txtflow.getChildren().add(new Text(System.lineSeparator()));
    }

    /**
     * Same method as print(String text). However, the color is set to "white" by default
     * @param text text as a String object to display in the TextFlow
     */
    public void print(String text){
        txtflow.getChildren().add(new CustomText(text));
        txtflow.getChildren().add(new Text(System.lineSeparator()));
    }

    /**
     * Method used to print the player's time to roll. It also changes the color of the player for better visibility.
     * @param p Player object.
     */
    public void customPrintRolls(Player p){
        txtflow.getChildren().add(new CustomText("It's "));
        txtflow.getChildren().add(new CustomText(p.getName(),p.getColor()));
        txtflow.getChildren().add(new CustomText("'s turn to roll..."));
        txtflow.getChildren().add(new Text(System.lineSeparator()));
    }

    /**
     * Method used to determine if a String can be converted to an Integer using Integer.parseInt(String s)
     * Tries Integer.parseInt, if it fails, returns false, otherwise, return true.
     * @param input user's input as a String object
     * @return whether the input is an integer or not. (boolean)
     */
    public static boolean checkIsInteger(String input) {
        try {
            Integer.parseInt(input);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }return true;
    }

    /**
     * Retrieves the list of players from the LadderAndSnake class
     * @return list of players
     */
    public ArrayList<Player> getPlayers(){
        return game.getPlayers();
    }

    /**
     * Retrieves the rolled dice number
     * @return integer between 1 and 6 inclusively
     */
    public int getRoll() {
        return roll;
    }

}