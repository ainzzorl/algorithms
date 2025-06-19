package main

import (
	"fmt"
	"sort"
	"testing"
	"time"
)

func TestBasic(t *testing.T) {
	myMap := NewSimpleHashMap[string, string](Djb2ForParam(0))

	if myMap.size != 0 {
		t.Fatalf("Actual size: %d", myMap.size)
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
		t.Fatal("Expected a value 1")
	}
	if myMap.size != 1 {
		t.Fatal("Expected size 1")
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
		t.Fatal("Expected b value 2")
	}
	v, e = myMap.Get("a")
	if e != true {
		t.Fatal("Expected a found")
	}
	if v != "1" {
		t.Fatal("Expected a value 1")
	}
	if myMap.size != 2 {
		t.Fatal("Expected size 2")
	}

	myMap.Put("a", "11")
	v, e = myMap.Get("a")
	if e != true {
		t.Fatal("Expected a found")
	}
	if v != "11" {
		t.Fatal("Expected a value 11")
	}
	if myMap.size != 2 {
		t.Fatal("Expected size 2")
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
	if myMap.size != 1 {
		t.Fatal("Expected size 1")
	}

	myMap.Delete("a")
	_, e = myMap.Get("a")
	if e != false {
		t.Fatal("Expected a not found")
	}
	if myMap.size != 0 {
		t.Fatal("Expected size 0")
	}
}

func TestManyStrings(t *testing.T) {
	myMap := NewSimpleHashMap[string, string](Djb2ForParam(0))

	if myMap.size != 0 {
		t.Fatalf("Expected size 0, got %d", myMap.size)
	}

	// Create a standard map for comparison
	standardMap := make(map[string]string)

	// Add 1000 random strings
	for i := 0; i < 1000; i++ {
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
		if myMap.size != i+1 {
			t.Fatalf("Expected size %d, got %d", i+1, myMap.size)
		}
	}

	// Get all keys and sort them
	keys := make([]string, 0, len(standardMap))
	for k := range standardMap {
		keys = append(keys, k)
	}
	sort.Strings(keys)

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
		if myMap.size != len(standardMap) {
			t.Fatalf("Expected size %d, got %d", len(standardMap), myMap.size)
		}
	}

	if myMap.size != 0 {
		t.Fatalf("Expected size 0, got %d", myMap.size)
	}
}
