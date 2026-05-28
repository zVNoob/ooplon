package com.hashvis.model.hash;

/**
 * Hash function for string keys.
 *
 * <p>Uses the <em>polynomial rolling hash</em> (also known as the Horner
 * evaluation scheme), a widely-used algorithm that treats the string as a
 * polynomial in a prime base {@value #BASE}:
 *
 * <pre>
 *   h = (s[0]*BASE^(n-1) + s[1]*BASE^(n-2) + ... + s[n-1]) mod tableSize
 * </pre>
 *
 * This is computed iteratively via Horner's rule to avoid exponentiation:
 * <pre>
 *   h = 0
 *   for each character c : h = h * BASE + c
 *   index = compress(h)
 * </pre>
 *
 * <p>The base {@value #BASE} (31) is a small odd prime that gives good
 * distribution for typical ASCII / alphanumeric strings. Integer overflow is
 * intentional and harmless; the final {@link HashFunction#compress} call folds
 * the result into the valid bucket range.
 *
 * <p>The hash is <em>case-sensitive</em>: {@code "Hello"} and {@code "hello"}
 * map to different buckets. Keys are required to be non-null and non-empty,
 * matching the contract enforced by {@link com.hashvis.model.table.Item}.
 *
 * <p>Example (tableSize = 10):
 * <pre>
 *   hf.hash("a")   // 'a'=97 → compress(97)  = 7
 *   hf.hash("ab")  // 97*31 + 98 = 3105 → compress(3105) = 5
 * </pre>
 */
public class HashFunctionString extends HashFunction {

  /**
   * Prime base for the polynomial rolling hash.
   * 31 is the standard choice for short ASCII strings (used in Java's own
   * {@link String#hashCode}).
   */
  private static final int BASE = 31;

  /**
   * Creates a string hash function for a table of the given size.
   *
   * @param tableSize positive number of buckets
   */
  public HashFunctionString(int tableSize) {
    super(tableSize);
  }

  /**
   * Hashes a string key using polynomial rolling hash.
   *
   * @param key a non-null, non-empty {@link String}
   * @return bucket index in {@code [0, tableSize)}
   * @throws IllegalArgumentException if {@code key} is not a non-empty String
   */
  @Override
  public int hash(Object key) {
    if (!(key instanceof String))
      throw new IllegalArgumentException(
          "HashFunctionString requires a String key, got: "
              + (key == null ? "null" : key.getClass().getSimpleName()));
    String s = (String) key;
    if (s.isEmpty())
      throw new IllegalArgumentException(
          "HashFunctionString requires a non-empty String key");

    int h = 0;
    for (int i = 0; i < s.length(); i++) {
      // Horner step: accumulate character value into running hash.
      // Multiplication intentionally overflows Java int arithmetic.
      h = h * BASE + s.charAt(i);
    }
    return compress(h);
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return "Polynomial rolling hash (base " + BASE + "): "
        + "h = (h * " + BASE + " + c) for each char; index = h mod " + tableSize;
  }
}