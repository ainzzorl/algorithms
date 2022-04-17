module Algorithms
  module Diff
    # Naive diff algorithm.
    # O(NM) time and space complexity.
    # TODO: squash "insert operations",
    # otherwise application of the script depends on the order of the operations.
    def naive_diff(a, b)
      dp = Array.new(b.length + 1) { Array.new(a.length + 1) }
      (0..a.length).each do |i|
        dp[0][i] = i
      end
      (0..b.length).each do |i|
        dp[i][0] = i
      end

      (1..b.length).each do |i|
        (1..a.length).each do |j|
          dp[i][j] = if a[j - 1] == b[i - 1]
                       dp[i - 1][j - 1]
                     else
                       [dp[i - 1][j], dp[i][j - 1]].min + 1
                     end
        end
      end

      script = []

      i = b.length
      j = a.length
      while i > 0 || j > 0
        if i > 0 && j > 0 && a[j - 1] == b[i - 1]
          i -= 1
          j -= 1
          next
        end
        if j > 0 && (i == 0 || dp[i][j - 1] < dp[i - 1][j])
          script << { op: :delete, index: j - 1 }
          j -= 1
        else
          script << { op: :insert, index: j, items: [b[i - 1]] }
          i -= 1
        end
      end

      script
    end
    module_function :naive_diff
  end
end
