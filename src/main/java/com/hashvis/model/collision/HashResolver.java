package com.hashvis.model.collision;

import java.util.ArrayList;
import java.util.List;
import com.hashvis.model.hashfunc.*;
import com.hashvis.model.table.*;

public abstract class HashResolver implements CollisionResolver {

  private HashFunction initializeHashFunction(DataType type) {
    switch (type) {
      case INTEGER:
        return new HashFunctionNumber();
      case STRING:
        return new HashFunctionString();
      default:
        throw new IllegalArgumentException("Unsupported data type: " + type);
    }
  }

  @Override
  public List<HashFunction> getHashFunctionFields(DataType dataType) {
    ArrayList<HashFunction> result = new ArrayList<HashFunction>();
    result.add(initializeHashFunction(dataType));
    return result;
  }
}
