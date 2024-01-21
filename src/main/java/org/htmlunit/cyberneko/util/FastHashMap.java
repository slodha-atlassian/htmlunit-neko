/*
 * Copyright (c) 2005-2024 René Schwietzke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.htmlunit.cyberneko.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple hash map implementation taken from here
 * https://github.com/mikvor/hashmapTest/blob/master/src/main/java/map/objobj/ObjObjMap.java
 * No concrete license specified at the source. The project is public domain.
 *
 * Not thread-safe! Null support was removed.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 *
 * @author Ren&eacute; Schwietzke
 * @since 3.10.0
 */
public class FastHashMap<K, V> {
    private static final Object FREE_KEY = new Object();
    private static final Object REMOVED_KEY = new Object();

    /** Keys and values */
    private Object[] m_data_;

    /** Fill factor, must be between (0 and 1) */
    private final float m_fillFactor_;
    /** We will resize a map once it reaches this size */
    private int m_threshold_;
    /** Current map size */
    private int m_size_;
    /** Mask to calculate the original position */
    private int m_mask_;
    /** Mask to wrap the actual array pointer */
    private int m_mask2_;

    public FastHashMap() {
        this(13, 0.5f);
    }

    public FastHashMap(final int size, final float fillFactor) {
        if (fillFactor <= 0 || fillFactor >= 1) {
            throw new IllegalArgumentException("FillFactor must be in (0, 1)");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive!");
        }
        final int capacity = arraySize(size, fillFactor);
        m_mask_ = capacity - 1;
        m_mask2_ = capacity * 2 - 1;
        m_fillFactor_ = fillFactor;

        m_data_ = new Object[capacity * 2];
        Arrays.fill(m_data_, FREE_KEY);

        m_threshold_ = (int) (capacity * fillFactor);
    }

    public V get(final K key) {
        final int srcHashCode = key.hashCode();

        int ptr = (srcHashCode & m_mask_) << 1;
        Object k = m_data_[ptr];

        if (k == FREE_KEY) {
            //end of chain already
            return null;
        }

        //we check FREE and REMOVED prior to this call
        if (k.hashCode() == srcHashCode && k.equals(key)) {
            return (V) m_data_[ptr + 1];
        }

        while (true) {
            ptr = (ptr + 2) & m_mask2_; //that's next index
            k = m_data_[ ptr ];
            if (k == FREE_KEY) {
                return null;
            }
            if (k.hashCode() == srcHashCode && k.equals(key)) {
                return (V) m_data_[ptr + 1];
            }
        }
    }

    public V put(final K key, final V value) {
        int ptr = getStartIndex(key) << 1;
        Object k = m_data_[ptr];

        if (k == FREE_KEY) {
            //end of chain already
            m_data_[ ptr ] = key;
            m_data_[ ptr + 1 ] = value;
            if (m_size_ >= m_threshold_) {
                rehash(m_data_.length * 2); //size is set inside
            }
            else {
                ++m_size_;
            }
            return null;
        }
        else if (k.equals(key)) {
            //we check FREE and REMOVED prior to this call
            final Object ret = m_data_[ ptr + 1 ];
            m_data_[ ptr + 1 ] = value;
            return (V) ret;
        }

        int firstRemoved = -1;
        if (k == REMOVED_KEY) {
            //we may find a key later
            firstRemoved = ptr;
        }

        while (true) {
            // that's next index calculation
            ptr = (ptr + 2) & m_mask2_;
            k = m_data_[ ptr ];
            if (k == FREE_KEY) {
                if (firstRemoved != -1) {
                    ptr = firstRemoved;
                }
                m_data_[ ptr ] = key;
                m_data_[ ptr + 1 ] = value;
                if (m_size_ >= m_threshold_) {
                    //size is set inside
                    rehash(m_data_.length * 2);
                }
                else {
                    ++m_size_;
                }
                return null;
            }
            else if (k.equals(key)) {
                final Object ret = m_data_[ ptr + 1 ];
                m_data_[ ptr + 1 ] = value;
                return (V) ret;
            }
            else if (k == REMOVED_KEY) {
                if (firstRemoved == -1) {
                    firstRemoved = ptr;
                }
            }
        }
    }

    public V remove(final K key) {
        int ptr = getStartIndex(key) << 1;
        Object k = m_data_[ ptr ];
        if (k == FREE_KEY) {
            // end of chain already
            return null;
        }
        else if (k.equals(key)) {
            // we check FREE and REMOVED prior to this call
            --m_size_;
            if (m_data_[ (ptr + 2) & m_mask2_ ] == FREE_KEY) {
                m_data_[ ptr ] = FREE_KEY;
            }
            else {
                m_data_[ ptr ] = REMOVED_KEY;
            }
            final V ret = (V) m_data_[ ptr + 1 ];
            m_data_[ ptr + 1 ] = null;
            return ret;
        }
        while (true) {
            //that's next index calculation
            ptr = (ptr + 2) & m_mask2_;
            k = m_data_[ ptr ];
            if (k == FREE_KEY) {
                return null;
            }
            else if (k.equals(key)) {
                --m_size_;
                if (m_data_[ (ptr + 2) & m_mask2_ ] == FREE_KEY) {
                    m_data_[ ptr ] = FREE_KEY;
                }
                else {
                    m_data_[ ptr ] = REMOVED_KEY;
                }
                final V ret = (V) m_data_[ ptr + 1 ];
                m_data_[ ptr + 1 ] = null;
                return ret;
            }
        }
    }

    public int size() {
        return m_size_;
    }

    private void rehash(final int newCapacity) {
        m_threshold_ = (int) (newCapacity / 2 * m_fillFactor_);
        m_mask_ = newCapacity / 2 - 1;
        m_mask2_ = newCapacity - 1;

        final int oldCapacity = m_data_.length;
        final Object[] oldData = m_data_;

        m_data_ = new Object[ newCapacity ];
        Arrays.fill(m_data_, FREE_KEY);

        m_size_ = 0;

        for (int i = 0; i < oldCapacity; i += 2) {
            final Object oldKey = oldData[ i ];
            if (oldKey != FREE_KEY && oldKey != REMOVED_KEY) {
                put((K) oldKey, (V) oldData[ i + 1 ]);
            }
        }
    }

    /**
     * Returns a list of all values
     *
     * @return
     */
    public List<K> keys() {
        final List<K> result = new ArrayList<>(this.size());

        final int length = m_data_.length;
        for (int i = 0; i < length; i += 2) {
            final Object o = m_data_[i];
            if (o != FREE_KEY && o != REMOVED_KEY) {
                result.add((K) o);
            }
        }

        return result;
    }

    /**
     * Returns a list of all values
     *
     * @return
     */
    public List<V> values() {
        final List<V> result = new ArrayList<>(this.size());

        final int length = m_data_.length;
        for (int i = 0; i < length; i += 2) {
            final Object o = m_data_[i];
            if (o != FREE_KEY && o != REMOVED_KEY) {
                result.add((V) m_data_[i + 1]);
            }
        }

        return result;
    }

    /**
     * Clears the map, reuses the data structure by clearing it out.
     * It won't shrink the underlying array!
     */
    public void clear() {
        this.m_size_ = 0;
        Arrays.fill(m_data_, FREE_KEY);
    }

    private int getStartIndex(final Object key) {
        //key is not null here
        return key.hashCode() & m_mask_;
    }

    /** Return the least power of two greater than or equal to the specified value.
     *
     * <p>Note that this function will return 1 when the argument is 0.
     *
     * @param x a long integer smaller than or equal to 2<sup>62</sup>.
     * @return the least power of two greater than or equal to the specified value.
     */
    public static long nextPowerOfTwo(long x) {
        if (x == 0) {
            return 1;
        }

        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;

        return (x | x >> 32) + 1;
    }

    /** Returns the least power of two smaller than or equal to 2<sup>30</sup> and larger than or equal to <code>Math.ceil( expected / f )</code>.
     *
     * @param expected the expected number of elements in a hash table.
     * @param f the load factor.
     * @return the minimum possible size for a backing array.
     * @throws IllegalArgumentException if the necessary size is larger than 2<sup>30</sup>.
     */
    public static int arraySize(final int expected, final float f) {
        final long s = Math.max(2, nextPowerOfTwo((long) Math.ceil(expected / f)));
        if (s > (1 << 30)) {
            throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")");
        }
        return (int) s;
    }
}
