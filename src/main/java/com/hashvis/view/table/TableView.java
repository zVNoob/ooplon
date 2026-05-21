package com.hashvis.view.table;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

import com.hashvis.model.table.Table;

public abstract class TableView extends JScrollPane {
  protected final JPanel content = new JPanel();

  public TableView(Table table) {
    setViewportView(content);
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    getViewport().addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        Dimension pref = content.getLayout().preferredLayoutSize(content);
        content.setPreferredSize(new Dimension(getViewport().getWidth(), pref.height));
        content.revalidate();
      }
    });
  }
}
