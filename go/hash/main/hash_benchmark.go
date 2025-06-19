package main

import (
	"fmt"
	"math/rand/v2"
	"testing"
	"time"
)

const (
	NUM_ITEMS            = 1_000_000
	BENCHMARK_ITERATIONS = 5
)

// Benchmark data structures
type BenchmarkResult struct {
	Operation      string
	Implementation string
	Duration       time.Duration
	ItemsPerSec    float64
}

// HashMap interface for benchmarking
type HashMap interface {
	Put(key, value string)
	Get(key string) (string, bool)
	Delete(key string) bool
}

// BuiltinMap wrapper to implement HashMapInterface
type BuiltinMapWrapper struct {
	data map[string]string
}

func NewBuiltinMapWrapper() *BuiltinMapWrapper {
	return &BuiltinMapWrapper{
		data: make(map[string]string),
	}
}

func (b *BuiltinMapWrapper) Put(key, value string) {
	b.data[key] = value
}

func (b *BuiltinMapWrapper) Get(key string) (string, bool) {
	value, ok := b.data[key]
	return value, ok
}

func (b *BuiltinMapWrapper) Delete(key string) bool {
	if _, exists := b.data[key]; exists {
		delete(b.data, key)
		return true
	}
	return false
}

func (b *BuiltinMapWrapper) Size() int {
	return len(b.data)
}

// Generate test data
func generateTestData(n int) []string {
	data := make([]string, n)
	for i := 0; i < n; i++ {
		data[i] = fmt.Sprintf("key_%d_value_%d", i, i*2)
	}
	return data
}

// Generic benchmark function
func benchmarkHashMap(impl HashMapInterface[string, string], data []string, operation string, implName string) BenchmarkResult {
	var duration time.Duration

	switch operation {
	case "Insert":
		start := time.Now()
		for _, key := range data {
			impl.Put(key, key+"_value")
		}
		duration = time.Since(start)

	case "Lookup":
		start := time.Now()
		for _, key := range data {
			_, _ = impl.Get(key)
		}
		duration = time.Since(start)

	case "Delete":
		start := time.Now()
		for _, key := range data {
			impl.Delete(key)
		}
		duration = time.Since(start)
	}

	return BenchmarkResult{
		Operation:      operation,
		Implementation: implName,
		Duration:       duration,
		ItemsPerSec:    float64(len(data)) / duration.Seconds(),
	}
}

// Benchmark all implementations for a specific operation
func benchmarkOperation(data []string, operation string) []BenchmarkResult {
	results := make([]BenchmarkResult, 0)

	// Benchmark CuckooHashMap
	hashFamily := func(seed uint64) func(string) uint64 {
		return Djb2ForParam(seed)
	}
	cuckooMap := NewCuckooHashMap[string, string](hashFamily, 42)
	if operation == "Lookup" || operation == "Delete" {
		// Pre-populate for lookup and delete operations
		for _, key := range data {
			cuckooMap.Put(key, key+"_value")
		}
	}
	results = append(results, benchmarkHashMap(cuckooMap, data, operation, "CuckooHashMap"))

	// Benchmark SimpleHashMap
	hashFunc := Djb2ForParam(42)
	simpleMap := NewSimpleHashMap[string, string](hashFunc)
	if operation == "Lookup" || operation == "Delete" {
		// Pre-populate for lookup and delete operations
		for _, key := range data {
			simpleMap.Put(key, key+"_value")
		}
	}
	results = append(results, benchmarkHashMap(simpleMap, data, operation, "SimpleHashMap"))

	// Benchmark BuiltinMap
	builtinMap := NewBuiltinMapWrapper()
	if operation == "Lookup" || operation == "Delete" {
		// Pre-populate for lookup and delete operations
		for _, key := range data {
			builtinMap.Put(key, key+"_value")
		}
	}
	results = append(results, benchmarkHashMap(builtinMap, data, operation, "BuiltinMap"))

	return results
}

// Run comprehensive benchmark
func runComprehensiveBenchmark() {
	fmt.Printf("=== Hash Map Performance Benchmark ===\n")
	fmt.Printf("Testing %d items with %d iterations\n\n", NUM_ITEMS, BENCHMARK_ITERATIONS)

	// Generate test data
	data := generateTestData(NUM_ITEMS)

	// Collect all results
	allResults := make([]BenchmarkResult, 0)

	// Run benchmarks multiple times for more accurate results
	for i := 0; i < BENCHMARK_ITERATIONS; i++ {
		fmt.Printf("Iteration %d/%d...\n", i+1, BENCHMARK_ITERATIONS)

		// Shuffle data for each iteration to avoid bias
		rand.Shuffle(len(data), func(i, j int) {
			data[i], data[j] = data[j], data[i]
		})

		// Run all operations
		operations := []string{"Insert", "Lookup", "Delete"}
		for _, operation := range operations {
			allResults = append(allResults, benchmarkOperation(data, operation)...)
		}
	}

	// Calculate averages
	averages := calculateAverages(allResults)

	// Print results
	printResults(averages)
}

// Calculate average results across iterations
func calculateAverages(results []BenchmarkResult) []BenchmarkResult {
	// Group results by operation and implementation
	groups := make(map[string][]BenchmarkResult)
	for _, result := range results {
		key := result.Operation + "_" + result.Implementation
		groups[key] = append(groups[key], result)
	}

	// Calculate averages
	averages := make([]BenchmarkResult, 0)
	for _, group := range groups {
		if len(group) == 0 {
			continue
		}

		var totalDuration time.Duration
		var totalItemsPerSec float64

		for _, result := range group {
			totalDuration += result.Duration
			totalItemsPerSec += result.ItemsPerSec
		}

		avgDuration := totalDuration / time.Duration(len(group))
		avgItemsPerSec := totalItemsPerSec / float64(len(group))

		averages = append(averages, BenchmarkResult{
			Operation:      group[0].Operation,
			Implementation: group[0].Implementation,
			Duration:       avgDuration,
			ItemsPerSec:    avgItemsPerSec,
		})
	}

	return averages
}

// Print formatted results
func printResults(results []BenchmarkResult) {
	fmt.Printf("\n=== Benchmark Results (Average of %d iterations) ===\n\n", BENCHMARK_ITERATIONS)

	// Group by operation
	operations := []string{"Insert", "Lookup", "Delete"}

	for _, operation := range operations {
		fmt.Printf("--- %s Operation ---\n", operation)
		fmt.Printf("%-15s %-15s %-15s\n", "Implementation", "Duration", "Items/sec")
		fmt.Printf("%-15s %-15s %-15s\n", "-------------", "--------", "---------")

		// Find results for this operation
		var opResults []BenchmarkResult
		for _, result := range results {
			if result.Operation == operation {
				opResults = append(opResults, result)
			}
		}

		// Sort by performance (items/sec, descending)
		for i := 0; i < len(opResults); i++ {
			for j := i + 1; j < len(opResults); j++ {
				if opResults[i].ItemsPerSec < opResults[j].ItemsPerSec {
					opResults[i], opResults[j] = opResults[j], opResults[i]
				}
			}
		}

		// Print sorted results
		for _, result := range opResults {
			fmt.Printf("%-15s %-15s %-15.0f\n",
				result.Implementation,
				result.Duration.String(),
				result.ItemsPerSec)
		}
		fmt.Println()
	}

	// Print summary
	fmt.Printf("=== Performance Summary ===\n")
	for _, operation := range operations {
		var bestResult BenchmarkResult
		var worstResult BenchmarkResult
		var opResults []BenchmarkResult

		for _, result := range results {
			if result.Operation == operation {
				opResults = append(opResults, result)
			}
		}

		if len(opResults) == 0 {
			continue
		}

		bestResult = opResults[0]
		worstResult = opResults[0]

		for _, result := range opResults {
			if result.ItemsPerSec > bestResult.ItemsPerSec {
				bestResult = result
			}
			if result.ItemsPerSec < worstResult.ItemsPerSec {
				worstResult = result
			}
		}

		ratio := bestResult.ItemsPerSec / worstResult.ItemsPerSec
		fmt.Printf("%s: %s is %.1fx faster than %s\n",
			operation,
			bestResult.Implementation,
			ratio,
			worstResult.Implementation)
	}
}

// Generic benchmark function for Go testing
func runBenchmark(b *testing.B, implName string, operation string) {
	data := generateTestData(NUM_ITEMS)

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		var impl HashMapInterface[string, string]

		switch implName {
		case "CuckooHashMap":
			hashFamily := func(seed uint64) func(string) uint64 {
				return Djb2ForParam(seed)
			}
			impl = NewCuckooHashMap[string, string](hashFamily, 42)
		case "SimpleHashMap":
			hashFunc := Djb2ForParam(42)
			impl = NewSimpleHashMap[string, string](hashFunc)
		case "BuiltinMap":
			impl = NewBuiltinMapWrapper()
		}

		switch operation {
		case "Insert":
			for _, key := range data {
				impl.Put(key, key+"_value")
			}
		case "Lookup":
			// Pre-populate
			for _, key := range data {
				impl.Put(key, key+"_value")
			}
			b.ResetTimer()
			for _, key := range data {
				_, _ = impl.Get(key)
			}
		case "Delete":
			// Pre-populate
			for _, key := range data {
				impl.Put(key, key+"_value")
			}
			b.ResetTimer()
			for _, key := range data {
				impl.Delete(key)
			}
		}
	}
}

// Go testing benchmark functions
func BenchmarkCuckooHashMapInsert(b *testing.B) {
	runBenchmark(b, "CuckooHashMap", "Insert")
}

func BenchmarkSimpleHashMapInsert(b *testing.B) {
	runBenchmark(b, "SimpleHashMap", "Insert")
}

func BenchmarkBuiltinMapInsert(b *testing.B) {
	runBenchmark(b, "BuiltinMap", "Insert")
}

func BenchmarkCuckooHashMapLookup(b *testing.B) {
	runBenchmark(b, "CuckooHashMap", "Lookup")
}

func BenchmarkSimpleHashMapLookup(b *testing.B) {
	runBenchmark(b, "SimpleHashMap", "Lookup")
}

func BenchmarkBuiltinMapLookup(b *testing.B) {
	runBenchmark(b, "BuiltinMap", "Lookup")
}

func BenchmarkCuckooHashMapDelete(b *testing.B) {
	runBenchmark(b, "CuckooHashMap", "Delete")
}

func BenchmarkSimpleHashMapDelete(b *testing.B) {
	runBenchmark(b, "SimpleHashMap", "Delete")
}

func BenchmarkBuiltinMapDelete(b *testing.B) {
	runBenchmark(b, "BuiltinMap", "Delete")
}

// Main function to run the benchmark
func main() {
	runComprehensiveBenchmark()
}
