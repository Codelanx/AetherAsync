package com.codelanx.aether.construction.mission.branch.bank;

import com.codelanx.aether.common.CommonActions;
import com.codelanx.aether.construction.BasicBitchBot;
import com.codelanx.aether.construction.item.ConstructionMaterials;
import com.codelanx.aether.common.menu.dialog.DialogueIterator;
import com.codelanx.aether.common.menu.dialog.Speech;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent.Type;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;

import java.util.Objects;
import java.util.regex.Pattern;


//TODO: whole class
public class ButlerSpeakTask extends LeafTask {

    private static final Pattern FULL_INV = Pattern.compile("[A-Za-z]+, I have returned with .*retrieve\\. As I see thy inventory is full, .*");
    private static final Pattern PLANKS_RET = Pattern.compile("[A-Za-z]+, I have returned with .*retrieve\\.");
    private static final Pattern PAYMENT_DEMAND = Pattern.compile("[A-Za-z]+, if thou desirest my continued service.*");
    private final Speech delivery;
    private final Speech payment;
    private State speakingState = State.GETTING_DELIVERY;
    private int trips = -1;

    public static void main(String... args) {
        String smpl = "Mistress, I have returned with what you asked me to retrieve.";
        System.out.println(PLANKS_RET.matcher(smpl).matches());
    }

    private void setState(State state) {
        this.speakingState = state;
    }

    public ButlerSpeakTask() {
        this.delivery = Speech.builder().stepOption("Repeat last task?", list -> list.get(0)).stepContinue().build();
        this.payment = Speech.builder().stepContinue().stepOption(list -> list.get(0)).stepContinue().build();
    }

    public enum State {
        GETTING_DELIVERY,
        WAITING_DELIVERY,
        PAYMENT,
    }

    public State getSpeakingState() {
        return this.speakingState;
    }


    @Override
    public void execute() {
        Environment.getLogger().info("\t\t\t\t=>ButlerSpeakTask");
        Environment.getLogger().info("Beginning speak task");
        Environment.getLogger().info("speaking: " + this.speakingState.name());
        switch (this.speakingState) {
            case GETTING_DELIVERY:
                if (this.delivery.step()) {
                    Execution.delay(25);
                    if (DialogueIterator.isChatValid()) {
                        Environment.getLogger().info("Chat still valid - repeating last delivery step");
                        this.delivery.repeat();
                        return;
                    }
                    this.setState(State.WAITING_DELIVERY);
                    Environment.getLogger().info("Delivery request over");
                    this.trips += this.trips < 0 ? 0 : 1;
                    if (this.trips == 10) {
                        this.setState(State.PAYMENT);
                    }
                    this.delivery.reset();
                }
                return;
            case WAITING_DELIVERY:
                String str = DialogueIterator.getTextSafe();
                str = str == null ? null : str.replace("\n", "").replace('\r', ' ');
                Environment.getLogger().info("Testing match: '" + str + "'");
                if (str == null) {
                    if (Objects.equals(ChatDialog.getTitle(), "Repeat last task?")) {
                        this.delivery.reset();
                        this.setState(State.GETTING_DELIVERY);
                    }
                    Execution.delay(100);
                    return;
                }
                Environment.getLogger().info("PLANKS_RET match: " + PLANKS_RET.matcher(str).matches());
                Environment.getLogger().info("FULL_INV match: " + FULL_INV.matcher(str).matches());
                Environment.getLogger().info("PAYMENT_DEMAND match: " + PAYMENT_DEMAND.matcher(str).matches());
                if (PLANKS_RET.matcher(str).matches()) {
                    this.speakingState = State.GETTING_DELIVERY;
                    Environment.getLogger().info("Planks delivered");
                    BasicBitchBot.get().getInventory().update(ConstructionMaterials.OAK_PLANK, 24);
                    if (ChatDialog.getContinue() != null) {
                        ChatDialog.getContinue().select(); //only do this for a normal delivery
                    }
                } else if (FULL_INV.matcher(str).matches()) {
                    Environment.getLogger().info("Called a butler with a non-empty inventory");
                    CommonActions.END.getTask().execute();
                } else if (PAYMENT_DEMAND.matcher(str).matches()) {
                    this.speakingState = State.PAYMENT;
                }
                break;
            case PAYMENT:
                if (this.payment.step()) {
                    Environment.getLogger().info("Payment given to butler");
                    this.trips = 0;
                    this.payment.reset();
                    this.speakingState = State.WAITING_DELIVERY;
                }
                break;
        }
        Environment.getLogger().info("Butler engaged after speech - planks delivered and/or payment demand");

        //do a check to see if the butler spawned but cannot reach the player

    }

    public static Npc findButler() {
        LocatableEntityQueryResults<Npc> butlers = Npcs.newQuery().names("Demon butler").results();
        return butlers.isEmpty() ? null : butlers.iterator().next();
    }

    public static boolean isButlerSpeaking() {
        String title = ChatDialog.getTitle();
        if (title == null) {
            InterfaceComponent comp = Interfaces.newQuery().containers(231).types(Type.LABEL).visible().results().first();
            title = comp == null ? null : comp.getText();
        }
        return "Demon butler".equalsIgnoreCase(title);
    }

    //private static final Pattern FETCH_FROM_BANK = Pattern.compile("Fetch from bank: (?<amount>\\d+) x (?<type>.*)");


        /*
        if (this.dialogue == null) {
            this.dialogue = new DialogueIterator();
        }
        if (this.dialogue.hasNext() && !this.dialogue.isSameResult()) {
            Dialogue d = this.dialogue.next();
            Environment.getLogger().info("Found Dialogue: " + d);
            if (d.isContinue()) {
                Environment.getLogger().info("Hitting continue");
                d.getContinue().select();
                Execution.delay(200);
                return;
                //continue;
            }
            Environment.getLogger().info("Selecting first option");
            Option o = d.getOptionsList().get(0);
            o.select();
            Execution.delay(200);
        }*/
        /*Option o;
        do {
            o = ChatDialog.getOption(0);
            if (o == null || !o.getText().startsWith("Fetch from bank")) {
                Environment.getLogger().info("Delaying, no viable text option yet | #get(0)");
                Environment.getLogger().info(o);
                Environment.getLogger().info(o == null ? "null" : o.getText());
                Execution.delay(200);
            }
            o = ChatDialog.getOption(FETCH_FROM_BANK);
            if (o == null || !o.getText().startsWith("Fetch from bank")) {
                Environment.getLogger().info("Delaying, no viable text option yet | #get(0)");
                Environment.getLogger().info(o);
                Environment.getLogger().info(o == null ? "null" : o.getText());
                Execution.delay(200);
            }
        } while (o == null || !o.getText().startsWith("Fetch from bank"));
        o.select();
        Execution.delay(200 + (long) (ThreadLocalRandom.current().nextGaussian() * 55));
        Continue c = null;
        while (c == null) {
            c = ChatDialog.getContinue();
            if (c == null) {
                Environment.getLogger().info("Delaying, no viable continue option yet");
                Execution.delay(200);
            }
        }
        c.select();
        ChatDialog.getContinue().select();
        Execution.delay(1200 + (long) (ThreadLocalRandom.current().nextGaussian() + 700));*/




        /*
        //BLERGHGHGH think of something else
        Map<String, Runnable> tasks = new HashMap<>(5);
        //hardcode, :\
        tasks.put("Repeat last task?", () -> {
            ChatDialog.getOption(0).select();
        });
        tasks.put("Select an Option", () -> {
            ChatDialog.getOption(1).select();
        });
        tasks.put("Select an Option", () -> {

        });
        List<Runnable> str = Arrays.asList(
                () -> {

                },
                () -> {

                });
        str.stream().map(r -> new LeafTask() {
            @Override
            public void execute() {
                r.run();
            }
        }).forEach(responses::add);
        responses.add(new LeafTask() {
            @Override
            public void execute() {

            }
        });*/
}
