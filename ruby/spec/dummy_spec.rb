RSpec.describe Algorithms::Dummy do
  let(:dummy) { Algorithms::Dummy.new }

  describe '#return_foo' do
    it 'returns foo' do
      expect(dummy.return_foo).to eq('foo')
    end
  end
end