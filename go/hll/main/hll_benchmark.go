package main

import (
	"fmt"
	"math/rand"
)

func main() {
	cnt := 100 * 1000 * 1000
	max_cardinality := int64(1 * 1000 * 1000)
	seed := int64(42)

	fmt.Printf("Counting true value\n")
	true_val := trueValue(newRandomStream(cnt, max_cardinality, seed))
	fmt.Printf("True value: %d\n", true_val)

	var num_buckets int32
	var error_ratio float64
	var e int64

	num_buckets = 8
	e = EstimateHll(newRandomStream(cnt, max_cardinality, seed), HllOptions{
		Num_buckets: num_buckets,
	})
	error_ratio = float64(e-true_val) / float64(true_val)
	fmt.Printf("Estimate: %d (%d buckets), true value: %d, error: %.2f%%\n", e, num_buckets, true_val, error_ratio*100)

	num_buckets = 32
	e = EstimateHll(newRandomStream(cnt, max_cardinality, seed), HllOptions{
		Num_buckets: num_buckets,
	})
	error_ratio = float64(e-true_val) / float64(true_val)
	fmt.Printf("Estimate: %d (%d buckets), true value: %d, error: %.2f%%\n", e, num_buckets, true_val, error_ratio*100)

	num_buckets = 64
	e = EstimateHll(newRandomStream(cnt, max_cardinality, seed), HllOptions{
		Num_buckets: num_buckets,
	})
	error_ratio = float64(e-true_val) / float64(true_val)
	fmt.Printf("Estimate: %d (%d buckets), true value: %d, error: %.2f%%\n", e, num_buckets, true_val, error_ratio*100)

	num_buckets = 128
	e = EstimateHll(newRandomStream(cnt, max_cardinality, seed), HllOptions{
		Num_buckets: num_buckets,
	})
	error_ratio = float64(e-true_val) / float64(true_val)
	fmt.Printf("Estimate: %d (%d buckets), true value: %d, error: %.2f%%\n", e, num_buckets, true_val, error_ratio*100)
}

type randomStream struct {
	source          rand.Source64
	stream_len      int64
	next_idx        int64
	max_cardinality int64
}

func newRandomStream(stream_len int, max_cardinality int64, seed int64) HllInt64Stream {
	return &randomStream{
		source:          rand.New(rand.NewSource(seed)),
		stream_len:      int64(stream_len),
		next_idx:        0,
		max_cardinality: max_cardinality,
	}
}

func (s randomStream) HasNext() bool {
	return s.next_idx < s.stream_len
}

func (s *randomStream) Next() int64 {
	s.next_idx++
	return s.source.Int63() % s.max_cardinality
}

func trueValue(stream HllInt64Stream) int64 {
	vals := map[int64]bool{}

	for stream.HasNext() {
		element := stream.Next()
		vals[element] = true
	}
	return int64(len(vals))
}
