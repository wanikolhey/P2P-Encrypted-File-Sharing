package com.kolhey.p2p.gui.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Animation utilities for smooth transitions and effects
 */
public class AnimationUtils {
    
    /**
     * Create a fade-in animation
     */
    public static FadeTransition fadeIn(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        return fadeTransition;
    }
    
    /**
     * Create a fade-out animation
     */
    public static FadeTransition fadeOut(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), node);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        return fadeTransition;
    }
    
    /**
     * Create a scale-up animation
     */
    public static ScaleTransition scaleUp(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), node);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        return scaleTransition;
    }
    
    /**
     * Create a slide-in animation from left
     */
    public static TranslateTransition slideInFromLeft(Node node) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), node);
        transition.setFromX(-100);
        transition.setToX(0);
        return transition;
    }
    
    /**
     * Create a slide-out animation to right
     */
    public static TranslateTransition slideOutToRight(Node node) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), node);
        transition.setFromX(0);
        transition.setToX(100);
        return transition;
    }
    
    /**
     * Create a bounce animation
     */
    public static ScaleTransition bounce(Node node) {
        ScaleTransition bounce = new ScaleTransition(Duration.millis(100), node);
        bounce.setFromX(1.0);
        bounce.setFromY(1.0);
        bounce.setToX(1.1);
        bounce.setToY(1.1);
        
        ScaleTransition bounceback = new ScaleTransition(Duration.millis(100), node);
        bounceback.setFromX(1.1);
        bounceback.setFromY(1.1);
        bounceback.setToX(1.0);
        bounceback.setToY(1.0);
        
        bounce.setOnFinished(event -> bounceback.play());
        
        return bounce;
    }
}
