package animation_controller;

import game.snakeandladdergame.LadderAndSnake;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.ImageView;
import javafx.scene.shape.*;
import javafx.util.Duration;
import model.Case;
import model.Coordinate;
import model.Player;

import java.util.ArrayList;

public class AnimationController {
     private LadderAndSnake game;
     private int shiftedX = 14;
     private int shiftedY = 16;

     public AnimationController(LadderAndSnake game) {
        this.game = game;
    }

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
        for(PathTransition p1 : paths){
            sequentialTransition.getChildren().add(p1);
        }
        sequentialTransition.setOnFinished(e -> {
            game.checkIfFinished(p);
            game.printFinishedPlayer(p);
            if(!game.isFinished()){
                game.getController().button_roll.setDisable(false);
            }else{
                game.printEndingMessage();
            }
            if(!game.checkSpecialAnimation(p)){
                if(game.getPlayers().indexOf(game.findNextPlayer(p)) == 0){
                    game.getController().print("\nRound " + game.getRound() + ": ", "green");
                }
                if(!game.isFinished()){
                    game.getController().customPrintRolls(game.findNextPlayer(p));
                }
            }
        });
        sequentialTransition.play();

    }


    public PathTransition createPathTransition(Path path, ImageView pawn){
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(0.6));
        pathTransition.setPath(path);
        pathTransition.setNode(pawn);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);
        return pathTransition;
    }


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
        MoveTo move = createMoveTo(shiftedX, shiftedY, x, y);

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
        MoveTo move = createMoveTo(shiftedX, shiftedY, x,y);
        Path path = new Path();
        LineTo lineTo = new LineTo();
        lineTo.setX(x1-x);
        lineTo.setY(y1-y);
        lineTo.setAbsolute(false);
        path.getElements().addAll(move, lineTo);
        PathTransition pathTransition = createPathTransition(path, p.getPawn());
        int a = Math.abs(p.getPosition()-destination);
        pathTransition.setDuration(Duration.seconds(Math.log(Math.abs(0.2*a+0.8))));
        if(p.getPosition() != 1){
            game.getController().button_roll.setDisable(true);
        }
        pathTransition.setOnFinished(e -> {
            game.checkIfFinished(p);
            game.printFinishedPlayer(p);
            if(!game.isFinished()){
                game.getController().button_roll.setDisable(false);
            }else{
                game.printEndingMessage();
            }
            if(game.getPlayers().indexOf(game.findNextPlayer(p)) == 0){
                if(!game.isFinished()){
                    game.getController().print("\nRound " + game.getRound() + ": ", "green");
                }
            }
            game.getController().customPrintRolls(game.findNextPlayer(p));
        });
        pathTransition.play();

        p.setAbsolutePosition(destination);
    }
    public MoveTo createMoveTo(int shiftedX, int shiftedY, int x, int y){
        MoveTo move;
        move = new MoveTo(x+shiftedX,-(900-y)+shiftedY);
        move.setAbsolute(true);
        return move;
    }
}
