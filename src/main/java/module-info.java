module game.snakeandladdergame {
    requires javafx.controls;
    requires javafx.fxml;


    opens game.snakeandladdergame to javafx.fxml;
    exports game.snakeandladdergame;
}