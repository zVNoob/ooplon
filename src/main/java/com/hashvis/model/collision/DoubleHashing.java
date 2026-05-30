package com.hashvis.model.collision;

import java.util.List;

import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.hashfunc.HashFunctionNumber;
import com.hashvis.model.hashfunc.HashFunctionString;

public class DoubleHashing extends OpenAddressing {
  private Integer hashValue1 = null;
  private Integer hashValue2 = null;
  private HashFunction hashFunc1;
  private HashFunction hashFunc2;
  @Override 
  protected String initalizePseudocode(){
    return "step = 0 ; i = base =hash1(k,n) ; jumpdistance = hash2(k,n)";
  }
  @Override
  protected String getcurrent_ResolverType(){
    return " i = (base + step * jumpdistance) % size of HT";
  }
  @Override
  public List<HashFunction> getHashFunctionFields(DataType dataType) {
    HashFunction hf1;
    HashFunction hf2;
    switch (dataType) {
      case INTEGER -> {
        hf1 = new HashFunctionNumber();
        hf2 = new HashFunctionNumber();
      }
      case STRING -> {
        hf1 = new HashFunctionString();
        hf2 = new HashFunctionString();
      }
      default -> throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }
    return List.of(hf1, hf2);
  }

  @Override
  public void setHashFunctionFields(List<HashFunction> hashFunctions) {
    if (hashFunctions.size() != 2)
      throw new IllegalArgumentException("Expected 2 hash functions, but got " + hashFunctions.size());
    hashFunc1 = hashFunctions.get(0);
    hashFunc2 = hashFunctions.get(1);
  }

  @Override
  protected void uniqueInitalize(HashAction action) {
    super.uniqueInitalize(action);
    hashValue1 = null;
    hashValue2 = null;
  }

  @Override
  protected int handleBucketSelection(int probeCount) {
    return probeCount*hashValue2;
  }

  @Override
  public Result firstStep() {
    if (hashValue1 == null) {
      hashValue1 = hashFunc1.compute(key, table.size());
      return new Result("Hash value: " + hashValue1, 0);
    }
    if (hashValue2 == null) {
      hashValue2 = hashFunc2.compute(key, table.size());
      if (hashValue2 == 0) {
        hashValue2 = 1;
      }
      return new Result("Hash value: " + hashValue2, 0);
    }
    return null;
  }
}
