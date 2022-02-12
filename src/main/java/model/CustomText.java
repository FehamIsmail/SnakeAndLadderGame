
package model;

import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

/**
 * Custom Text class, extending Text class
 * Used to print texts in right-hand side of the game (TextFlow)
 * @author Ismail Feham
 */
public class CustomText extends Text {
    public CustomText(String s) {
        super(s);
        this.setFill(Paint.valueOf("white"));
    }

    public CustomText(String s, String color) {
        super(s);
        this.setFill(Paint.valueOf(color));
    }

}
