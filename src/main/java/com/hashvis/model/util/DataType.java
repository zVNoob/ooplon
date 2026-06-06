package com.hashvis.model.util;

public enum DataType {
  STRING {
    @Override
    public boolean validate(String value) {
      return value != null && !value.isEmpty();
    }
  },
  INTEGER {
    @Override
    public boolean validate(String value) {
      try {
        Integer.parseInt(value);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  };

  public abstract boolean validate(String value);

}
