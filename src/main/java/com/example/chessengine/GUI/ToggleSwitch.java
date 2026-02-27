package com.example.chessengine.GUI;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

//Where this is based off of.
//https://gist.github.com/TheItachiUchiha/12e40a6f3af6e1eb6f75

/**
 * The ToggleSwitch class creates a toggle switch to be added to the page.
 */
public class ToggleSwitch extends HBox {

    /**
     * The Label object for the text for the toggle switch
     */
    private final Label label = new Label();
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
     * Initialises the toggle switch, by setting the default sate text, and adding
     */
    private void init() {

        label.setText("white");

        getChildren().addAll(label, button);
        button.setOnAction((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        label.setOnMouseClicked((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        setStyle();
        bindProperties();
    }

    /**
     * Set the base styling for the toggle switch
     */
    private void setStyle() {
        //Default Width
        setWidth(80);
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font(20));
        button.setStyle("-fx-background-radius: 0 100 100 0");
        setStyle("-fx-background-color: white; -fx-background-radius: 100;");
        setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * Bind the layout of the label and button to this object, so they resize automatically
     */
    private void bindProperties() {
        label.prefWidthProperty().bind(widthProperty().divide(2));
        label.prefHeightProperty().bind(heightProperty());
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
                label.setText("black");
                label.setTextFill(Color.WHITE);
                button.setStyle("-fx-background-radius:  100 0 0 100 ");
                setStyle("-fx-background-color: black; -fx-background-radius: 100;");
                label.toFront();
            }
            else {
                label.setText("white");
                label.setTextFill(Color.BLACK);
                button.setStyle("-fx-background-radius: 0 100 100 0");
                setStyle("-fx-background-color: white; -fx-background-radius: 100;");
                button.toFront();
            }
        });
    }
}
