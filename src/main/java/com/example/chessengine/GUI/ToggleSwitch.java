package com.example.chessengine.GUI;

import com.example.chessengine.Board.Pieces.King;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

//Where this is based off of.
//https://gist.github.com/TheItachiUchiha/12e40a6f3af6e1eb6f75

/**
 * The ToggleSwitch class creates a toggle switch to be added to the page.
 */
public class ToggleSwitch extends StackPane {

    /**
     * The imageView object for the image for the toggle switch
     */
    private final ImageView imageView = new ImageView();
    /**
     * The Button object for the toggle switch
     */
    private final Button button = new Button();

    /**
     * The boolean property for if the toggle switch is on or off.
     */
    private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);

    /**
     * @return The boolean property for if the toggle switch is on or off.
     */
    public SimpleBooleanProperty switchOnProperty() { return switchedOn; }

    /**
     * The image of a white king to indicate the user is playing as white
     */
    Image white = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white king.png")));

    /**
     * The image of a black king to indicate the user is playing as black
     */
    Image black = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black king.png")));

    /**
     * Initialises the toggle switch, by setting the default state image, and events to  switch the state when clicked
     */
    private void init() {

        imageView.setImage(white);
        imageView.setPreserveRatio(true);

        getChildren().addAll(imageView, button);
        StackPane.setAlignment(imageView, Pos.CENTER_LEFT);
        StackPane.setAlignment(button, Pos.CENTER_RIGHT);

        button.setOnAction(e -> switchedOn.set(!switchedOn.get()));
        imageView.setOnMouseClicked(e -> switchedOn.set(!switchedOn.get()));

        setStyle();
        bindProperties();
    }

    /**
     * Set the base styling for the toggle switch
     */
    private void setStyle() {
        //Default Width
        setWidth(80);
        button.setStyle("-fx-background-radius: 0 100 100 0");
        setStyle("-fx-background-color: grey; -fx-background-radius: 100;");
        setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * Bind the layout of the imageView and button to this object, so they resize automatically
     */
    private void bindProperties() {
        imageView.fitWidthProperty().bind(widthProperty().divide(2));
        imageView.fitHeightProperty().bind(heightProperty());
        button.prefWidthProperty().bind(widthProperty().divide(2));
        button.prefHeightProperty().bind(heightProperty());
    }

    /**
     * Constructor for the ToggleSwitch, initialising.
     * Adds a listener to the boolean property, to switch the styling, when it changes state.
     */
    public ToggleSwitch() {
        init();
        switchedOn.addListener((a,b,c) -> {
            if (c) {
                imageView.setImage(black);
                StackPane.setAlignment(imageView, Pos.CENTER_RIGHT);
                button.setStyle("-fx-background-radius:  100 0 0 100 ");
                button.setTranslateX(-getWidth() / 2);
            }
            else {
                imageView.setImage(white);
                StackPane.setAlignment(imageView, Pos.CENTER_LEFT);
                button.setStyle("-fx-background-radius: 0 100 100 0");
                button.setTranslateX(0);
            }
        });
    }
}
