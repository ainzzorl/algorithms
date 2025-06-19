package main

import (
	"fmt"
	"sort"
	"testing"
	"time"
)

func TestCuckooTestBasic(t *testing.T) {
	myMap := NewCuckooHashMap[string, string](Djb2ForParam, 42)

	if myMap.Size() != 0 {
		t.Fatalf("Actual size: %d", myMap.Size())
	}

	v, e := myMap.Get("a")
	_ = v
	if e != false {
		t.Fatal("Expected a not found")
	}
	myMap.Put("a", "1")
	v, e = myMap.Get("a")
	if e != true {
		t.Fatal("Expected a found")
	}
	if v != "1" {
		t.Fatal("Expected a value 1, got ", v)
	}
	if myMap.Size() != 1 {
		t.Fatal("Expected size 1, got ", myMap.Size())
	}

	_, e = myMap.Get("b")
	if e != false {
		t.Fatal("Expected b not found")
	}
	myMap.Put("b", "2")
	v, e = myMap.Get("b")
	if e != true {
		t.Fatal("Expected b found")
	}
	if v != "2" {
		t.Fatal("Expected b value 2, got ", v)
	}
	v, e = myMap.Get("a")
	if e != true {
		t.Fatal("Expected a found")
	}
	if v != "1" {
		t.Fatal("Expected a value 1, got ", v)
	}
	if myMap.Size() != 2 {
		t.Fatal("Expected size 2, got ", myMap.Size())
	}

	myMap.Put("a", "11")
	v, e = myMap.Get("a")
	if e != true {
		t.Fatal("Expected a found")
	}
	if v != "11" {
		t.Fatal("Expected a value 11, got ", v)
	}
	if myMap.Size() != 2 {
		t.Fatal("Expected size 2, got ", myMap.Size())
	}

	myMap.Delete("b")
	_, e = myMap.Get("b")
	if e != false {
		t.Fatal("Expected b not found")
	}
	_, e = myMap.Get("a")
	if e != true {
		t.Fatal("Expected a found")
	}
	if myMap.Size() != 1 {
		t.Fatal("Expected size 1, got ", myMap.Size())
	}

	myMap.Delete("a")
	_, e = myMap.Get("a")
	if e != false {
		t.Fatal("Expected a not found")
	}
	if myMap.Size() != 0 {
		t.Fatal("Expected size 0, got ", myMap.Size())
	}
}

func TestCuckooManyStrings(t *testing.T) {
	myMap := NewCuckooHashMap[string, string](Djb2ForParam, 42)

	if myMap.Size() != 0 {
		t.Fatalf("Expected size 0, got %d", myMap.Size())
	}

	// Create a standard map for comparison
	standardMap := make(map[string]string)

	// Add 10000 random strings
	for i := 0; i < 1000000; i++ {
		key := fmt.Sprintf("key-%d-%d", i, time.Now().UnixNano())
		value := fmt.Sprintf("%d", i)

		// Verify key doesn't exist before adding
		_, exists := myMap.Get(key)
		if exists {
			t.Fatalf("Key %s should not exist before adding", key)
		}

		// Add to both maps
		myMap.Put(key, value)
		standardMap[key] = value

		// Verify key exists and has correct value
		v, exists := myMap.Get(key)
		if !exists {
			t.Fatalf("Key %s should exist after adding", key)
		}
		if v != value {
			t.Fatalf("Expected value %s for key %s, got %s", value, key, v)
		}
		if myMap.Size() != i+1 {
			t.Fatalf("Expected size %d, got %d", i+1, myMap.Size())
		}
	}

	// Get all keys and sort them
	keys := make([]string, 0, len(standardMap))
	for k := range standardMap {
		keys = append(keys, k)
	}
	sort.Strings(keys)

	for _, key := range keys {
		v, exists := myMap.Get(key)
		if !exists {
			t.Fatalf("Key %s should exist", key)
		}
		if v != standardMap[key] {
			t.Fatalf("Key %s should have value %s, got %s", key, standardMap[key], v)
		}
	}

	// Remove all keys and verify
	for _, key := range keys {
		// Verify key exists before removing
		_, exists := myMap.Get(key)
		if !exists {
			t.Fatalf("Key %s should exist before removing", key)
		}

		// Remove from both maps
		myMap.Delete(key)
		delete(standardMap, key)

		// Verify key doesn't exist after removing
		_, exists = myMap.Get(key)
		if exists {
			t.Fatalf("Key %s should not exist after removing", key)
		}
		if myMap.Size() != len(standardMap) {
			t.Fatalf("Expected size %d, got %d", len(standardMap), myMap.Size())
		}
	}

	if myMap.Size() != 0 {
		t.Fatalf("Expected size 0, got %d", myMap.Size())
	}
}
