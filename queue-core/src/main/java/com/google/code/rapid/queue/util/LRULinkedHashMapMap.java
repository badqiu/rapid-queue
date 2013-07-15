package com.google.code.rapid.queue.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRULinkedHashMapMap<K,V> extends LinkedHashMap<K,V>
{
    protected final int _maxEntries;
    
    ReadWriteLock globalLock = new ReentrantReadWriteLock();
    private Lock readLock = globalLock.readLock();
    private Lock writeLock = globalLock.writeLock();
    
    public LRULinkedHashMapMap(int initialEntries, int maxEntries)
    {
        super(initialEntries, 0.8f, true);
        _maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest)
    {
        return size() > _maxEntries;
    }
    
    @Override
    public V get(Object key) {
    	try {
    		readLock.lock();
    		return super.get(key);
    	}finally {
    		readLock.unlock();
    	}
    }
    
    public V put(K key, V value) {
    	try {
    		writeLock.lock();
    		return super.put(key, value);
    	}finally {
    		writeLock.unlock();
    	}
    }

}
