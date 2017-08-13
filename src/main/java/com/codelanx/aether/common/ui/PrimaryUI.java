package com.codelanx.aether.common.ui;

import com.runemate.game.api.script.framework.AbstractBot;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryUI extends GridPane implements Initializable {

    public PrimaryUI(AbstractBot bot) {
        /*
        // Load the fxml file using RuneMate's resources class.
        //FXMLLoader loader = new FXMLLoader();

        // Input your InfoUI FXML file location here.
        // NOTE: DO NOT FORGET TO ADD IT TO MANIFEST AS A RESOURCE
        //Future<InputStream> stream = bot.getPlatform().invokeLater(() -> Resources.getAsStream("com/sudo/v3/spectre/bots/exampleflaxpicker/ui/InfoUI.fxml"));



        // Set this class as root AND Controller for the Java FX GUI
        loader.setController(this);

        // NOTE: By setting the root to (this) you must change your .fxml to reflect fx:root
        loader.setRoot(this);

        try {
            loader.load(stream.get());
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    public void refresh() {
        Platform.runLater(this::update);
    }

    public void update() {
        //update javafx elements
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setHgap(15);
        this.setVgap(15);
        this.setPadding(new Insets(25, 25, 25, 25));

        //Text

        this.setVisible(true);
    }
}
