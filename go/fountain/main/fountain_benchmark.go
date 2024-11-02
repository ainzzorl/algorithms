package main

import (
	"crypto/rand"
	"fmt"
	"sort"
)

const (
	numAttempts = 10
)

func main() {
	fmt.Printf("In benchmark\n")
	for _, blockSize := range []int{1, 5, 10, 20} {
		for _, dataSize := range []int{20, 100, 255, 1024, 2009} {
			for _, distribution := range []Distribution{Soliton, Uniform1, Uniform2, Uniform3} {
				config := Config{
					BlockSize:    blockSize,
					Verbose:      false,
					Distribution: Distribution(distribution),
				}
				results := make([]int, numAttempts)
				total := 0
				for i := 0; i < numAttempts; i++ {
					results[i] = CountIterations(dataSize, config)
					total += results[i]
				}
				sort.Ints(results)
				best := results[0]
				worst := results[numAttempts-1]
				avg := float64(total) / float64(numAttempts)
				median := float64(results[numAttempts/2]+results[numAttempts/2+1]) / float64(2)
				fmt.Printf("data=%04d, block=%02d, distribution=%d, best=%d, worst=%d, avg=%.1f, median=%.1f.\n", dataSize, blockSize, distribution, best, worst, avg, median)
			}
		}
	}
}

func CountIterations(dataLen int, config Config) int {
	data := make([]byte, dataLen)
	rand.Read(data)
	initialData := make([]byte, dataLen)
	copy(initialData, data)
	encoder := NewEncoder(data, &config)
	decoder := NewDecoder(dataLen, &config)

	for i := 0; i < 100000; i++ {
		block := encoder.NextBlock()
		decoder.ConsumeBlock(block)
		if decoder.IsDone() {
			return i
		}
	}
	return -1
}
