package com.hashvis.model.hash;

/**
 * Hash function for numeric (integer) keys.
 *
 * <p>Applies the classic <em>division method</em>:
 * <pre>
 *   index = |key| mod tableSize
 * </pre>
 * The absolute value guards against negative keys; the helper
 * {@link HashFunction#compress} handles the edge case of
 * {@code Integer.MIN_VALUE} safely.
 *
 * <p>For non-integer {@link Number} subtypes (Long, Double, etc.) the value is
 * converted to {@code int} before hashing, matching the behaviour expected by
 * the DSL interpreter which always parses numeric tokens as {@code int}.
 *
 * <p>Example:
 * <pre>
 *   HashFunction hf = new HashFunctionNumber(7);
 *   hf.hash(20);   // 20 % 7 = 6
 *   hf.hash(-3);   // ((-3 % 7) + 7) % 7 = 4
 * </pre>
 */
public class HashFunctionNumber extends HashFunction {

  /**
   * Creates a numeric hash function for a table of the given size.
   *
   * @param tableSize positive number of buckets
   */
  public HashFunctionNumber(int tableSize) {
    super(tableSize);
  }

  /**
   * Hashes a numeric key using the division method.
   *
   * @param key a {@link Number} (typically {@link Integer})
   * @return bucket index in {@code [0, tableSize)}
   * @throws IllegalArgumentException if {@code key} is not a {@link Number}
   */
  @Override
  public int hash(Object key) {
    if (!(key instanceof Number))
      throw new IllegalArgumentException(
          "HashFunctionNumber requires a Number key, got: "
              + (key == null ? "null" : key.getClass().getSimpleName()));
    int k = ((Number) key).intValue();
    return compress(k);
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return "Division method: index = |key| mod " + tableSize;
  }
}