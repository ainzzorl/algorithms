package main

type HashMapInterface[K comparable, V any] interface {
	Put(key K, value V)
	Get(key K) (V, bool)
	Delete(key K) bool
	Size() int
}
