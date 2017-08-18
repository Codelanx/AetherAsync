package com.codelanx.aether.common.bot.task;

import com.codelanx.commons.util.ref.Box;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by roKgue on 8/17/2017.
 */
public class BranchPrediction<E> {
    
    private static final boolean DEBUG = true;
    private static final boolean FINER_DEBUG = false;
    private final List<Object> mixedList = new ArrayList<>();
    private final Map<Integer, E> hashCodeToState = new HashMap<>();
    
    
    public void observeState(E state) {
        //TODO: State insertion
        this.hashCodeToState.putIfAbsent(state.hashCode(), state);
        this.bakeList(state);
    }
    
    private void bakeList(E state) {
        this.bake(this.mixedList, state.hashCode());
    }

    private void bake(List<Object> bake, int hstate) {
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
        if (bake.isEmpty()) {
            bake.add(hstate);
            return;
        }
        LinkedList<Object> a = new LinkedList<>();
        LinkedList<Object> b = new LinkedList<>();
        Consumer<Integer> trimmer = w -> {
            for (;w < bake.size();) {
                bake.remove((int) w);
            }
        };
        bake.add(hstate);
        Consumer<Object> remakeToPattern = p -> {
            a.clear();
            b.clear();
            b.add(p);
        };
        for (int i = bake.size() - 1; i >= 0; i--) {
            String format;
            String before;
            String result = null;
            String sa;
            String sb;
            Object o = bake.get(i);
            if (DEBUG) {
                format = "%-50s -> %-50s #a: %-25s b: %-25s";
                before = objectToString(this, o) + listToString(this, a) + listToString(this, b);
            }
            if (a.isEmpty() && b.isEmpty()) {
                b.push(o);
                if (DEBUG) {
                    //System.out.println("initial value");
                    System.out.println(String.format(format, before, before, "[]", listToString(this, b)));
                }
                continue;
            }
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
                    bake.add(o);
                    if (DEBUG) {
                        result = op.toString();
                        if (FINER_DEBUG) {
                            System.out.println("pattern increment");
                        }
                        System.out.println(String.format(format, before, result, sa, listToString(this, copyCheck)));
                    }
                    continue;
                }
            }
            if (a.size() == b.size()) {
                //move from a->b
                if (a.size() > 0) {
                    b.push(a.pollLast());
                }
            }
            //add to a
            a.push(o);
            if (DEBUG) {
                sa = listToString(this, a);
                sb = listToString(this, b);
            }
            if (a.equals(b)) {
                //TODO: Determine if this is a pre-loop or post-loop op
                //we found a pattern match
                trimmer.accept(i);
                Pattern p = new Pattern(this, a);
                if (DEBUG) {
                    if (FINER_DEBUG) {
                        System.out.println("pattern make");
                    }
                    result = p.toString();
                }
                remakeToPattern.accept(p);
                bake.add(p);
            }
            if (DEBUG) {
                if (result == null) {
                    if (FINER_DEBUG) {
                        System.out.println("no pattern");
                    }
                    result = before;
                }
                System.out.println(String.format(format, before, result, sa, sb));
            }
        }
        if (DEBUG && FINER_DEBUG) {
            System.out.println("End list: " + listToString(this, this.mixedList));
        }
    }
    
    public static void main(String... args) {
        List<String> tests = Arrays.asList("aaabaaabaaabaaab", "abcdcbabcdcb", "abbcabbcabbc", "aaabaaababbcabbcabbca", "aaabaaabcbcabbcabbcabbca");
        tests.forEach(test -> {
            char[] c = test.toCharArray();
            System.out.println("Char array: " + Arrays.toString(c));
            BranchPrediction<Character> predictor = new BranchPrediction<>();
            for (int i = 0; i < c.length; i++) {
                predictor.observeState(c[i]);
                System.out.println();
            }
        });
    }

    private List<Object> getTail(List<Object> raw) {
        if (raw.isEmpty()) {
            return raw;
        }
        if (raw.get(raw.size() - 1).getClass() == Pattern.class) {
            return Collections.emptyList();
        }
        for (int i = raw.size() - 1; i >= 0; i--) {
            if (raw.get(i).getClass() == Pattern.class) {
                return raw.subList(i + 1, raw.size());
            }
        }
        return raw;
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
        List<Object> tail = this.getTail(this.mixedList);
        //our tail is our powerful ally, as we use it to binary search the patterns:
        List<Object> other = this.mixedList.subList(0, this.mixedList.size() - tail.size());
        LinkedHashMap<Pattern, Integer> pairs = other.stream().filter(o -> o.getClass() == Pattern.class).map(o -> (Pattern) o)
                .sorted(Comparator.comparing(Pattern::getAmount))
                .collect(Collectors.toMap(Function.identity(), p -> p.binarySearch(tail), (o1, o2) -> o1, LinkedHashMap::new));
        Pattern p = pairs.entrySet().stream()
                .filter(ent -> ent.getValue() > 0)
                .findAny().map(Entry::getKey).orElse(null);
        if (p == null) {
            //no pattern match
            //...so, no prediction?
        } else {

        }
        if (this.mixedList.size() == 1) {
            Object o = this.mixedList.get(0);
            return this.mixedList.get(0).hashCode();
        }
        {
            //if we reach a state where we need to determine by trying available states for shortest pattern
            Collection<? extends E> knownValues = this.hashCodeToState.values();
            List<Object> base = this.mixedList;
            //O(n)
            Box<E> minVal = new Box<>();
            AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
            knownValues.forEach(state -> {
                List<Object> tempBake = new ArrayList<>(base);
                this.bake(tempBake, state.hashCode());
                if (tempBake.size() < min.get()) {
                    min.set(tempBake.size());
                    minVal.value = state;
                }
            });
            Map<E, Integer> count = knownValues.stream().collect(Collectors.toMap(Function.identity(), state -> {
                List<Object> tempBake = new ArrayList<>(base);
                this.bake(tempBake, state.hashCode());
                return tempBake.size();
            }));
            //find minimum value
        }
        //TODO
        return 0;
    }
    
    private void casualInsert(E state) {
    }
    
    private static final class Pattern {
        
        //TODO: Pattern needs to be able to store hashcodes/patterns too
        private final BranchPrediction<?> src;
        private final List<Object> hashcodes = new ArrayList<>();
        private AtomicInteger amount;
        private AtomicInteger baseLen = new AtomicInteger();
        
        public Pattern(BranchPrediction<?> src, int hashcode, int initialAmount) {
            this(src, Collections.singletonList(hashcode), initialAmount);
        }
        
        public Pattern(BranchPrediction<?> src, List<Object> hashcodes) {
            this(src, hashcodes, 2);
        }
        
        public Pattern(BranchPrediction<?> src, List<Object> hashcodes, int initialAmount) {
            this.src = src;
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

        @Override
        public int hashCode() {
            return this.hashcodes.hashCode();
        }

        public int binarySearch(List<Object> partialMatch) {
            if (partialMatch.isEmpty()) {
                return 0;
            }
            //TODO:
            for (int i = 0; i < this.hashcodes.size() && i < partialMatch.size(); i++) {
                Object us = this.hashcodes.get(i);
                Object them = partialMatch.get(i);
                if (them.getClass() == Pattern.class) {

                }
                if (us.getClass() == Pattern.class) {
                    Pattern p = (Pattern) us;
                    //garbage code below, just made it so it it'd compile
                    //if (len > index) {
                        //our index is within this pattern
                        return p.getHashCodeAt(0);
                    //}
                } else {
                    //index--;
                }
            }
            return 0;
        }

        private int getHashCodeAt(int index) {
            for (int i = 0; i < this.hashcodes.size() && i < index; i++) {
                Object o = this.hashcodes.get(i);
                if (o.getClass() == Pattern.class) {
                    Pattern p = (Pattern) o;
                    int len = p.getLength();
                    if (len > index) {
                        //our index is within this pattern
                        return p.getHashCodeAt(index);
                    }
                } else {
                    index--;
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
                return  "{" + this.src.reverseMap(this.hashcodeAt(0)) + "x" + this.amount.get() + "}";
            }
            return "{" + BranchPrediction.listToString(this.src, this.hashcodes) + "x" + this.amount.get() + "}";
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass() && this.hashcodes.equals(((Pattern) obj).hashcodes);
        }
    }

    private E reverseMap(int hashcode) {
        return this.hashCodeToState.get(hashcode);
    }
    
    private static String listToString(BranchPrediction<?> src, List<Object> patternObjects) {
        return patternObjects.stream().map(o -> BranchPrediction.objectToString(src, o)).collect(Collectors.joining());
    }

    private static String objectToString(BranchPrediction<?> src, Object o) {
        if (o instanceof Number) {
            Object back = src.reverseMap((int) o);
            return back == null ? o.toString() : back.toString();
        } else {
            return o.toString();
        }
    }
    
    
}
