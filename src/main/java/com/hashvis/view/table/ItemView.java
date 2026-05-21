package com.hashvis.view.table;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.*;

import com.hashvis.model.table.Item;
import com.hashvis.model.table.Item.ItemState;

public class ItemView extends JLabel implements Item.ItemListener {

  private static final int PAD = 4;
  private static final int LINE_W = 4;

  private static final Border NORMAL = border(Color.BLACK);
  private static final Border SELECTED = border(Color.RED);
  private static final Border POST_SELECTED = border(Color.ORANGE);
  private static final Border GHOSTED = border(Color.LIGHT_GRAY);

  private final Item item;

  public ItemView(Item item) {
    super(item.getName());
    this.item = item;
    setOpaque(true);
    setHorizontalAlignment(SwingConstants.CENTER);
    item.setListener(this);
    setBorder(NORMAL);
  }

  public Item getItem() {
    return item;
  }

  @Override
  public void stateChanged(ItemState state) {
    switch (state) {
      case GHOSTED -> {
        setForeground(Color.LIGHT_GRAY);
        setBorder(GHOSTED);
      }
      case NORMAL -> setBorder(NORMAL);
      case SELECTED -> setBorder(SELECTED);
      case POSTSELECTED -> setBorder(POST_SELECTED);
    }
    repaint();
  }

  private static Border border(Color color) {
    return new CompoundBorder(
        new EmptyBorder(PAD, PAD, PAD, PAD),
        new CompoundBorder(
            new LineBorder(color, LINE_W),
            new EmptyBorder(PAD, PAD, PAD, PAD)));
  }
}
