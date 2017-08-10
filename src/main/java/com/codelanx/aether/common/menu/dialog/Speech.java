package com.codelanx.aether.common.menu.dialog;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.Option;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Speech {

    private final Interactable src;
    private final List<Predicate<Dialogue>> actions;
    private DialogueIterator itr = new DialogueIterator();
    private int index = 0;

    private Speech(Interactable src, List<Predicate<Dialogue>> actions) {
        this.src = src;
        this.actions = actions;
    }

    //true if complete, false otherwise
    public boolean step() {
        if (!this.itr.hasNext()) {
            return true;
        }
        if (this.index >= this.actions.size()) {
            return true;
        }
        if (!this.itr.isSameResult()) {
            Dialogue next = this.itr.next();
            Environment.getLogger().info("next dialogue: " + next);
            if (!this.actions.get(this.index++).test(next)) {
                this.index--;
            }
        }
        return this.index >= this.actions.size();
    }

    public void restart() {
        this.reset();
        if (this.src != null && this.src.isVisible()) {
            this.src.interact("Talk-to"); //TODO: verify option with all src, maybe need to supply
        }
    }

    public void reset() {
        this.index = 0;
        this.itr = new DialogueIterator();
    }

    public void repeat() {
        this.index -= this.index > 0 ? 1 : 0;
        this.itr = new DialogueIterator();
    }

    public static SpeechBuilder builder() {
        return new SpeechBuilder(null);
    }

    public static SpeechBuilder builder(Interactable src) {
        return new SpeechBuilder(src);
    }

    public static class SpeechBuilder {

        private List<Predicate<Dialogue>> built = new LinkedList<>();
        private final Interactable src;

        public SpeechBuilder(Interactable src) {
            this.src = src;
        }

        public SpeechBuilder step(Predicate<Dialogue> action) {
            return this.step("", action);
        }

        //null == no title, "" == any title, rest equals string match to title
        public SpeechBuilder step(String title, Predicate<Dialogue> action) {
            this.built.add(title != null && title.isEmpty() ? action : d -> {
                return Objects.equals(d.getTitle(), title) && action.test(d);
            });
            return this;
        }

        public SpeechBuilder stepOption(Function<List<Option>, Option> selector) {
            return this.stepOption("", selector);
        }

        public SpeechBuilder stepOption(String title, Function<List<Option>, Option> selector) {
            return this.step(title, d -> {
                Environment.getLogger().info("SpeechBuilder#stepOption$0:");
                List<Option> lis = d.getOptionsList();
                Environment.getLogger().info("\tlis: " + lis);
                Option sel = selector.apply(lis);
                Environment.getLogger().info("\tsel: " + sel);
                Boolean back = sel.select();
                Environment.getLogger().info("\tback: " + back);
                return back;
            });
        }

        public SpeechBuilder stepContinue() {
            return this.stepContinue("");
        }

        public SpeechBuilder stepContinue(String title) {
            return this.step(title, d -> d.getContinue().select());
        }

        public Speech build() {
            return new Speech(this.src, this.built);
        }
    }
}
