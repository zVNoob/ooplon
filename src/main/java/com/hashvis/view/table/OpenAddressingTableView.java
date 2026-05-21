package com.hashvis.view.table;

import java.awt.*;

import com.hashvis.model.table.Row;
import com.hashvis.model.table.Table;

public class OpenAddressingTableView extends TableView {

  // Wraps RowView components like text: when a RowView exceeds the container
  // width it drops to the next line. FlowLayout normally does not wrap when
  // the container lacks a hard width limit.
  private static class WrapLayout extends FlowLayout {
    private static final int HGAP = 10;
    private static final int VGAP = 10;

    WrapLayout() {
      super(FlowLayout.LEFT, HGAP, VGAP);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
      return layoutSize(target, false);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
      return layoutSize(target, true);
    }

    @Override
    public void layoutContainer(Container target) {
      // Synchronize on the container's AWT tree lock, required when
      // reading/writing component bounds during layout.
      synchronized (target.getTreeLock()) {
        int targetWidth = target.getWidth();
        if (targetWidth <= 0)
          targetWidth = 400; // fallback before first real resize

        int x = HGAP;
        int y = VGAP;
        int rowHeight = 0;

        for (Component comp : target.getComponents()) {
          if (!comp.isVisible())
            continue;
          Dimension d = comp.getPreferredSize();
          // Wrap if this component would overflow, unless we are
          // still at the start of the row (single component wider
          // than the container is allowed).
          if (x + d.width > targetWidth && x > HGAP) {
            x = HGAP;
            y += rowHeight + VGAP;
            rowHeight = 0;
          }
          comp.setBounds(x, y, d.width, d.height);
          x += d.width + HGAP;
          rowHeight = Math.max(rowHeight, d.height);
        }
      }
    }

    // Calculates the total size needed using the same wrapping logic as
    // layoutContainer, without actually moving any components.
    private Dimension layoutSize(Container target, boolean minimum) {
      synchronized (target.getTreeLock()) {
        int targetWidth = target.getWidth();
        if (targetWidth <= 0)
          targetWidth = 400;

        int x = 0;
        int y = 0;
        int rowHeight = 0;

        for (Component comp : target.getComponents()) {
          if (!comp.isVisible())
            continue;
          Dimension d = minimum ? comp.getMinimumSize() : comp.getPreferredSize();
          if (x + d.width > targetWidth && x > 0) {
            x = 0;
            y += rowHeight + VGAP;
            rowHeight = 0;
          }
          x += d.width + HGAP;
          rowHeight = Math.max(rowHeight, d.height);
        }
        return new Dimension(targetWidth, y + rowHeight + VGAP);
      }
    }
  }

  public OpenAddressingTableView(Table table) {
    super(table);
    content.setLayout(new WrapLayout());
    for (Row row : table.getRows())
      content.add(new RowView(row));
  }
}
