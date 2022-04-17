module Algorithms
  module Diff
    # Basic variation of Myers diff algorithm.
    # O(D * (N + M)) time, O(NM) space complexity.
    # TODO: squash "insert operations",
    # otherwise application of the script depends on the order of the operations.
    #
    # http://www.xmailserver.org/diff2.pdf
    def myers_diff(a, b)
      return [] if a == b

      from = {}
      best_on_diag = { 0 => max_snake(a, b, 0, 0) }

      d = 1
      found = false
      until found
        (-d..d).step(2).each do |diag|
          p1_i, p1_j = best_on_diag.fetch(diag + 1, [0, 0])
          m1_i, m1_j = best_on_diag.fetch(diag - 1, [0, 0])

          if p1_i + p1_j >= m1_i + m1_j && p1_i < b.length
            fi = p1_i
            fj = p1_j
            i = p1_i + 1
            j = p1_j
          elsif p1_i + p1_j <= m1_i + m1_j && m1_j < a.length
            fi = m1_i
            fj = m1_j
            i = m1_i
            j = m1_j + 1
          else
            next
          end

          i, j = max_snake(a, b, i, j)
          best_on_diag[diag] = [i, j]
          from[[i, j]] = [fi, fj]

          found = true if i == b.length && j == a.length
        end

        d += 1
      end

      script = []
      i = b.length
      j = a.length
      while from.key?([i, j])
        fi, fj = from[[i, j]]
        script << if fi - fj < i - j
                    { op: :insert, index: fj, items: [b[fi]] }
                  else
                    { op: :delete, index: fj }
                  end
        i = fi
        j = fj
      end

      script
    end
    module_function :myers_diff

    private

    def max_snake(a, b, i, j)
      while j < a.length && i < b.length && a[j] == b[i]
        j += 1
        i += 1
      end
      [i, j]
    end
    module_function :max_snake
  end
end
