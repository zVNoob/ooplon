package com.hashvis.model.hash;

/**
 * Abstract base class for all hash functions used in the hash table visualizer.
 *
 * <p>A HashFunction maps an arbitrary key (String or Number) to a valid bucket
 * index in the range {@code [0, tableSize)}.  Concrete subclasses provide the
 * actual hashing algorithm; this class enforces the common contract and exposes
 * the table-size dependency that every modular hash function shares.
 *
 * <p>Typical usage:
 * <pre>
 *   HashFunction hf = new HashFunctionString(10);
 *   int index = hf.hash("hello");   // 0 <= index < 10
 * </pre>
 */
public abstract class HashFunction {

  /** Number of buckets in the hash table this function targets. */
  protected final int tableSize;

  /**
   * Creates a HashFunction for a table with the given number of buckets.
   *
   * @param tableSize positive number of buckets
   * @throws IllegalArgumentException if {@code tableSize} is not positive
   */
  public HashFunction(int tableSize) {
    if (tableSize <= 0)
      throw new IllegalArgumentException(
          "Table size must be positive, got: " + tableSize);
    this.tableSize = tableSize;
  }

  /**
   * Computes the bucket index for the given key.
   *
   * <p>Implementations must always return a value in {@code [0, tableSize)}.
   *
   * @param key non-null key object (String or Number)
   * @return bucket index in {@code [0, tableSize)}
   * @throws IllegalArgumentException if the key type is unsupported
   */
  public abstract int hash(Object key);

  /**
   * Returns the table size this function was built for.
   *
   * @return positive table size
   */
  public int getTableSize() {
    return tableSize;
  }

  /**
   * Returns a human-readable description of the hash algorithm.
   * Subclasses should override this to describe their specific formula.
   *
   * @return algorithm description string
   */
  public abstract String getDescription();

  /**
   * Helper: reduces any integer hash code to a valid index.
   * Handles negative codes correctly via double-modulo.
   *
   * @param rawHash arbitrary integer hash code
   * @return value in {@code [0, tableSize)}
   */
  protected final int compress(int rawHash) {
    return ((rawHash % tableSize) + tableSize) % tableSize;
  }
}