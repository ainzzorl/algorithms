package main

import (
	"math"
	"math/rand/v2"
)

const initialCapacity = 16

type entry[K comparable, V any] struct {
	Key   K
	Value V
}

type CuckooHashMap[K comparable, V any] struct {
	map1       *simpleMap[K, V]
	map2       *simpleMap[K, V]
	HashFamily func(uint64) func(K) uint64
	random     *rand.Rand
	maxSteps   int
}

type simpleMap[K comparable, V any] struct {
	buckets      []*entry[K, V]
	size         int
	HashFunction func(K) uint64
}

func NewCuckooHashMap[K comparable, V any](hashFamily func(uint64) func(K) uint64, seed uint64) *CuckooHashMap[K, V] {
	cm := CuckooHashMap[K, V]{
		HashFamily: hashFamily,
		random:     rand.New(rand.NewPCG(seed, 1024)),
	}
	cm.map1 = cm.newSimpleMap(initialCapacity)
	cm.map2 = cm.newSimpleMap(initialCapacity)
	cm.updateMaxSteps()
	return &cm
}

func (cm *CuckooHashMap[K, V]) newSimpleMap(capacity int) *simpleMap[K, V] {
	return &simpleMap[K, V]{
		buckets:      make([]*entry[K, V], capacity),
		HashFunction: cm.HashFamily(cm.random.Uint64N(1000000)),
		size:         0,
	}
}

func (cm *CuckooHashMap[K, V]) Put(key K, value V) {
	if _, ok := cm.map1.get(key); ok {
		cm.map1.buckets[cm.map1.hash(key)] = &entry[K, V]{Key: key, Value: value}
		return
	}
	if _, ok := cm.map2.get(key); ok {
		cm.map2.buckets[cm.map2.hash(key)] = &entry[K, V]{Key: key, Value: value}
		return
	}
	cm.put(key, value)
	cm.updateMaxSteps()
}

func (cm *CuckooHashMap[K, V]) put(key K, value V) {
	numSteps := 0
	whichMap := 0
	currentKey := key
	currentValue := value
	for numSteps < cm.maxSteps {
		var theMap *simpleMap[K, V]
		if whichMap == 0 {
			theMap = cm.map1
		} else {
			theMap = cm.map2
		}
		index := theMap.hash(currentKey)
		e := theMap.buckets[index]
		if e == nil {
			theMap.buckets[index] = &entry[K, V]{Key: currentKey, Value: currentValue}
			theMap.size++
			return
		}

		// Evict the entry
		evictedKey := e.Key
		evictedValue := e.Value

		// Insert the new key
		theMap.buckets[index] = &entry[K, V]{Key: currentKey, Value: currentValue}

		currentKey = evictedKey
		currentValue = evictedValue

		numSteps++
		whichMap = 1 - whichMap
	}

	cm.rehash()
	cm.Put(currentKey, currentValue)
}

func (cm *CuckooHashMap[K, V]) Get(key K) (V, bool) {
	v, ok := cm.map1.get(key)
	if ok {
		return v, ok
	}
	v, ok = cm.map2.get(key)
	if ok {
		return v, ok
	}
	return v, ok
}

func (cm *CuckooHashMap[K, V]) Delete(key K) bool {
	if _, ok := cm.map1.get(key); ok {
		cm.map1.buckets[cm.map1.hash(key)] = nil
		cm.map1.size--
		return true
	}
	if _, ok := cm.map2.get(key); ok {
		cm.map2.buckets[cm.map2.hash(key)] = nil
		cm.map2.size--
		return true
	}
	return false
}

func (cm *CuckooHashMap[K, V]) Size() int {
	return cm.map1.size + cm.map2.size
}

func (sm *simpleMap[K, V]) hash(key K) uint64 {
	return sm.HashFunction(key) % uint64(len(sm.buckets))
}

func (sm *simpleMap[K, V]) get(key K) (V, bool) {
	index := sm.hash(key)
	e := sm.buckets[index]
	if e != nil && e.Key == key {
		return e.Value, true
	}
	var zero V
	return zero, false
}

func (cm *CuckooHashMap[K, V]) rehash() {
	seed1 := cm.random.Uint64N(1000000)
	seed2 := cm.random.Uint64N(1000000)
	//totalSize := cm.Size()
	//loadFactor := float64(totalSize) / float64(len(cm.map1.buckets)+len(cm.map2.buckets))
	//fmt.Printf("Main rehashing. New seeds: %d, %d. Load factor: %f, total size: %d\n", seed1, seed2, loadFactor, totalSize)
	cm.map1.HashFunction = cm.HashFamily(seed1)
	cm.map2.HashFunction = cm.HashFamily(seed2)

	oldBuckets1 := cm.map1.buckets
	oldBuckets2 := cm.map2.buckets
	cm.map1.buckets = make([]*entry[K, V], len(oldBuckets1)*2)
	cm.map2.buckets = make([]*entry[K, V], len(oldBuckets2)*2)

	cm.map1.size = 0
	cm.map2.size = 0

	for _, v := range oldBuckets1 {
		if v != nil {
			cm.put(v.Key, v.Value)
		}
	}

	for _, v := range oldBuckets2 {
		if v != nil {
			cm.put(v.Key, v.Value)
		}
	}

	//fmt.Printf("Main rehashing done. New map1 capacity: %d, map2 capacity: %d\n", len(cm.map1.buckets), len(cm.map2.buckets))
}

func (cm *CuckooHashMap[K, V]) updateMaxSteps() {
	totalCapacity := len(cm.map1.buckets) + len(cm.map2.buckets)
	cm.maxSteps = 2 * int(math.Log2(float64(totalCapacity)))
}
