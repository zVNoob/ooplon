package com.hashvis.view.table;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;

import com.hashvis.model.table.Row;
import com.hashvis.model.table.Table;

public class SeparateChainingTableView extends TableView {

  private static final int VGAP = 10;

  public SeparateChainingTableView(Table table) {
    super(table);
    content.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(0, 0, VGAP, 10);

    for (int i = 0; i < table.size(); i++) {
      Row row = table.getRows().get(i);

      c.gridx = 0;
      c.gridy = i;
      c.weightx = 0;
      content.add(new RowView(row), c);

      c.gridx = 1;
      c.weightx = 1;
      content.add(Box.createHorizontalGlue(), c);
    }
  }
}
