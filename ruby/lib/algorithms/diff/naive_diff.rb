module Algorithms
  module Diff
    def naive_diff(a, b)
      a.map.with_index { |c, i| { op: :delete, index: i } } +
        b.map.with_index { |c, i| { op: :insert, index: i, items: [c] } }
    end
    module_function :naive_diff
  end
end
