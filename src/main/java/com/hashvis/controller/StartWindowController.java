package com.hashvis.controller;

import java.util.Map;
import com.hashvis.model.collision.CollisionResolver;
import com.hashvis.model.util.DataType;

import com.hashvis.view.ui.startwindow.StartWindow;
import com.hashvis.applicationmanager.ApplicationWindowRouting;
import com.hashvis.model.collision.DoubleHashing;
import com.hashvis.model.collision.LinearProbing;
import com.hashvis.model.collision.QuadraticProbing;
import com.hashvis.model.collision.SeparateChaining;

public class StartWindowController {
  private StartWindow view;
  private ApplicationWindowRouting appRouting;

  public StartWindowController(StartWindow view, ApplicationWindowRouting appRouting) {
    this.view = view;
    this.appRouting = appRouting;

    view.addCreateTableButtonListener(e -> onCreateTableEvent());
  }

  private final Map<String, CollisionResolver> collisionResolvers = Map.of(
      "Separate Chaining", new SeparateChaining(),
      "Linear Probing", new LinearProbing(),
      "Quadratic Probing", new QuadraticProbing(),
      "Double Hashing", new DoubleHashing());

  private final Map<String, DataType> dataTypes = Map.of(
      "Integer", DataType.INTEGER,
      "String", DataType.STRING);

  // SUS
  public CollisionResolver getCollisionResolver() {
    return collisionResolvers.get(view.getCollisionResolver());
  }

  // SUS
  public DataType getDataType() {
    return dataTypes.get(view.getDataType());
  }

  public void onCreateTableEvent() {
    // Open the main window and pass the hash table parameters
    appRouting.openMainWindow();
  }

  public void setViewVisible(boolean visible) {
    view.setVisible(visible);
  }

}
