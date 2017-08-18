package com.codelanx.aether.common.menu.dialog;

import com.codelanx.aether.common.input.UserInput;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.Option;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Speech {

    private final Supplier<Interactable> src;
    private final List<Predicate<Dialogue>> actions;
    private DialogueIterator itr = new DialogueIterator();
    private final AtomicInteger index = new AtomicInteger();

    private Speech(Supplier<Interactable> src, List<Predicate<Dialogue>> actions) {
        this.src = src;
        this.actions = actions;
    }

    //true if already completed, false otherwise
    //--NEGATED-- true if complete, false otherwise
    public boolean step() {
        if (!this.itr.hasNext()) {
            Environment.getLogger().info("No dialogue available");
            return false;
        }
        if (this.index.get() >= this.actions.size()) {
            Environment.getLogger().info("Actions exceeded");
            return true;
        }
        if (!this.itr.isSameResult()) {
            Dialogue next = this.itr.next();
            Environment.getLogger().info("next dialogue: " + next);
            Supplier<Boolean> input = () -> this.actions.get(this.index.getAndIncrement()).test(next);
            UserInput.runemateInput(input).postAttempt().thenRun(this.index::decrementAndGet);
        }
        return this.index.get() >= this.actions.size();
    }

    public void restart() {
        this.reset();
        Interactable src = this.src.get();
        if (src != null && src.isVisible()) {
            UserInput.interact(src, "Talk-to"); //TODO: verify option with all src, maybe need to supply
        }
    }

    public void reset() {
        this.index.set(0);
        this.itr = new DialogueIterator();
    }

    public void repeat() {
        if (this.index.decrementAndGet() < 0) {
            this.index.set(0);
        }
        this.itr = new DialogueIterator();
    }

    public static SpeechBuilder builder() {
        return new SpeechBuilder(null);
    }

    public static SpeechBuilder builder(Supplier<Interactable> src) {
        return new SpeechBuilder(src);
    }

    public static class SpeechBuilder {

        private List<Predicate<Dialogue>> built = new LinkedList<>();
        private final Supplier<Interactable> src;

        public SpeechBuilder(Supplier<Interactable> src) {
            this.src = src;
        }

        public SpeechBuilder step(Predicate<Dialogue> action) {
            return this.step("", action);
        }

        public SpeechBuilder step(String title, Predicate<Dialogue> action) {
            return this.step(title, "", action);
        }

        //null == no title, "" == any title, rest equals string match to title
        //same for text
        public SpeechBuilder step(String title, String text, Predicate<Dialogue> action) {
            this.built.add(title != null && title.isEmpty() ? action : d -> {
                if ((title == null || !title.isEmpty()) && !Objects.equals(title, d.getTitle())) {
                    throw new DialogueMismatchException("Mismatched title, expected: " + this.wrap(title) + ", found: " + this.wrap(d.getTitle()));
                }
                if ((text == null || !text.isEmpty()) && !Objects.equals(text, d.getRawText())) {
                    throw new DialogueMismatchException("Mismatched text, expected: " + this.wrap(title) + ", found: " + this.wrap(d.getTitle()));
                }
                return Objects.equals(d.getTitle(), title) && action.test(d);
            });
            return this;
        }

        public SpeechBuilder stepOption(Function<List<Option>, Option> selector) {
            return this.stepOption("", selector);
        }

        public SpeechBuilder stepOption(String title, Function<List<Option>, Option> selector) {
            return this.stepOption(title, "", selector);
        }
        
        private String wrap(String in) {
            return in == null ? null : '"' + in + '"';
        }

        public SpeechBuilder stepOption(String title, String text, Function<List<Option>, Option> selector) {
            return this.step(title, text, d -> {
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
