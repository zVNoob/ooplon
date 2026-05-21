package com.hashvis.view.table;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.hashvis.model.table.Item;
import com.hashvis.model.table.Row;
import com.hashvis.model.table.Row.RowState;

public class RowView extends JPanel implements Row.RowListener {

  private static final int PAD = 4;
  private static final int LINE_W = 4;
  private static final int INDEX_W = 30;
  private static final int INDEX_H = 35;

  private static final Border NORMAL = border(Color.BLACK);
  private static final Border SELECTED = border(Color.BLUE);
  private static final Border POST_SELECTED = border(Color.GREEN);

  private final JPanel contentPanel = new JPanel();

  public RowView(Row row) {
    row.setListener(this);

    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setOpaque(true);

    JLabel indexLabel = new JLabel(Integer.toString(row.getIndex()));
    indexLabel.setPreferredSize(new Dimension(INDEX_W, INDEX_H));
    indexLabel.setMaximumSize(new Dimension(INDEX_W, INDEX_H));
    indexLabel.setHorizontalAlignment(SwingConstants.LEFT);
    add(indexLabel);
    add(new JLabel(":"));
    add(Box.createHorizontalStrut(5));

    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
    add(contentPanel);

    setBorder(NORMAL);
  }

  @Override
  public void stateChanged(RowState state) {
    switch (state) {
      case NORMAL -> setBorder(NORMAL);
      case SELECTED -> setBorder(SELECTED);
      case POSTSELECTED -> setBorder(POST_SELECTED);
    }
    repaint();
  }

  @Override
  public void itemAdded(Item item) {
    contentPanel.add(new ItemView(item));
    revalidate();
    repaint();
  }

  @Override
  public void itemRemoved(Item item) {
    for (int i = 0; i < contentPanel.getComponentCount(); i++) {
      ItemView iv = (ItemView) contentPanel.getComponent(i);
      if (iv.getItem() == item) {
        contentPanel.remove(i);
        revalidate();
        repaint();
        return;
      }
    }
  }

  private static Border border(Color color) {
    return new CompoundBorder(
        new EmptyBorder(PAD, PAD, PAD, PAD),
        new CompoundBorder(
            new LineBorder(color, LINE_W),
            new EmptyBorder(PAD, PAD, PAD, PAD)));
  }
}
