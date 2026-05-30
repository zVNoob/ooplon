package com.hashvis.model.collision;

import java.util.List;

import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.table.Item;
import com.hashvis.model.table.Row;
abstract class OpenAddressing extends ActionProcessor {
  private HashAction action;
  private Integer probeCount = 0;
  private Integer availableRow = null;
  private Integer hashValue = null;
  private HashFunction hashFunc;
  private Row currentRow = null;
  private int keyCount=0;

  abstract protected int handleBucketSelection(int probeCount);
  @Override
  public void setHashFunctionFields(List<HashFunction> hashFunctions) {
    hashFunc = hashFunctions.get(0);
  }
  @Override
  public boolean useSeparateChaining() {
    return false;
  }
  @Override
  protected void uniqueInitalize(HashAction action){
    this.action  = action;
    probeCount   = 0;
    availableRow = null;
    hashValue    = null;
    currentRow   = null;
  }
  @Override
  protected Result firstStep() {
    if(keyCount==table.size() && action == HashAction.INSERT){return new Result("Error: Table is full", -1);}
    if (hashValue == null){return handleHashing();}
    return null;
  }
  protected Result handleHashing() {
  	hashValue = hashFunc.compute(key, table.size());
  	return new Result("Hash value: " + hashValue, 0);
  }
  @Override
  protected  Result handleTraversal() {
    if (currentRow == null){
      if (probeCount == table.size()){return handleFinalization();}
      currentRow = table.getRow((hashValue + handleBucketSelection(probeCount)) % table.size());
      probeCount++;
      return new Result("Accessing bucket index " + currentRow.getIndex(), 0);
    }
    Item item = currentRow.nextItem();
    if (item == null) {return handleFinalization();}
    int ind = currentRow.getIndex();
    currentRow = null;
    if (item.isGhosted()) {
      if (availableRow == null) {
        availableRow = ind;
        return new Result("Marking bucket index " + ind + " as was used", 0);
      }
    } 
    else if (item.getName().equals(key)) {return processFoundItem(item);}
    return new Result("Checking item: " + item.getName() + " (No match)", 0);
  }

  protected Result processFoundItem(Item item) {
    switch (action){
      case (HashAction.INSERT) -> { return new Result("Error: Duplicate key " + key, -1);}
      case (HashAction.DELETE) -> {
        item.ghost();
        keyCount--;
        return new Result("Deleted key " + key, -1);
      }  
      default-> { return new Result("Found key " + key, -1);}
    }
  }

  protected Result handleFinalization() {
    if (action == HashAction.INSERT) {
      if (availableRow != null) { 
        currentRow = table.getRow(availableRow);
        currentRow.removeItem(currentRow.getItems().get(0));
      }
      if (currentRow == null || probeCount == table.size() || currentRow.getItems().size() != 0) {return new Result("Error: Can't insert that key into table", -1);}
      currentRow.addItem(key);
      keyCount++;
      return new Result("Key not found. Inserted " + key + " into bucket " + currentRow.getIndex(), -1);
    }
    return new Result("Error: Key " + key + " not found in table", -1);
  }
}