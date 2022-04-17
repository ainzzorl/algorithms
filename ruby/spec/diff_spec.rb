RSpec.describe Algorithms::Diff do
  describe '#script_length' do
    it 'counts script length' do
      expect(script_length([{ op: :delete, index: 0 },
                            { op: :delete, index: 1 },
                            { op: :insert, index: 3, items: %w[B G] },
                            { op: :delete, index: 5 },
                            { op: :insert, index: 7,
                              items: ['C'] }])).to eq(6)
    end
  end

  describe '#apply_script' do
    it 'handles empty script' do
      expect(apply_script('abcde'.chars, []).join).to eq('abcde')
    end

    it 'handles deleting everything' do
      expect(apply_script('abcde'.chars, [
                            { op: :delete, index: 0 },
                            { op: :delete, index: 1 },
                            { op: :delete, index: 2 },
                            { op: :delete, index: 3 },
                            { op: :delete, index: 4 }
                          ]).join).to eq('')
    end

    it 'handles inserting everything' do
      expect(apply_script(''.chars, [
                            { op: :insert, index: 0, items: %w[a a] },
                            { op: :insert, index: 1, items: %w[b b] },
                            { op: :insert, index: 2, items: %w[c c] },
                            { op: :insert, index: 3, items: %w[d d] },
                            { op: :insert, index: 4, items: %w[e e] }
                          ]).join).to eq('aabbccddee')
    end

    it 'applies script' do
      expect(apply_script('ABCABBA'.chars, [
                            { op: :delete, index: 0 },
                            { op: :delete, index: 1 },
                            { op: :insert, index: 3, items: ['B'] },
                            { op: :delete, index: 5 },
                            { op: :insert, index: 7, items: ['C'] }
                          ]).join).to eq('CBABAC')
    end
  end

  describe '#naive_diff' do
    it 'returns working script' do
      script = Algorithms::Diff.naive_diff('ABCABBA'.chars, 'CBABAC'.chars)
      expect(apply_script('ABCABBA'.chars, script)).to eq('CBABAC'.chars)
    end

    it 'handles empty destination' do
      script = Algorithms::Diff.naive_diff('ABCABBA'.chars, ''.chars)
      expect(script_length(script)).to eq('ABCABBA'.length)
      expect(apply_script('ABCABBA'.chars, script)).to eq(''.chars)
    end

    it 'handles empty source' do
      script = Algorithms::Diff.naive_diff(''.chars, 'CBABAC'.chars)
      expect(script_length(script)).to eq('CBABAC'.length)
      expect(apply_script(''.chars, script)).to eq('CBABAC'.chars)
    end
  end
end
