package com.codelanx.aether.bots.cowkiller.UI;

import com.runemate.game.api.hybrid.util.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class CowInfoUI extends GridPane implements Initializable {

    private com.codelanx.aether.bots.cowkiller.CowKiller bot;

    @FXML
    Label uiMoneyGained, uiTime, uiHidesPH, uiGpPH, uiHideNumber;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setVisible(true);
    }

    // An object property is a container of an object, which can be added
    // listeners to. In this case the property contains our controller class
    // (this)

    public CowInfoUI(com.codelanx.aether.bots.cowkiller.CowKiller bot) {
        this.bot = bot;
        FXMLLoader loader = new FXMLLoader();
        Future<InputStream> stream = bot.getPlatform().invokeLater(() -> Resources.getAsStream("com/ethan0pia/bots/CowKiller/UI/CowUI.fxml"));
        loader.setController(this);
        // NOTE: By setting the root to (this) you must change your .fxml to reflect fx:root
        loader.setRoot(this);

        try {
            loader.load(stream.get());
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // This method will update the text that is presented to the end user
    public void update(int rate, int hideCount, String runTime, int value) {
        try {
            int money = hideCount * value;
            int gainRate = rate * value;
            uiHidesPH.textProperty().set("" + rate);
            uiHideNumber.textProperty().set("" + hideCount);
            uiTime.textProperty().set("" + runTime);
            uiMoneyGained.textProperty().set("" + money + "gp");
            uiGpPH.textProperty().set("" + gainRate + "gp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
