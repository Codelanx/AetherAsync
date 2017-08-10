package com.codelanx.aether.common.menu.dialog;

import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.Continue;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.Option;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.Selectable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Dialogue {

    private final List<Selectable> options = new ArrayList<>();
    private final String title;
    private final String text;

    //package-private, no one else needs to create dialogues
    Dialogue(String title, String text, Selectable selectable) {
        this(title, text, Collections.singletonList(selectable));
    }

    Dialogue(String title, String text, List<? extends Selectable> selectables) {
        this.title = title;
        this.text = text == null ? text : text.replace('\r', ' ').replace("\n", "");
        this.options.addAll(selectables);
    }

    public List<Selectable> getOptions() {
        return this.options;
    }

    public boolean isContinue() {
        return this.options.size() == 1 && this.options.get(0) instanceof Continue;
    }

    public Continue getContinue() {
        return (Continue) this.options.get(0);
    }

    public List<Option> getOptionsList() {
        return (List<Option>) (List<?>) this.options; //who needs type safety
    }

    public boolean isCurrent() {
        return Objects.equals(this.title, ChatDialog.getTitle())
                && Objects.equals(this.text, DialogueIterator.getTextSafe())
                && this.options.equals(DialogueIterator.getRawOptions());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Dialogue)) {
            return false;
        }
        Dialogue d = (Dialogue) obj;
        return Objects.equals(this.title, d.getTitle())
                && Objects.equals(this.title, d.getRawText())
                && this.options.equals(d.options);
    }

    @Override
    public int hashCode() {
        int result = getOptions().hashCode();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    public String getTitle() {
        return this.title;
    }

    public String getRawText() {
        return this.text;
    }

    public boolean isEmpty() {
        return this.options.isEmpty() && this.text == null && this.title == null;
    }

    @Override
    public String toString() {
        return "Dialogue{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", options=[" + options.stream().map(o -> {
                    if (o instanceof Option) {
                        Option opt = (Option) o;
                        return "Option{number=" + opt.getNumber() + ", text='" + opt.getText() + "'}";
                    } else if (o instanceof Continue) {
                        return "Continue{text='" + ((Continue) o).getComponent().getText() + "'}";
                    }
                    return "Selectable";
                }).collect(Collectors.joining(", ")) + "]" +
                '}';
    }
}
