def apply_script(a, script)
  res = a.map { |c| [c] }
  script.each do |op|
    case op[:op]
    when :delete
      res[op[:index]].pop
    when :insert
      res << [] while res.length < op[:index] + 1
      res[op[:index]] = op[:items] + res[op[:index]]
    else
      raise "Unknown op: #{op[:op]}"
    end
  end
  res.flatten
end

def script_length(script)
  script.map { |op| op.key?(:items) ? op[:items].length : 1 }.inject(:+)
end
