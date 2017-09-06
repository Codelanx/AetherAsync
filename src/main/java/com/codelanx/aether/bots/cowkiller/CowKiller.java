package com.codelanx.aether.bots.cowkiller;

import com.codelanx.aether.bots.cowkiller.UI.CowInfoUI;
import com.codelanx.aether.bots.cowkiller.branches.InventoryFull;
import com.codelanx.aether.common.bot.AsyncBot;
import com.codelanx.aether.common.bot.mission.AetherMission;
import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.net.GrandExchange;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.script.framework.core.LoopingThread;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.concurrent.TimeUnit;

//Not for release, this is a conversion of a bot from ethan0pia (used for testing)
//previously: cowKiller
public class CowKiller extends AsyncBot implements InventoryListener, EmbeddableUI {

    //TODO: You're reusing a lot of values here which mismatches other ones (ported from other classes into here)
    //private static final Area COW_AREA = new Area.Rectangular(new Coordinate(2881,3493,0), new Coordinate(2890,3481,0));
    //private static final Area COW_AREA = new Area.Rectangular(new Coordinate(2882,3491,0), new Coordinate(2889,3483,0));
    //private static final Area COW_AREA = new Area.Rectangular(new Coordinate(2877,3497,0), new Coordinate(2892,3479,0));
    //private static final Area BANK_AREA = new Area.Rectangular(new Coordinate(2884,3538,0), new Coordinate(2891,3533,0));
    //private static final Area JACK_AREA = new Area.Rectangular(new Coordinate(2886,3503,0), new Coordinate(2890,3499,0));
    public static final Area COW_AREA = new Area.Rectangular(new Coordinate(2877, 3497, 0), new Coordinate(2892, 3479, 0));
    public static final Area BANK_AREA = new Area.Rectangular(new Coordinate(2886, 3539, 0), new Coordinate(2891, 3534, 0));
    public static final Area JACK_AREA = new Area.Rectangular(new Coordinate(2886, 3504, 0), new Coordinate(2891, 3499, 0));

    private int leatherPrice = GrandExchange.lookup(1741).getPrice();
    private int hideCount = 0;

    private CowInfoUI infoUI;
    private SimpleObjectProperty<Node> botInterfaceProperty;

    private StopWatch stopWatch = new StopWatch();

    public CowKiller() {
        hideCount = 0;
        updateInfo();
        // Set this class as the EmbeddableUI
        setEmbeddableUI(this);
    }

    @Override
    public ObjectProperty<? extends Node> botInterfaceProperty() {
        if (botInterfaceProperty == null) {
            // Initializing configUI in this manor is known as Lazy Instantiation
            botInterfaceProperty = new SimpleObjectProperty<>(infoUI = new CowInfoUI(this));
        }
        return botInterfaceProperty;
    }

    @Override
    public void onStart(String... args) {
        stopWatch.start();
        this.getBrain().getLogicTree().register(AetherMission.of(new InventoryFull()));
        new LoopingThread(() -> Platform.runLater(this::updateInfo), 1000).start();
        // Add this class as a listener for the Event Dispatcher
        getEventDispatcher().addListener(this);
    }

    @Override
    public void onItemAdded(ItemEvent event) {
        ItemDefinition definition = event.getItem().getDefinition();
        if (definition != null && definition.getName().contains("Cowhide")) {
            hideCount++;
        }
    }

    // This method is used to update the GUI thread from the bot thread
    private void updateInfo() {
        try {
            // Assign all values to a new instance of the Info class
            int rate = (int) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), hideCount);
            String runTime = stopWatch.getRuntimeAsString();

            if (runTime != null && infoUI != null) {
                infoUI.update(rate, hideCount, runTime, leatherPrice);
            }
            // Assign all values to a new instance of the Info class
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}