package main

import (
	"crypto/sha256"
	"encoding/binary"
	"fmt"
	"math"
)

type HllOptions struct {
	Num_buckets int32
}

type HllInt64Stream interface {
	HasNext() bool
	Next() int64
}

func getHash(element int64) []byte {
	// TODO: sha is way too slow for this
	h := sha256.New()
	bs := make([]byte, 8)
	binary.LittleEndian.PutUint64(bs, uint64(element))
	h.Write(bs)
	return h.Sum(nil)
}

func EstimateHll(stream HllInt64Stream, options HllOptions) int64 {
	buckets := make([]uint8, options.Num_buckets)
	var num_bucket_bits = 1
	var c = options.Num_buckets
	// TODO: check if power of 2
	for c > 2 {
		num_bucket_bits++
		c /= 2
	}

	c = options.Num_buckets
	num_bucket_bytes := 1
	for c > 256 {
		num_bucket_bytes++
		c /= 256
	}

	//fmt.Printf("Num buckets: %d, bucket bytes: %d, bucket bits: %d\n", options.Num_buckets, num_bucket_bytes, num_bucket_bits)

	for stream.HasNext() {
		element := stream.Next()
		hash := getHash(element)
		bucket := getBucket(hash, num_bucket_bits)
		leading_zeros := uint8(countLeadingZeros(hash[num_bucket_bytes:])) + 1
		if buckets[bucket] < leading_zeros {
			buckets[bucket] = leading_zeros
		}
	}

	//fmt.Printf("Bucket counts: %v\n", buckets)

	mean := getMean(buckets)
	fmt.Printf("Mean: %f\n", mean)
	// fmt.Printf("Mean estimate: %f\n", mean*float64(options.Num_buckets))

	multiplier := getMultiplier(options.Num_buckets)
	fmt.Printf("Multiplier: %.2f\n", multiplier)

	res := multiplier * mean * float64(options.Num_buckets)

	return int64(res)
}

func getMultiplier(num_buckets int32) float64 {
	switch num_buckets {
	case 1, 2, 4, 8, 16:
		return 0.673
	case 32:
		return 0.697
	case 64:
		return 0.709
	default:
		return 0.7213 / (1 + (1.079 / float64(num_buckets)))
	}
}

func getMean(vals []uint8) float64 {
	sum := 0.0
	for _, v := range vals {
		sum += math.Pow(2, -float64(v))
	}
	return float64(len(vals)) / sum
}

func countLeadingZeros(b []byte) int {
	for i := 0; i < len(b)*8; i++ {
		byte_idx := i / 8
		bit_idx := byte(i % 8)
		bt := b[byte_idx]
		bit := (bt >> bit_idx) & 1
		if bit == 1 {
			return i
		}
	}
	return len(b) * 8
}

func getBucket(b []byte, num_bucket_bits int) int {
	res := 0
	for i := 0; i < num_bucket_bits; i++ {
		byte_idx := i / 8
		bit_idx := byte(i % 8)
		bt := b[byte_idx]
		bit := (bt >> bit_idx) & 1
		res = res*2 + int(bit)
	}
	return res
}
