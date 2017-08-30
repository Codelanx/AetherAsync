package com.codelanx.aether.common.bot.task.predict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Created by rogue on 8/29/2017.
 */
public class Pattern<E> {

    //TODO: Pattern needs to be able to store hashcodes/patterns too
    private final BranchPredictor<E> src;
    private final LinkedHashTree<E> treeSrc;
    private final List<Object> hashcodes = new ArrayList<>();
    private AtomicInteger amount;
    private AtomicInteger baseLen = new AtomicInteger();

    public Pattern(BranchPredictor<E> src, int hashcode, int initialAmount) {
        this(src, Collections.singletonList(hashcode), initialAmount);
    }

    public Pattern(BranchPredictor<E> src, List<Object> hashcodes) {
        this(src, hashcodes, 2);
    }

    public Pattern(BranchPredictor<E> src, List<Object> hashcodes, int initialAmount) {
        this.src = src;
        this.treeSrc = null;
        this.hashcodes.addAll(hashcodes);
        this.amount = new AtomicInteger(initialAmount);
        this.baseLen.set(this.lengthOf(this.hashcodes));
    }

    public Pattern(LinkedHashTree<E> src, int hashcode, int initialAmount) {
        this(src, Collections.singletonList(hashcode), initialAmount);
    }

    public Pattern(LinkedHashTree<E> src, List<Object> hashcodes) {
        this(src, hashcodes, 2);
    }

    public Pattern(LinkedHashTree<E> src, List<Object> hashcodes, int initialAmount) {
        this.src = null;
        this.treeSrc = src;
        this.hashcodes.addAll(hashcodes);
        this.amount = new AtomicInteger(initialAmount);
        this.baseLen.set(this.lengthOf(this.hashcodes));
    }
    
    

    public int getAmount() {
        return this.amount.get();
    }

    //returns new value
    public int increment() {
        return this.amount.incrementAndGet();
    }

    public void set(int amount) {
        this.amount.set(amount);
    }

    public boolean matches(Object state) {
        if (this.hashcodes.size() != 1) {
            return false;
        }
        Object o = this.hashcodes.get(0);
        return (o.getClass() == this.getClass()) ? ((Pattern) o).matches(state) : ((int) o) == state.hashCode();
    }

    List<Object> getObjects() {
        return this.hashcodes;
    }

    public boolean isLiteral() {
        return this.hashcodes.size() == 1 && this.hashcodes.get(0) instanceof Number;
    }

    public Pattern patternAt(int index) {
        return (Pattern) this.hashcodes.get(index);
    }

    public int hashcodeAt(int index) {
        return (int) this.hashcodes.get(index);
    }

    public void append(List<Object> obj) {
        if (this.getAmount() != 1) {
            throw new UnsupportedOperationException("Can only append to a single-case pattern");
        }
        this.hashcodes.addAll(obj);
    }

    @Override
    public int hashCode() {
        return this.hashcodes.hashCode();
    }

    //returns starting index of 
    public int search(List<Object> bakedPartialMatch) {
        if (bakedPartialMatch.isEmpty()) {
            return this.getHashCodeAt(0);
        }
        return this.search(new Pattern(this.src, bakedPartialMatch));
    }

    private int search(Pattern raw) {
        int key = raw.hashcodeAt(0);
        parent:
        for (int i = 0; i < this.hashcodes.size(); i++) {
            Object o = this.hashcodes.get(i);
            if (o instanceof Pattern) {
                Pattern p = (Pattern) o;
                int at = p.search(raw);
                if (at != -1) {
                    return at;
                }
            } else if (((int) o) == key) {
                //search from here
                if (raw.hashcodes.get(0) instanceof Pattern) {
                    return -1;
                }
                raw.hashcodes.remove(0);
                if (raw.hashcodes.isEmpty()) {
                    return i;
                }
            }
        }
        return -1;
    }

    //a quick iteration
    private int getHashCodeAt(int index) {
        for (int i = 0; i < this.hashcodes.size() && i <= index; i++) {
            Object o = this.hashcodes.get(i);
            if (o instanceof Pattern) {
                Pattern p = (Pattern) o;
                int len = p.getLength();
                if (index < len) {
                    return p.getHashCodeAt(index);
                } else {
                    index -= len;
                }
            } else if (index == i) {
                return (int) o;
            }
        }
        return -1;
    }

    //return the raw length of this pattern (reduction of state * amount)
    public int getLength() {
        if (this.isLiteral()) {
            return this.amount.get();
        } else {
            return this.lengthOf(this.hashcodes) * this.amount.get();
        }
    }

    private int lengthOf(List<Object> objects) {
        return objects.stream().map(o -> {
            return o instanceof Number ? 1 : ((Pattern) o).getLength();
        }).reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        if (this.isLiteral()) {
            return  "{" + this.getMapper().apply(this.hashcodeAt(0)) + "x" + this.amount.get() + "}";
        }
        return "{" + BranchPredictor.listToString(this.getMapper(), this.hashcodes) + "x" + this.amount.get() + "}";
    }
    
    private Function<Integer, Object> getMapper() {
        if (this.src == null) {
            return this.treeSrc == null ? null : this.treeSrc::reverseMap;
        }
        return this.src::reverseMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Pattern o = (Pattern) obj;
        return this.hashcodes.equals(o.hashcodes) && this.amount.get() == o.amount.get();
    }
}
