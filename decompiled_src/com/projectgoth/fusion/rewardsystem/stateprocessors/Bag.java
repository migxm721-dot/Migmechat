/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.projectgoth.fusion.rewardsystem.stateprocessors.RemainderBag;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Bag<TItem> {
    private final HashMap<TItem, Counter> itemToCountMap;

    public Bag(int mapCapacity) {
        this.itemToCountMap = new HashMap(mapCapacity);
    }

    public Bag() {
        this(10);
    }

    public int add(TItem item) {
        Counter counter = this.itemToCountMap.get(item);
        if (counter == null) {
            counter = new Counter();
            this.itemToCountMap.put(item, counter);
        }
        counter.inc();
        return counter.intValue();
    }

    public Bag<TItem> addAll(Collection<TItem> items) {
        for (TItem item : items) {
            this.add(item);
        }
        return this;
    }

    public Bag<TItem> setCount(TItem item, int count) {
        if (count > 0) {
            this.itemToCountMap.put(item, new Counter(count));
        } else {
            this.itemToCountMap.remove(item);
        }
        return this;
    }

    public int uniqueItemCount() {
        return this.itemToCountMap.size();
    }

    private Set<Map.Entry<TItem, Counter>> entries() {
        return this.itemToCountMap.entrySet();
    }

    public Set<Map.Entry<TItem, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<TItem, Integer>>(){

            @Override
            public Iterator<Map.Entry<TItem, Integer>> iterator() {
                return new Iterator<Map.Entry<TItem, Integer>>(){
                    private final Iterator<Map.Entry<TItem, Counter>> baseIterator;
                    {
                        this.baseIterator = Bag.this.entries().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.baseIterator.hasNext();
                    }

                    @Override
                    public Map.Entry<TItem, Integer> next() {
                        final Map.Entry baseEntry = this.baseIterator.next();
                        return new Map.Entry<TItem, Integer>(){

                            @Override
                            public TItem getKey() {
                                return baseEntry.getKey();
                            }

                            @Override
                            public Integer getValue() {
                                return ((Counter)baseEntry.getValue()).intValue();
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                throw new UnsupportedOperationException("setValue not supported");
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        this.baseIterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return Bag.this.uniqueItemCount();
            }
        };
    }

    public int getCount(TItem item) {
        Counter count = this.itemToCountMap.get(item);
        if (count != null && count.intValue() > 0) {
            return count.intValue();
        }
        return 0;
    }

    public void clear() {
        this.itemToCountMap.clear();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Bag)) {
            return false;
        }
        return this.itemToCountMap.equals(((Bag)obj).itemToCountMap);
    }

    public RemainderBag<TItem> matchAndFilter(Bag<TItem> itemsToRemove) {
        RemainderBag nextConsumedBag = new RemainderBag(itemsToRemove.uniqueItemCount(), true);
        RemainderBag nextNonConsumedBag = new RemainderBag(itemsToRemove.uniqueItemCount(), false);
        boolean foundNonConsumable = false;
        for (Map.Entry<TItem, Counter> entriesToRemove : super.entries()) {
            TItem itemToRemove = entriesToRemove.getKey();
            int currentCount = this.getCount(itemToRemove);
            nextNonConsumedBag.setCount(itemToRemove, currentCount);
            if (!foundNonConsumable && currentCount > 0) {
                Counter expectedCount = entriesToRemove.getValue();
                int remainder = currentCount - expectedCount.intValue();
                if (remainder >= 0) {
                    nextConsumedBag.setCount(itemToRemove, remainder);
                    continue;
                }
                foundNonConsumable = true;
                nextConsumedBag.clear();
                continue;
            }
            foundNonConsumable = true;
            nextConsumedBag.clear();
        }
        if (foundNonConsumable) {
            return nextNonConsumedBag;
        }
        return nextConsumedBag;
    }

    public String toString() {
        return this.itemToCountMap.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Counter
    extends Number
    implements Comparable<Counter> {
        private int value;

        public Counter() {
            this(0);
        }

        public Counter(int val) {
            this.value = val;
        }

        void inc() {
            ++this.value;
        }

        @Override
        public int intValue() {
            return this.value;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof Counter) {
                return this.value == ((Counter)obj).value;
            }
            return false;
        }

        public int hashCode() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        @Override
        public int compareTo(Counter that) {
            return this.minus(that);
        }

        public int minus(Counter that) {
            return this.value - that.value;
        }

        @Override
        public double doubleValue() {
            return this.value;
        }

        @Override
        public float floatValue() {
            return this.value;
        }

        @Override
        public long longValue() {
            return this.value;
        }
    }
}

