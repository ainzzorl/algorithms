package main

import (
	"fmt"
	"math"
	"math/rand"
)

type Config struct {
	BlockSize    int
	Verbose      bool
	Distribution Distribution
}

type Distribution int

const (
	Soliton  Distribution = 0
	Uniform1 Distribution = 1
	Uniform2 Distribution = 2
	Uniform3 Distribution = 3
)

type EncodedBlock struct {
	Data []byte
	Seed int64
}

type Encoder struct {
	config *Config

	chunks    [][]byte
	numChunks int
}

func NewEncoder(data []byte, config *Config) *Encoder {
	encoder := new(Encoder)
	encoder.config = config
	encoder.numChunks = int(math.Ceil(float64(len(data)) / float64(config.BlockSize)))
	encoder.chunks = make([][]byte, encoder.numChunks)
	for i := 0; i < encoder.numChunks; i++ {
		s := i * config.BlockSize
		e := (i + 1) * config.BlockSize
		if e > len(data) {
			e = len(data)
		}
		encoder.chunks[i] = data[s:e]
		for len(encoder.chunks[i]) < config.BlockSize {
			encoder.chunks[i] = append(encoder.chunks[i], 0)
		}
	}
	return encoder
}

func xorBlocks(x *[]byte, y *[]byte) {
	for i := 0; i < len(*x); i++ {
		(*x)[i] ^= (*y)[i]
	}
}

func (encoder *Encoder) NextBlock() *EncodedBlock {
	seed := rand.Int63()
	chunkIds := getChunkIds(seed, encoder.numChunks, encoder.config.Distribution)
	if encoder.config.Verbose {
		fmt.Printf("NextBlock. Chunk ids: %v\n", chunkIds)
	}
	block := new(EncodedBlock)
	block.Seed = seed

	block.Data = make([]byte, encoder.config.BlockSize)
	for _, chunkId := range chunkIds {
		xorBlocks(&block.Data, &encoder.chunks[chunkId])
	}
	return block
}

func getChunkIds(seed int64, numChunks int, distribution Distribution) []int {
	r := rand.New(rand.NewSource(seed))
	num := getNumChunks(numChunks, r, distribution)
	result := make([]int, num)
	for i := 0; i < num; {
		c := r.Int() % numChunks
		contains := false
		for j := 0; j < i; j++ {
			if result[j] == c {
				contains = true
			}
		}
		if !contains {
			result[i] = c
			i++
		}
	}
	return result
}

func getNumChunks(max int, r *rand.Rand, distribution Distribution) int {
	switch distribution {
	case Soliton:
		val := r.Float64()
		agg := float64(1) / float64(max)
		for i := 1; i <= max; i++ {
			if agg >= val {
				// fmt.Printf("Generating num chunks. val=%f, res=%d, agg=%f\n", val, i, agg)
				return i
			}
			agg += float64(1) / float64(i*(i+1))
		}
		fmt.Printf("Ran out of candidates, val=%f, agg=%f\n", val, agg)
		return max
	case Uniform1:
		return 1
	case Uniform2:
		res := r.Int()%2 + 1
		if res > max {
			res = max
		}
		return res
	case Uniform3:
		res := r.Int()%3 + 1
		if res > max {
			res = max
		}
		return res
	default:
		panic("Unrecognized distribution")
	}
}

type Decoder struct {
	config                   *Config
	decodedChunks            [][]byte
	numChunks                int
	isChunkDecoded           []bool
	dataLen                  int
	pendingBlocks            map[int]*PendingBlock
	chunkIdToPendingBlockIds map[int][]int
	blockIndex               int
}

type PendingBlock struct {
	data     []byte
	chunkIds []int
	blockId  int
}

func NewDecoder(dataLen int, config *Config) *Decoder {
	decoder := new(Decoder)
	decoder.config = config
	decoder.dataLen = dataLen
	decoder.numChunks = int(math.Ceil(float64(dataLen) / float64(config.BlockSize)))
	decoder.decodedChunks = make([][]byte, decoder.numChunks)
	decoder.isChunkDecoded = make([]bool, decoder.numChunks)
	decoder.pendingBlocks = make(map[int]*PendingBlock)
	decoder.chunkIdToPendingBlockIds = make(map[int][]int)
	for c := 0; c < decoder.numChunks; c++ {
		decoder.chunkIdToPendingBlockIds[c] = make([]int, 0)
	}
	decoder.blockIndex = 0
	return decoder
}

func (decoder *Decoder) HasPendingBlocks() bool {
	if len(decoder.chunkIdToPendingBlockIds) > 0 && decoder.config.Verbose {
		fmt.Printf("Decoder still has %d values in chunkIdToPendingBlockIds\n", len(decoder.chunkIdToPendingBlockIds))
	}
	if len(decoder.pendingBlocks) > 0 && decoder.config.Verbose {
		fmt.Printf("Decoder still has %d pending blocks\n", len(decoder.pendingBlocks))
	}
	return len(decoder.chunkIdToPendingBlockIds) > 0 || len(decoder.pendingBlocks) > 0
}

func (decoder *Decoder) ConsumeBlock(block *EncodedBlock) {
	chunkIds := getChunkIds(block.Seed, decoder.numChunks, decoder.config.Distribution)
	pendingBlock := PendingBlock{data: block.Data, chunkIds: chunkIds, blockId: decoder.blockIndex}
	decoder.processNewBlock(&pendingBlock)
	decoder.blockIndex++
}

func (decoder *Decoder) processNewBlock(pendingBlock *PendingBlock) {
	newChunkIds := make([]int, len(pendingBlock.chunkIds))
	copy(newChunkIds, pendingBlock.chunkIds)
	for _, chunkId := range pendingBlock.chunkIds {
		if decoder.isChunkDecoded[chunkId] {
			xorBlocks(&pendingBlock.data, &decoder.decodedChunks[chunkId])
			newChunkIds = removeFromIntSlice(newChunkIds, chunkId)
		}
	}
	pendingBlock.chunkIds = newChunkIds
	if decoder.config.Verbose {
		fmt.Printf("Chunks after applying known: %v\n", pendingBlock.chunkIds)
	}
	if len(pendingBlock.chunkIds) == 1 && !decoder.isChunkDecoded[pendingBlock.chunkIds[0]] {
		decoder.decode(pendingBlock, pendingBlock.chunkIds[0])
	} else if len(pendingBlock.chunkIds) > 0 {
		decoder.pendingBlocks[decoder.blockIndex] = pendingBlock
		for _, c := range pendingBlock.chunkIds {
			decoder.chunkIdToPendingBlockIds[c] = append(decoder.chunkIdToPendingBlockIds[c], pendingBlock.blockId)
		}
	}
}

func (decoder *Decoder) decode(pendingBlock *PendingBlock, chunkId int) {
	if decoder.config.Verbose {
		fmt.Printf("Decoded chunk: %d.\nData: %v\n", chunkId, pendingBlock.data)
	}
	decoder.decodedChunks[chunkId] = make([]byte, len(pendingBlock.data))
	copy(decoder.decodedChunks[chunkId], pendingBlock.data)
	decoder.isChunkDecoded[chunkId] = true
	for _, pendingBlockId := range decoder.chunkIdToPendingBlockIds[chunkId] {
		pb := decoder.pendingBlocks[pendingBlockId]
		if pb == nil {
			if decoder.config.Verbose {
				fmt.Printf("Pending block %d is already deleted\n", pendingBlockId)
			}
			continue
		}
		xorBlocks(&pb.data, &decoder.decodedChunks[chunkId])
		pb.chunkIds = removeFromIntSlice(pb.chunkIds, chunkId)
		if decoder.config.Verbose {
			fmt.Printf("Removed chunk %d from pending block %d, remaining chunks in it: %v\n", chunkId, pb.blockId, pb.chunkIds)
		}
		if len(pb.chunkIds) == 1 {
			chunkIdToRemove := pb.chunkIds[0]
			if !decoder.isChunkDecoded[pb.chunkIds[0]] {
				decoder.decode(pb, pb.chunkIds[0])
			}
			delete(decoder.chunkIdToPendingBlockIds, chunkIdToRemove)
			delete(decoder.pendingBlocks, pendingBlockId)
		}
	}
	delete(decoder.chunkIdToPendingBlockIds, chunkId)
}

func removeFromIntSlice(l []int, item int) []int {
	for i, other := range l {
		if other == item {
			return append(l[:i], l[i+1:]...)
		}
	}
	return l
}

func (decoder *Decoder) IsDone() bool {
	for _, v := range decoder.isChunkDecoded {
		if !v {
			return false
		}
	}
	return true
}

func (decoder *Decoder) GetData() []byte {
	result := make([]byte, 0)
	for _, chunk := range decoder.decodedChunks {
		result = append(result, chunk...)
	}
	return result[:decoder.dataLen]
}
