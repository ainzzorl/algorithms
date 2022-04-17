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
      expect(script_length(script)).to eq(5)
      expect(apply_script('ABCABBA'.chars, script)).to eq('CBABAC'.chars)
    end

    it 'handles equal strings' do
      script = Algorithms::Diff.naive_diff('ABCABBA'.chars, 'ABCABBA'.chars)
      expect(script_length(script)).to eq(0)
      expect(apply_script('ABCABBA'.chars, script)).to eq('ABCABBA'.chars)
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

  describe '#myers_diff' do
    def compare_to_naive(a, b)
      myers_script = Algorithms::Diff.myers_diff(a.chars, b.chars)
      naive_script = Algorithms::Diff.naive_diff(a.chars, b.chars)
      expect(script_length(myers_script)).to eq(script_length(naive_script))
      expect(apply_script(a.chars, myers_script)).to eq(b.chars)
    end

    it 'handles simple test' do
      compare_to_naive('ABCABBA', 'CBABAC')
    end

    it 'handles equal strings' do
      compare_to_naive('ABCABBA', 'ABCABBA')
    end

    it 'handles empty destination' do
      compare_to_naive('ABCABBA', '')
    end

    it 'handles empty source' do
      compare_to_naive('', 'CBABAC')
    end

    it 'handles long test' do
      compare_to_naive(
        'ACCCBAECBCADEBEBBBABDCADACCBEBDBCBACCCCADCEDDBCDADEDDBBBDECEACEBBDCDDEDEACBBCDDABBADAEEADAECCDCEEEAB',
        'AEABEDCAEDECCEBAADEEDDAEEAEDDABBBCABEEADACDCABBADEBABEACDAEDDCAADEECEEEDAAEDEBBBCECEEAEEEECEDAA'
      )
    end
  end
end
