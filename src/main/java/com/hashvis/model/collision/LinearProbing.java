package com.hashvis.model.collision;

public class LinearProbing extends OpenAddressing {
  @Override
  protected int handleBucketSelection(int probeCount) {
    return probeCount;
  }
  @Override
  protected String getcurrent_ResolverType(){
    return " i = (base + step) % size of HT";
  }
}