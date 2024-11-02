package main

import (
	"crypto/rand"
	"fmt"
	"reflect"
	"testing"
)

func TestFountain(t *testing.T) {
	for _, blockSize := range []int{1, 5, 10, 20} {
		for _, dataSize := range []int{20, 100, 255, 1024, 2009} {
			for _, distribution := range []Distribution{Soliton, Uniform1, Uniform2, Uniform3} {
				// for _, distribution := range []Distribution{Uniform1} {
				fmt.Printf("Testing dataSize=%d, blockSize=%d, distribution=%d\n", dataSize, blockSize, distribution)
				config := Config{
					BlockSize:    blockSize,
					Verbose:      false,
					Distribution: Distribution(distribution),
				}
				count := GetNumIterations(t, dataSize, config)
				fmt.Printf("Done after %d iterations\n", count)
			}
		}
	}
}

func GetNumIterations(t *testing.T, dataLen int, config Config) int {
	data := make([]byte, dataLen)
	rand.Read(data)
	initialData := make([]byte, dataLen)
	copy(initialData, data)
	// fmt.Printf("Data: %v\n", data)
	encoder := NewEncoder(data, &config)
	decoder := NewDecoder(dataLen, &config)

	for i := 0; i < 100000; i++ {
		block := encoder.NextBlock()
		decoder.ConsumeBlock(block)
		if decoder.IsDone() {
			if !reflect.DeepEqual(initialData, decoder.GetData()) {
				t.Fatalf("Not equal.\nExpected=%v,\n  actual=%v", data, decoder.GetData())
			}
			if !reflect.DeepEqual(initialData, data) {
				t.Fatalf("Data got modified along the way.\nInitial=%v,\nactual=%v", initialData, data)
			}
			if decoder.HasPendingBlocks() {
				t.Fatalf("Decoder still has pending blocks")
			}
			// fmt.Printf("Equal!\n")
			// fmt.Printf("Decoded data: %v\n", decoder.GetData())
			return i
		}
	}
	t.Fatal("Never decoded")
	return 0
}
