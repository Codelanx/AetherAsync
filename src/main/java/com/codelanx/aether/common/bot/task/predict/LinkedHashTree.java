package com.codelanx.aether.common.bot.task.predict;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * this is a trie data structure with a linked backing, to support peeking
 * recent patterns and doing endpoint searches
 * 
 * This is also... not a trie. It has a tail.
 * 
 * Further explained the "tail" represents unlinked state patterns
 */
public class LinkedHashTree<E> {
    
    private final TrieNode root = new TrieNode(null, 0); //null == empty literal, trie used for heuristics
    private final List<Pattern> patterns = new ArrayList<>(); //tracks patterns for quick matching
    private final List<Integer> tail = new LinkedList<>(); //we track potentially repeating patterns in a "tail"
    private final Map<Integer, E> converter = new HashMap<>(); //mapping our internal hashes back to states
    
    public LinkedHashTree() {
        
    }
    
    public void observe(E state) {
        int hash = state.hashCode();
        this.converter.putIfAbsent(hash, state);
        this.root.observe(hash);
        if (this.patterns.isEmpty()) {
            this.patterns.add(new Pattern(this, hash, 1));
            return;
        }
        if (this.tail.isEmpty() && this.patterns.size() == 1) {
        }
        
        this.tail.add(hash);
        //TODO: get tail
        this.root.observe(hash);
        //TODO: baking and shit
    }
    
    public E reverseMap(int hashcode) {
        return this.converter.get(hashcode);
    }
    
    private void grammatize() {}
    
    private boolean trieCoverage(List<Integer> tail) {
        return this.root.isCovered(this.tail);
    }
    
    /*
    
        a            -> a
        ab            -> ab
        aa             -> {ax2}
        {ax(n)}a        -> {ax(n+1)}
        {ax(n)}b{ax(n)}b    -> {{ax(n)}bx2}

        special grammar rules:
        ε            -> {εx1}
        {ax1}b             -> {abx1}
        {ax1}a            -> {ax2}
        {abx1}a         -> {abx1}a
     */
    private static class TrieNode {
        
        private final Map<Integer, TrieNode> children = new HashMap<>();
        private final int hashcode;
        private AtomicInteger weight = new AtomicInteger(0);
        
        public TrieNode(Integer hashcode) {
            this(hashcode, 1);
        }
        
        public TrieNode(Integer hashcode, int initalAmount) {
            this.hashcode = hashcode;
            this.weight.set(initalAmount);
        }
        
        //TODO: use traversal, not mutation
        
        public void observe(int tail) {
            this.observe0(tail).weight.incrementAndGet(); //TODO: verify this increments correctly
        }
        
        public void observe(List<Integer> tail) {
            this.observe0(new LinkedList<>(tail));
        }
        
        private TrieNode observe0(int tail) {
            return this.children.computeIfAbsent(tail, TrieNode::new);
        }
        
        private void observe0(List<Integer> tail) {
            if (tail.isEmpty()) {
                this.weight.incrementAndGet();
                return;
            }
            int state = tail.remove(0);
            this.observe0(tail.remove(0)).observe0(tail);
        }
        
        public boolean isCovered(List<Integer> tail) {
            return this.isCovered0(new LinkedList<>(tail));
        }
        
        private boolean isCovered0(List<Integer> tail) {
            if (tail.isEmpty()) {
                return true;
            }
            int state = tail.remove(0);
            TrieNode node = this.children.get(state);
            return node != null && node.isCovered0(tail);
        }
        
    }
}
