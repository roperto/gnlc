package net.geral.slotcar.lapcounter.gui.pilots;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.gui.Util;

public abstract class PilotTableCellRenderer {
	public static class CheckboxRenderer extends JPanel implements TableCellRenderer {
		private static final long	serialVersionUID	= 1L;
		
		private final JCheckBox		checkbox			= new JCheckBox();
		private final Kernel		kernel;
		
		public CheckboxRenderer(final Kernel k) {
			super(new GridLayout(1, 0));
			kernel = k;
			setOpaque(true);
			setName("Table.cellRenderer");
			
			checkbox.setHorizontalAlignment(SwingConstants.CENTER);
			add(checkbox);
		}
		
		public void addActionListener(final ActionListener l) {
			checkbox.addActionListener(l);
		}
		
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			setCommonParameters(kernel, this, table, isSelected, row, column);
			
			checkbox.setSelected(((value != null) && ((Boolean)value).booleanValue()));
			
			return this;
		}
	}
	public static class TextRenderer extends JLabel implements TableCellRenderer {
		private static final long	serialVersionUID	= 1L;
		private final Kernel		kernel;
		
		public TextRenderer(final Kernel k) {
			super();
			kernel = k;
			setOpaque(true);
			setName("Table.cellRenderer");
		}
		
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			setCommonParameters(kernel, this, table, isSelected, row, column);
			
			setFont(table.getFont());
			
			setText((value == null) ? "" : value.toString());
			
			return this;
		}
	}
	
	public static Color		DEFAULT_BACKGROUND			= new Color(240, 240, 240);
	public static Color		DEFAULT_FOREGROUND			= Color.GRAY;
	public static Color		SELECTED_BACKGROUND			= DEFAULT_FOREGROUND;
	public static Color		SELECTED_FOREGROUND			= DEFAULT_BACKGROUND;
	public static Color		ACTIVE_IN_FOREGROUND		= Color.BLACK;
	public static Color		ACTIVE_OUT_FOREGROUND		= ACTIVE_IN_FOREGROUND;
	public static Color		ACTIVE_SELECTED_FOREGROUND	= Color.WHITE;
	public static Border	DEFAULT_BORDER				= BorderFactory.createEmptyBorder(1, 1, 1, 1);
	public static Color		SELECTED_BORDER_COLOR		= ACTIVE_IN_FOREGROUND;
	public static Border[]	SELECTED_BORDER				= {
														BorderFactory.createMatteBorder(1, 1, 1, 0, SELECTED_BORDER_COLOR),
														BorderFactory.createMatteBorder(1, 0, 1, 0, SELECTED_BORDER_COLOR),
														BorderFactory.createMatteBorder(1, 0, 1, 1, SELECTED_BORDER_COLOR),
														};
	
	public static void setCommonParameters(final Kernel kernel, final Component c, final JTable table, final boolean isSelected, final int row, final int column) {
		final JComponent jc = (c instanceof JComponent) ? (JComponent)c : null;
		
		final int laneIndex = kernel.pilots.laneIndexOf(kernel.pilots.get(row));
		if ((laneIndex < 0) || (laneIndex >= kernel.config.LanesQuantity)) {
			if (isSelected) {
				c.setForeground(SELECTED_FOREGROUND);
				c.setBackground(SELECTED_BACKGROUND);
			}
			else {
				c.setForeground((laneIndex == -1) ? DEFAULT_FOREGROUND : ACTIVE_OUT_FOREGROUND);
				c.setBackground(DEFAULT_BACKGROUND);
			}
		}
		else {
			if (isSelected) {
				c.setForeground(ACTIVE_SELECTED_FOREGROUND);
				c.setBackground(Util.makeDarker(kernel.config.LaneColor[laneIndex], 0.6));
			}
			else {
				c.setForeground(ACTIVE_IN_FOREGROUND);
				c.setBackground(kernel.config.LaneColor[laneIndex]);
			}
		}
		
		if (jc != null) jc.setBorder(isSelected ? SELECTED_BORDER[column] : DEFAULT_BORDER);
	}
}
