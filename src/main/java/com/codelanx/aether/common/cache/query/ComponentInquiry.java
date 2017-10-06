package com.codelanx.aether.common.cache.query;

import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent.Type;

import java.util.regex.Pattern;

public class ComponentInquiry extends Inquiry {

    private final int container;
    private final Type type;

    private final String text;
    private final String name;
    private final Pattern pText;
    private final Pattern pName;

    public ComponentInquiry(int container, String name, Type type) {
        this.container = container;
        this.name = name;
        this.type = type;
        this.text = null;
        this.pName = null;
        this.pText = null;
    }

    private ComponentInquiry(ComponentInquiryBuilder builder) {
        this.container = builder.container;
        this.type = builder.type;
        this.name = builder.basicName;
        this.text = builder.basicText;
        this.pText = builder.text;
        this.pName = builder.name;
    }

    public String getName() {
        return this.name;
    }

    public Pattern getNamePattern() {
        return this.pName;
    }

    public String getText() {
        return this.text;
    }

    public Pattern getTextPattern() {
        return this.pText;
    }

    public int getContainer() {
        return this.container;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentInquiry that = (ComponentInquiry) o;

        if (getContainer() != that.getContainer()) return false;
        if (!getName().equals(that.getName())) return false;
        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = getContainer();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }

    public static ComponentInquiryBuilder builder() {
        return new ComponentInquiryBuilder();
    }

    public static class ComponentInquiryBuilder {

        private String basicName;
        private String basicText;
        private Pattern name;
        private Pattern text;
        private int container;
        private Type type;

        public ComponentInquiryBuilder name(String name) {
            return this.name(name, false);
        }

        public ComponentInquiryBuilder name(String name, boolean regex) {
            if (!regex) {
                this.basicName = name;
                return this;
            }
            return this.name(Pattern.compile(name));
        }

        public ComponentInquiryBuilder name(Pattern pattern) {
            this.name = pattern;
            return this;
        }

        public ComponentInquiryBuilder text(String text) {
            return this.text(text, false);
        }

        public ComponentInquiryBuilder text(String text, boolean regex) {
            if (!regex) {
                this.basicText = text;
                return this;
            }
            return this.text(Pattern.compile(text));
        }

        public ComponentInquiryBuilder text(Pattern pattern) {
            this.text = pattern;
            return this;
        }

        public ComponentInquiryBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public ComponentInquiryBuilder container(int container) {
            this.container = container;
            return this;
        }

        public ComponentInquiry build() {
            return new ComponentInquiry(this);
        }

    }
}
