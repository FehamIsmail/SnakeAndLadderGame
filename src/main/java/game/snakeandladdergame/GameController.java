//Controller of the JavaFXML  game
//Used to

package game.snakeandladdergame;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.CustomText;
import model.Player;

import java.util.ArrayList;

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

    public int retrieveNumberOfPlayers(){
        boolean retrieved = false;
        print("The game will begin...", "green");
        print("Enter the number of players (2 to 4)");
        while(!retrieved){
            pause();
            if(checkIsInteger(input) && Integer.parseInt(input) > 1 && Integer.parseInt(input) < 5){
                retrieved = true;
                return Integer.parseInt(input);
            }else{
                print("Please enter a number from 2 to 4", "#e80000");
            }
        }
        return 0;
    }

    public String retrieveInput(){
        return txtfield.getText();
    }

    public ArrayList<Player> getPlayers(){
        return game.getPlayers();
    }

    public void setStartButtonAction(ActionEvent actionEvent) {
        print("\nNow deciding which player will start playing:", "yellow");
        button_roll.setVisible(true);
        button_start.setVisible(false);
        button_start.setManaged(false);
        game.start();
    }
    public void setTextFieldAction(ActionEvent actionEvent) {
        input = txtfield.getText();
        print(input, "#70f5ff");
        txtfield.clear();
        if(isPaused) resume();
    }

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

        game = new LadderAndSnake(retrieveNumberOfPlayers(), this);
        print(("Game is played by " + (game.getPlayers().size()) + " players"), "green");
        retrievePlayerNames();

        button_start.setDisable(false);
    }

    public void pause() {
        isPaused = true;
        Platform.enterNestedEventLoop(PAUSE_KEY);
    }

    public void resume() {
        isPaused = false;
        Platform.exitNestedEventLoop(PAUSE_KEY, null);
    }
    public void print(String text, String color){
        txtflow.getChildren().add(new CustomText(text,color));
        txtflow.getChildren().add(new Text(System.lineSeparator()));
    }
    public void print(String text){
        txtflow.getChildren().add(new CustomText(text));
        txtflow.getChildren().add(new Text(System.lineSeparator()));
    }
    public void customPrintRolls(Player p){
        txtflow.getChildren().add(new CustomText("It's "));
        txtflow.getChildren().add(new CustomText(p.getName(),p.getColor()));
        txtflow.getChildren().add(new CustomText("'s turn to roll..."));
        txtflow.getChildren().add(new Text(System.lineSeparator()));
    }

    public void setRollButtonAction(ActionEvent actionEvent) {
        roll = game.flipDice();
        if(isPaused){
            resume();
        }
    }

    public static boolean checkIsInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }return true;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Object getPAUSE_KEY() {
        return PAUSE_KEY;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }



}