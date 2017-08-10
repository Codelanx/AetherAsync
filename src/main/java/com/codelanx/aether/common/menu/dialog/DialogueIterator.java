package com.codelanx.aether.common.menu.dialog;

import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.Selectable;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DialogueIterator implements Iterator<Dialogue> {

    private boolean recursiveSafety = false; //just a delay in case of starting early
    private Dialogue previous;

    public DialogueIterator() {

    }

    @Override
    public boolean hasNext() {
        boolean hasNext = DialogueIterator.isChatValid();// && (this.previous == null || !this.previous.isCurrent());
        if (!recursiveSafety && !hasNext) {
            Execution.delay(200); //just in case
            recursiveSafety = true;
            hasNext = hasNext(); //if we have a result, set to false again. this ensures no repeat attempts
            recursiveSafety = false;
        }
        return hasNext;
    }

    //always returns a new dialogue
    @Override
    public Dialogue next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("End of iterator");
        }
        if (this.isSameResult()) {
            throw new NoSuchElementException("No new result yet - please wait");
        }
        //not particularly thread-safe
        this.previous = DialogueIterator.getCurrentDialogue();
        return this.previous;
    }

    public static Dialogue getCurrentDialogue() {
        return new Dialogue(ChatDialog.getTitle(), DialogueIterator.getTextSafe(), DialogueIterator.getRawOptions());
    }

    public static String getTextSafe() {
        String text = ChatDialog.getText();
        if ((Players.getLocal().getName() + ": *").equals(text)) {
            return null;
        }
        return text;
    }

    public static boolean isChatValid() {
        return ChatDialog.getTitle() != null
                || ChatDialog.getContinue() != null
                || !ChatDialog.getOptions().isEmpty();
    }

    public static List<? extends Selectable> getRawOptions() {
        return ChatDialog.getOptions().isEmpty()
                ? ChatDialog.getContinue() == null
                    ? Collections.emptyList()
                    : Collections.singletonList(ChatDialog.getContinue())
                : ChatDialog.getOptions();
    }

    public boolean isSameResult() {
        return this.previous != null && this.previous.isCurrent();
    }
}
