package com.hashvis.controller;

import java.util.List;
import javax.swing.Timer;

import com.hashvis.applicationmanager.ApplicationWindowRouting;
import com.hashvis.view.table.OpenAddressingTableView;
import com.hashvis.view.table.SeparateChainingTableView;
import com.hashvis.view.ui.mainwindow.MainWindow;
import com.hashvis.model.collision.CollisionResolver;
import com.hashvis.model.collision.CollisionResolver.Result;
import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.util.*;
import com.hashvis.model.table.Table;

/**
 * Coordinates the main hash table visualization window and its sub-panels.
 *
 * Manages the top-level view of the application, including the hash table
 * display, the visualizer panel, and transitions between the control panel
 * and the table creation panel.
 */
public class MainWindowController {
  // View
  private MainWindow view;

  // Model
  private Table table;
  private CollisionResolver resolver;
  private DataType dataType;
  List<HashFunction> hashFunctions;

  /**
   * Constructs a new main window controller with the specified resolver and
   * key type, and sets up the initial table and sub-controllers.
   *
   * @param resolver   the collision resolution strategy to use
   * @param dataType   the type of key data
   * @param view       the main window view
   * @param appRouting the application window control interface
   */
  public MainWindowController(CollisionResolver resolver, DataType dataType, MainWindow view,
      ApplicationWindowRouting appRouting) {
    this.view = view;
    this.resolver = resolver;
    this.dataType = dataType;
    this.hashFunctions = resolver.getHashFunctionFields(dataType);

    // Initializing button functionalities
    view.addBackButtonListener(e -> appRouting.openStartWindow());
    view.addCreateButtonListener(e -> onCreateTableEvent());
    view.addRunButtonListener(e -> onRunEvent());

    // Initialize the hash function code pane
    view.setUphashFunctionFields(hashFunctions);

  }

  // Get and validate user input
  private String getInputKey() {
    try {
      String inputKey = view.getInputKey();
      if (dataType.validate(inputKey))
        return inputKey;
      else
        throw new IllegalArgumentException("Invalid input key");
    } catch (Exception e) {
      throw new IllegalArgumentException("Empty key input field");
    }
  }

  private int getTableSize() {
    try {
      return Integer.parseInt(view.getTableSize());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid table size");
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("Empty table size input field");
    }
  }

  private HashAction getAction() {
    HashAction ha = HashAction.parse(view.getTableAction());
    if (ha == null)
      throw new IllegalArgumentException("Invalid table action");
    return ha;
  }

  private void setTable(Table table) {
    this.table = table;
    if (resolver.useSeparateChaining())
      view.setHashTableView(new SeparateChainingTableView(table));
    else
      view.setHashTableView(new OpenAddressingTableView(table));
  }

  private void onCreateTableEvent() {
    try {
      // Reset the table and pseudo code view
      view.resetHashTable();
      view.resetPseudoCode();

      // Take the user data and initialize new hash table
      int tableSize = getTableSize();
      resolver.setHashFunctionFields(hashFunctions);
      for (HashFunction hf : hashFunctions)
        if (!hf.isValidHashFunction())
          view.alertError("Invalid hash function");

      // Reset the hash function code pane to default
      hashFunctions = resolver.getHashFunctionFields(dataType);
      view.setUphashFunctionFields(hashFunctions);

      // Create the hash table
      Table table = new Table(tableSize);
      setTable(table);
    } catch (Exception e) {
      view.alertError(e.getMessage());
    }
  }

  private void onRunEvent() {
    // Freeze the input and button during visualization
    view.setEnableInteraction(false);

    try {
      if (table == null)
        throw new IllegalStateException("Table must be created first");

      String inputKey = getInputKey();
      HashAction action = getAction();

      // Get the pseudo code
      List<String> pseudoCode = resolver.getAlgorithmAndInitalize(action, inputKey, table);
      view.setPseudoCode(pseudoCode);

      // Reset table to default
      table.reset();

      // Play visualize animation
      Timer timer = new Timer(1000, null);
      timer.addActionListener(e -> {
        Result r;
        try {
          r = resolver.nextStep();
        } catch (RuntimeException e1) {
          r = new Result(e1.getMessage(), -1);
        }
        view.setCurrentLine(r.currentLine());
        view.setRunStatus(r.message());
        if (r.currentLine() == -1) {
          timer.stop();
          view.setEnableInteraction(true);
        }
      });

      timer.start();
    } catch (Exception e) {
      view.alertError(e.getMessage());
      view.setEnableInteraction(true);
    }
  }

  public void setViewVisible(boolean visible) {
    view.setVisible(visible);
  }

}
