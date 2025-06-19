package main

import "math/rand/v2"

func Djb2ForParam(seed uint64) func(string) uint64 {
	return func(s string) uint64 {
		r := rand.New(rand.NewPCG(seed, 1024))
		s1 := r.Uint64N(1000000)
		s2 := r.Uint64N(100)
		for s2%2 == 0 {
			s2 = r.Uint64N(100)
		}
		return djb2Param(s, s1, s2)
	}
}

func djb2Param(s string, seed1 uint64, seed2 uint64) uint64 {
	hash := seed1
	for i := 0; i < len(s); i++ {
		hash = hash*seed2 + uint64(s[i])
	}
	return hash
}
