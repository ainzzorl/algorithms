package main

const INITIAL_CAPACITY = 16
const LOAD_FACTOR_THRESHOLD = 0.75

type Entry[K comparable, V any] struct {
	Key   K
	Value V
	Next  *Entry[K, V]
}

type SimpleHashMap[K comparable, V any] struct {
	buckets      []*Entry[K, V]
	size         int
	HashFunction func(K) uint64
}

func NewSimpleHashMap[K comparable, V any](hf func(K) uint64) *SimpleHashMap[K, V] {
	return &SimpleHashMap[K, V]{
		buckets:      make([]*Entry[K, V], INITIAL_CAPACITY),
		HashFunction: hf,
	}
}

func (hm *SimpleHashMap[K, V]) hash(key K) uint64 {
	return hm.HashFunction(key) % uint64(len(hm.buckets))
}

// rehash doubles the capacity and reinserts all entries
func (hm *SimpleHashMap[K, V]) rehash() {
	oldBuckets := hm.buckets
	hm.buckets = make([]*Entry[K, V], len(oldBuckets)*2)
	hm.size = 0

	// Reinsert all entries
	for _, head := range oldBuckets {
		for e := head; e != nil; e = e.Next {
			hm.Put(e.Key, e.Value)
		}
	}
}

// Put inserts or updates a key-value pair
func (hm *SimpleHashMap[K, V]) Put(key K, value V) {
	// Check if rehashing is needed
	if float64(hm.size)/float64(len(hm.buckets)) > LOAD_FACTOR_THRESHOLD {
		hm.rehash()
	}

	index := hm.hash(key)
	head := hm.buckets[index]

	// Update value if key exists
	for e := head; e != nil; e = e.Next {
		if e.Key == key {
			e.Value = value
			return
		}
	}

	// Insert new entry
	newEntry := &Entry[K, V]{Key: key, Value: value, Next: head}
	hm.buckets[index] = newEntry
	hm.size++
}

// Get retrieves the value for a key
func (hm *SimpleHashMap[K, V]) Get(key K) (V, bool) {
	index := hm.hash(key)
	for e := hm.buckets[index]; e != nil; e = e.Next {
		if e.Key == key {
			return e.Value, true
		}
	}
	var zero V
	return zero, false
}

// Delete removes a key-value pair
func (hm *SimpleHashMap[K, V]) Delete(key K) bool {
	index := hm.hash(key)
	current := hm.buckets[index]
	var prev *Entry[K, V]

	for current != nil {
		if current.Key == key {
			if prev == nil {
				hm.buckets[index] = current.Next
			} else {
				prev.Next = current.Next
			}
			hm.size--
			return true
		}
		prev = current
		current = current.Next
	}
	return false
}

// Size returns the number of key-value pairs
func (hm *SimpleHashMap[K, V]) Size() int {
	return hm.size
}
