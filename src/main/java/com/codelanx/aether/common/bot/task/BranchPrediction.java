package com.codelanx.aether.common.bot.task;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by roKgue on 8/17/2017.
 */
public class BranchPrediction<E> {
    
    private static final boolean DEBUG = true;
    private final List<Object> mixedList = new ArrayList<>();
    private final Map<Integer, E> hashCodeToState = new HashMap<>();
    
    
    public void observeState(E state) {
        //TODO: State insertion
        this.hashCodeToState.putIfAbsent(state.hashCode(), state);
        this.bakeList(state);
    }
    
    private void bakeList(E state) {
        if (this.mixedList.isEmpty()) {
            this.mixedList.add(state.hashCode());
            return;
        }
        int hstate = state.hashCode();
        List<Object> patternMismatch = new LinkedList<>();
        List<Object> patternMismatchMatcher = new LinkedList<>(); //is this how you obfuscate?
        
        /*
        
        we need to operate on a grammar, to simplify pattern making:

                a -> a
                ab -> ab
                aa -> {ax2}
                {ax(n)}a -> {ax(n+1)}
                {ax(n)}b{ax(n)}b -> {{ax(n)}bx2}
                
            example using: abcdcbabcdcb
                b		        -> b                #a: []       b: b
                cb		        -> cb			    #a: c		b: b
                dcb		        -> dcb			    #a: d		b: cb
                cdcb		    -> cdcb			    #a: cd		b: cb
                bcdcb		    -> bcdcb		    #a: bc		b: dcb
                abcdcb		    -> abcdcb		    #a: abc		b: dcb
                babcdcb		    -> babcdcb		    #a: bab		b: cdcb
                cbabcdcb	    -> cbabcdcb		    #a: cbab	b: cdcb
                dcbabcdcb	    -> dcbabcdcb		#a: dcba	b: bcdcb
                cdcbabcdcb	    -> cdcbabcdcb		#a: cdcba	b: bcdcb
                bcdcbabcdcb	    -> bcdcbabcdcb		#a: bcdcb	b: abcdcb
                abcdcbabcdcb	-> {abcdcbx2}		#a: abcdcb	b: abcdcb	# MATCH FOUND
                {abcdcbx2}abcdcb
         */
        
        LinkedList<Object> a = new LinkedList<>();
        LinkedList<Object> b = new LinkedList<>();
        b.add(hstate);
        Consumer<Integer> trimmer = w -> {
            for (;w < this.mixedList.size();) {
                this.mixedList.remove(w);
            }
        };
        Consumer<Object> remakeToPattern = p -> {
            a.clear();
            b.clear();
            b.add(p);
        };
        for (int i = this.mixedList.size() - 1; i >= 0; i--) {
            String format;
            String before;
            String result = null;
            String sa;
            String sb;
            if (DEBUG) {
                format = "%-100s -> %-100s #a: %-50s b: %-50s";
                before = listToString(a) + listToString(b);
            } 
            Object o = this.mixedList.get(i);
            if (o.getClass() == Pattern.class) {
                //TODO:
                //this can likely be simplified further
                //instead of checking whole list contents, we can probably check the most recent literal
                //to pattern literals, and combine only those
                Pattern op = (Pattern) o;
                if (DEBUG) {
                    sa = op.toString();
                }
                List<Object> copyCheck = new ArrayList<>(a.size() + b.size());
                copyCheck.addAll(a);
                copyCheck.addAll(b);
                if (op.getObjects().equals(copyCheck)) {
                    trimmer.accept(i);
                    op.increment();
                    remakeToPattern.accept(o);
                    if (DEBUG) {
                        result = op.toString();
                        System.out.println(String.format(format, before, result, sa, listToString(copyCheck)));
                    }
                    continue;
                }
            }
            if (b.size() == 1 && a.size() == 0) {
            }
            if (a.size() == b.size()) {
                //move from a->b
                if (a.size() > 0) {
                    b.push(a.pop());
                }
            }
            //add to a
            a.push(o);
            if (DEBUG) {
                sa = listToString(a);
                sb = listToString(b);
            }
            if (a.equals(b)) {
                //TODO: Determine if this is a pre-loop or post-loop op
                //we found a pattern match
                trimmer.accept(i);
                Pattern p = new Pattern(a);
                if (DEBUG) {
                    result = p.toString();
                }
                remakeToPattern.accept(p);
                this.mixedList.add(p);
            }
            if (DEBUG) {
                if (result == null) {
                    result = before;
                }
                System.out.println(String.format(format, before, result, sa, sb));
            }
        }
        if (DEBUG) {
            System.out.println("End list: " + listToString(this.mixedList));
        }
    }
    
    public static void main(String... args) {
        char[] c = "aaabaaabaaabaaab".toCharArray();
        BranchPrediction<Character> predictor = new BranchPrediction<>();
        for (int i = 0; i < c.length; i++) {
            predictor.observeState(c[i]);
        }
    }
    
            
            
            
         /*   if (o.getClass() == Pattern.class) {
        Pattern p = (Pattern) o;
        if (p.matches(state)) {
            //We've got a match to a prev pattern, increment
            if (patternMismatch.isEmpty()) {
                p.increment();
            } else {
                //we've got a match within our pattern mismatch
                if (patternMismatchMatcher.isEmpty()) {
                    patternMismatchMatcher.addAll(patternMismatch);
                    patternMismatch.clear();
                    patternMismatch.add(p);
                } else {
                    //compare our two previous mismatches
                    if (patternMismatchMatcher.equals(patternMismatch)) {
                        //we've got a pattern within a pattern
                        //TODO: Continue searching for pattern
                        this.mixedList.add(new Pattern(patternMismatch));

                    }
                }
            }
            p.increment();
            return;
        } else {
            //TODO: Backwards search for pattern within patterns
            //patternception
            //like dicaprio
            //fuck idk
            patternMismatch.add(o);
        }
    } else {
        int hashcode = (int) o;
        if (hashcode == hstate) {
            //we've got a local pattern
            this.mixedList.set(i, new Pattern(hstate, 2));
        } else {
            patternMismatch.add(o);
        }
    }*/
    
    public E predict() {
        return this.hashCodeToState.get(this.predictCode());
    }
    
    private int predictCode() {
        if (this.mixedList.size() == 1) {
            return this.mixedList.get(0).hashCode();
        }
        //TODO
        return 0;
    }
    
    private void casualInsert(E state) {
    }
    
    private final class Pattern {
        
        //TODO: Pattern needs to be able to store hashcodes/patterns too
        private final List<Object> hashcodes = new ArrayList<>();
        private AtomicInteger amount;
        
        public Pattern(int hashcode, int initialAmount) {
            this(Collections.singletonList(hashcode), initialAmount);
        }
        
        public Pattern(List<Object> hashcodes) {
            this(hashcodes, 2);
        }
        
        public Pattern(List<Object> hashcodes, int initialAmount) {
            this.hashcodes.addAll(hashcodes);
            this.amount = new AtomicInteger(initialAmount);
        }
        
        //returns new value
        public int increment() {
            return this.amount.incrementAndGet();
        }
        
        public void set(int amount) {
            this.amount.set(amount);
        }
        
        public boolean matches(E state) {
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

        @Override
        public int hashCode() {
            return this.hashcodes.hashCode();
        }

        @Override
        public String toString() {
            if (this.isLiteral()) {
                return  "{" + BranchPrediction.this.hashCodeToState.get(this.hashcodeAt(0)) + "x" + this.amount.get();
            }
            return "{" + listToString(this.hashcodes) + "x" + this.amount.get() + "}";
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass() && this.hashcodes.equals(((Pattern) obj).hashcodes);
        }
    }
    
    private String listToString(List<Object> patternObjects) {
        return patternObjects.stream().map(o -> {
            if (o instanceof Number) {
                E back = BranchPrediction.this.hashCodeToState.get((int) o);
                return back == null ? o.toString() : back.toString();
            } else {
                return o.toString();
            }
        }).collect(Collectors.joining());
    }
    
    
}
