package net.geral.slotcar.lapcounter.gui.pilots;

import javax.swing.table.AbstractTableModel;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.structs.Pilot;
import net.geral.slotcar.lapcounter.structs.PilotsData;

public class PilotTableModel extends AbstractTableModel {
	private static final long		serialVersionUID	= 1L;
	
	public static final int			COLUMN_ACTIVE		= 0;
	public static final int			COLUMN_NICKNAME		= 1;
	public static final int			COLUMN_NAME			= 2;
	
	private static final String[]	columns				= {"", "Nickname", "Name"};
	
	private final PilotsData		pilots;
	
	public PilotTableModel(final Kernel k) {
		pilots = k.pilots;
	}
	
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		// active is boolean
		if (columnIndex == COLUMN_ACTIVE) { return Boolean.class; }
		// others are string
		return String.class;
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	@Override
	public String getColumnName(final int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		return pilots.count();
	}
	
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final Pilot p = pilots.get(rowIndex);
		switch (columnIndex) {
			case COLUMN_ACTIVE:
				return pilots.isActive(p);
			case COLUMN_NICKNAME:
				return p.getNickname();
			case COLUMN_NAME:
				return p.getName();
			default:
				return null;
		}
	}
	
	@Override
	public boolean isCellEditable(final int row, final int column) {
		return true;
	}
	
	public void refresh() {
		fireTableDataChanged();
	}
	
	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		final Pilot p = pilots.get(rowIndex);
		switch (columnIndex) {
			case COLUMN_ACTIVE:
				pilots.setActive(p, ((Boolean)aValue).booleanValue());
				break;
			case COLUMN_NICKNAME:
				p.setNickname(aValue.toString());
				break;
			case COLUMN_NAME:
				p.setName(aValue.toString());
				break;
			default:
				return;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
