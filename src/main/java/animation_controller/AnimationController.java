package animation_controller;

import game.snakeandladdergame.LadderAndSnake;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.shape.*;
import javafx.util.Duration;
import model.Case;
import model.Coordinate;
import model.Player;

import java.util.ArrayList;

/**
 * Class used to control and animates the pawns
 * Also handles what happens after an animation finishes
 * @author Ismail Feham
 */
public class AnimationController {
     private LadderAndSnake game;
     private int shiftedX = 14;
     private int shiftedY = 16;

     public AnimationController(LadderAndSnake game) {
        this.game = game;
    }

    /**
     * Method used to animate a specific pawn
     * @param p Player object to animate
     * @param pawn the ImageView pawn, which belongs to the Player
     * @param start integer of the starting case to animate
     * @param end integer of the destination
     */
    public void animatePawn(Player p, ImageView pawn, int start, int end){
        game.setAnimating(true);
        int pos = start;
        if(start == 0){
            pos++;
            if(end == 1){
                p.move(1);
            }
        }
        int displacement = end - pos;
        SequentialTransition sequentialTransition = new SequentialTransition();
        ArrayList<PathTransition> paths = new ArrayList<>();
        boolean reverseOrder = false;
        for (int i = 0; i < displacement; i++) {
            if(p.getPosition() == 100)reverseOrder = true;
            if(reverseOrder) p.move(-1);
            else p.move(1);
            Path path = createPath(p, pawn, pos, reverseOrder);
            if(reverseOrder) pos--;
            else{pos++;}
            PathTransition pathTransition = createPathTransition(path,pawn);
            paths.add(pathTransition);
            if(pos==end){
                if(start == 0){
                    p.move(1);
                }
            }
        }
        if(paths.isEmpty()) game.getController().button_roll.setDisable(false);
        else {
            for (PathTransition p1 : paths) {
                sequentialTransition.getChildren().add(p1);
            }
            game.getController().button_roll.setDisable(true);
        }
        sequentialTransition.setOnFinished(e -> {
            if(game.checkIfThisPlayerIsFinished(p)){
                handleFinishedPlayer(p);
            }
            else if(!game.checkSpecialAnimation(p)){
                nextMove(p);
            }
        });
        sequentialTransition.play();
    }


    /**
     * Creates and returns a PathTransition object from a Path object.
     * Initializes and sets default values for this PathTransition
     * @param path Path object
     * @param pawn ImageView paww, which belongs to an object. It will be the node to animate.
     * @return PathTransition object
     */
    public PathTransition createPathTransition(Path path, ImageView pawn){
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(0.05));
        pathTransition.setPath(path);
        pathTransition.setNode(pawn);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);
        return pathTransition;
    }

    /**
     * Creates and returns a Path depending on its position and if the order has be reversed.
     * The order reverses if the Player crosses the last case (100). As such, when a player lands on the case 100, the
     * player will step back depending on the number of remaining rolls
     * @param p Player object
     * @param pawn ImageView pawn
     * @param position int starting position
     * @param reverseOrder boolean if the pawn's movement are to be reversed
     * @return Path object
     */
    public Path createPath(Player p, ImageView pawn, int position, boolean reverseOrder){
        boolean right = p.isRight();
        boolean up = p.isUp();
        if(reverseOrder) right = !right;
        Path path = new Path();
        int b = 100;
        ArcTo arc;
        Case c = game.findCase(position);
        Coordinate coordinate = c.getCoordinates();
        int x = coordinate.getX();
        int y = coordinate.getY();
        MoveTo move = createMoveTo(x, y);

        arc = new ArcTo();
        arc.setRadiusX(50);
        arc.setRadiusY(20);
        arc.setAbsolute(true);
        if(!up || position == 1 || (p.getPosition() == 100)){
            arc.setY(-(900-y)+shiftedY);
            if(!right) arc.setX(-b+x+shiftedX);
            else{arc.setX(b+x+shiftedX);}
            path.getElements().addAll(move, arc);
            return path;
        }else{
            LineTo line = new LineTo();
            line.setAbsolute(false);
            if(reverseOrder)line.setY(100);
            else line.setY(-100);
            line.setX(0);
            path.getElements().addAll(move, line);
            return path;
        }
    }

    /**
     * A special animation is an animation played if the player lands on a special case. Look Case class documentation
     * to know more about special cases. A special animation creates a custom PathTransition and plays it.
     * @param p Player object
     * @param destination int destination, Player's final position
     */
    public void specialAnimation(Player p, int destination){
        game.setAnimating(true);
        Case start = game.findCase(p.getPosition());
        Case destinationCase = game.findCase(destination);
        Coordinate destinationCoordinates = destinationCase.getCoordinates();
        int x1 = destinationCoordinates.getX();
        int y1 = destinationCoordinates.getY();
        Coordinate headCoordinates = start.getCoordinates();
        int x = headCoordinates.getX();
        int y = headCoordinates.getY();
        MoveTo move = createMoveTo(x,y);
        Path path = new Path();
        LineTo lineTo = new LineTo();
        lineTo.setX(x1-x);
        lineTo.setY(y1-y);
        lineTo.setAbsolute(false);
        path.getElements().addAll(move, lineTo);
        PathTransition pathTransition = createPathTransition(path, p.getPawn());
        int a = Math.abs(p.getPosition()-destination);
        pathTransition.setDuration(Duration.seconds(0.1*(Math.log(Math.abs(0.2*a+0.8)))));
        pathTransition.setOnFinished(e -> {
            if(game.checkIfThisPlayerIsFinished(p)){
                handleFinishedPlayer(p);
            }else{
                nextMove(p);
            }
        });
        game.getController().button_roll.setDisable(true);
        pathTransition.play();

        p.setAbsolutePosition(destination);
    }

    /**
     * Method executed if the player has finished in the winning case. Finishes the game if it is finished or continue
     * with the next player.
     * @param p Player object
     */
    private void handleFinishedPlayer(Player p){
        game.printFinishedPlayer(p);
        if(game.isFinished()) game.printEndingMessage();
        else{
            nextMove(p);
        }
    }

    /**
     * Code executed to roll for the next player. Prints whose turn is to roll and starts a new round if it is finished.
     * @param p Player object
     */
    private void nextMove(Player p){
//        if (game.getPlayers().indexOf(game.findNextPlayer(p)) == 0) {
        if (p.getOrder() > game.findNextPlayer(p).getOrder()) {
            Platform.runLater(() -> game.nextRound());
        }
        Platform.runLater(() -> game.getController().customPrintRolls(game.findNextPlayer(p)));;
        Platform.runLater(() -> game.getController().button_roll.setDisable(false));
    }

    /**
     * Creates and returns a MoveTo object used for normal and special animations.
     * @param x destination's case x coordinate
     * @param y destination's case y coordinate
     * @return MoveTo object
     */
    public MoveTo createMoveTo(int x, int y){
        MoveTo move;
        move = new MoveTo(x+shiftedX,-(900-y)+shiftedY);
        move.setAbsolute(true);
        return move;
    }
}
