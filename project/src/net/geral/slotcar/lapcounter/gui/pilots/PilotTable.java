package net.geral.slotcar.lapcounter.gui.pilots;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.structs.Pilot;

public class PilotTable extends JTable implements Runnable {
	
	private static final long		serialVersionUID	= 1L;
	private static final int		COLUMN_1_SIZE		= 25;
	private static final int		COLUMN_2_MIN_SIZE	= 100;
	private static final int		COLUMN_2_PREF_SIZE	= 150;
	private static final int		COLUMN_3_MIN_SIZE	= 150;
	
	private final PilotTableModel	model;
	private final Kernel			kernel;
	
	private Pilot					selectedPilot;
	private int						rowEdited			= -1;
	
	public PilotTable(final Kernel k) {
		super(new PilotTableModel(k));
		kernel = k;
		model = (PilotTableModel)getModel();
		
		setDefaultRenderer(Boolean.class, new PilotTableCellRenderer.CheckboxRenderer(kernel));
		setDefaultRenderer(String.class, new PilotTableCellRenderer.TextRenderer(kernel));
		
		getTableHeader().setReorderingAllowed(false);
		
		setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
		setFillsViewportHeight(true);
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		setBackground(PilotTableCellRenderer.DEFAULT_BACKGROUND);
		
		final TableColumnModel cm = getColumnModel();
		cm.getColumn(0).setMinWidth(COLUMN_1_SIZE);
		cm.getColumn(0).setMaxWidth(COLUMN_1_SIZE);
		cm.getColumn(0).setResizable(false);
		cm.getColumn(1).setMinWidth(COLUMN_2_MIN_SIZE);
		cm.getColumn(1).setPreferredWidth(COLUMN_2_PREF_SIZE);
		cm.getColumn(2).setMinWidth(COLUMN_3_MIN_SIZE);
	}
	
	@Override
	public boolean editCellAt(final int row, final int column) {
		if (!super.editCellAt(row, column)) return false;
		setRowSelectionInterval(row, row);
		// select all
		final Component c = getEditorComponent();
		if (c instanceof JTextField) {
			c.requestFocus();
			((JTextField)c).selectAll();
		}
		
		return true;
	}
	
	public void editLastNickname() {
		editCellAt(model.getRowCount() - 1, PilotTableModel.COLUMN_NICKNAME);
	}
	
	public Pilot getSelectedPilot() {
		return selectedPilot;
	}
	
	public void refresh() {
		final Pilot oldSelection = selectedPilot;
		model.refresh();
		repaint();
		setSelectedPilot(oldSelection);
	}
	
	@Override
	public void run() {
		if (isEditing()) return;
		if (rowEdited == -1) return;
		final int row = rowEdited;
		rowEdited = -1;
		
		if (Pilot.DEFAULT_NICKNAME.equals(getValueAt(row, PilotTableModel.COLUMN_NICKNAME))) {
			editCellAt(row, PilotTableModel.COLUMN_NICKNAME);
			return;
		}
		
		if (Pilot.DEFAULT_NAME.equals(getValueAt(row, PilotTableModel.COLUMN_NAME))) {
			editCellAt(row, PilotTableModel.COLUMN_NAME);
			return;
		}
		
		clearSelection();
	}
	
	public void setSelectedPilot(final Pilot p) {
		final int i = kernel.pilots.indexOf(p);
		getSelectionModel().setSelectionInterval(i, i);
	}
	
	@Override
	public void tableChanged(final TableModelEvent e) {
		super.tableChanged(e);
		
		// only for values that changed
		if (e.getFirstRow() == -1) return;
		if (e.getColumn() == -1) return;
		
		refresh();
		
		rowEdited = e.getFirstRow();
		SwingUtilities.invokeLater(this);
	}
	
	@Override
	public void valueChanged(final ListSelectionEvent evt) {
		final int i = getSelectedRow();
		selectedPilot = (i == -1) ? null : kernel.pilots.get(i);
		super.valueChanged(evt);
	}
}
