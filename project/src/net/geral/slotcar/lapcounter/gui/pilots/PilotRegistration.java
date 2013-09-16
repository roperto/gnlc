package net.geral.slotcar.lapcounter.gui.pilots;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Storage;
import net.geral.slotcar.lapcounter.structs.Pilot;

public class PilotRegistration extends JPanel implements ActionListener, ListSelectionListener {
	private static final long		serialVersionUID	= 1L;
	private static final Dimension	SIZE				= new Dimension(500, 300);
	
	private final JButton			btnAdd				= new JButton("Add");
	private final JButton			btnErase			= new JButton("Erase");
	private final JButton			btnClear			= new JButton("Clear");
	private final JButton			btnDefault			= new JButton("Default");
	private final JButton			btnSave				= new JButton("Save");
	private final JButton			btnLoad				= new JButton("Load");
	private final JButton			btnCancel			= new JButton("Cancel");
	private final JButton			btnOk				= new JButton("Ok");
	private final JButton			btnUp				= new JButton("");
	private final JButton			btnDown				= new JButton("");
	private final JButton			btnSort				= new JButton("Sort");
	private final JButton			btnShuffle			= new JButton("Shuffle");
	private final JButton			btnRotate			= new JButton("Rotate");
	private final JButton			btnSelectAll		= new JButton("Select All");
	private final JButton			btnSelectNone		= new JButton("Select None");
	
	private final PilotTable		table;
	private final Kernel			kernel;
	
	public PilotRegistration(final Kernel k) {
		setBackground(Color.BLACK);
		kernel = k;
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		final JPanel panelRoot = new JPanel();
		panelRoot.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelRoot.setMinimumSize(SIZE);
		panelRoot.setMaximumSize(SIZE);
		panelRoot.setSize(SIZE);
		final GridBagConstraints gbc_panelRoot = new GridBagConstraints();
		gbc_panelRoot.gridx = 1;
		gbc_panelRoot.gridy = 1;
		add(panelRoot, gbc_panelRoot);
		panelRoot.setLayout(new BorderLayout(0, 0));
		
		final JPanel panelBorder = new JPanel();
		panelBorder.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelRoot.add(panelBorder, BorderLayout.CENTER);
		panelBorder.setLayout(new BorderLayout(0, 0));
		
		final JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(5, 0, 5, 0));
		panelBorder.add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new GridLayout(2, 9, 0, 0));
		
		panelBottom.add(btnAdd);
		
		panelBottom.add(btnErase);
		
		panelBottom.add(btnClear);
		
		panelBottom.add(btnDefault);
		
		panelBottom.add(btnSave);
		
		panelBottom.add(btnLoad);
		
		panelBottom.add(btnCancel);
		
		panelBottom.add(btnOk);
		
		final JPanel panelCenter = new JPanel();
		panelBorder.add(panelCenter);
		panelCenter.setLayout(new BorderLayout(0, 0));
		
		final JLabel lblSelectPilots = new JLabel("Select Pilots:");
		panelCenter.add(lblSelectPilots, BorderLayout.NORTH);
		
		final JScrollPane scrollPane = new JScrollPane();
		panelCenter.add(scrollPane, BorderLayout.CENTER);
		
		table = new PilotTable(kernel);
		scrollPane.setViewportView(table);
		
		final JPanel panelRight = new JPanel();
		panelRight.setBorder(new EmptyBorder(0, 5, 0, 0));
		panelCenter.add(panelRight, BorderLayout.EAST);
		final GridBagLayout gbl_panelRight = new GridBagLayout();
		gbl_panelRight.columnWidths = new int[] {20, 20, 0};
		gbl_panelRight.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelRight.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		gbl_panelRight.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panelRight.setLayout(gbl_panelRight);
		
		btnUp.setIcon(new ImageIcon(PilotRegistration.class.getResource("/images/icons/Crystal_Clear_action_1uparrow.png")));
		final GridBagConstraints gbc_btnUp = new GridBagConstraints();
		gbc_btnUp.fill = GridBagConstraints.BOTH;
		gbc_btnUp.insets = new Insets(0, 0, 5, 5);
		gbc_btnUp.gridx = 0;
		gbc_btnUp.gridy = 0;
		panelRight.add(btnUp, gbc_btnUp);
		
		btnDown.setIcon(new ImageIcon(PilotRegistration.class.getResource("/images/icons/Crystal_Clear_action_1downarrow.png")));
		final GridBagConstraints gbc_btnDown = new GridBagConstraints();
		gbc_btnDown.fill = GridBagConstraints.VERTICAL;
		gbc_btnDown.insets = new Insets(0, 0, 5, 0);
		gbc_btnDown.gridx = 1;
		gbc_btnDown.gridy = 0;
		panelRight.add(btnDown, gbc_btnDown);
		
		final GridBagConstraints gbc_btnSort = new GridBagConstraints();
		gbc_btnSort.gridwidth = 2;
		gbc_btnSort.fill = GridBagConstraints.BOTH;
		gbc_btnSort.insets = new Insets(0, 0, 5, 0);
		gbc_btnSort.gridx = 0;
		gbc_btnSort.gridy = 1;
		panelRight.add(btnSort, gbc_btnSort);
		
		final GridBagConstraints gbc_btnShuffle = new GridBagConstraints();
		gbc_btnShuffle.gridwidth = 2;
		gbc_btnShuffle.fill = GridBagConstraints.BOTH;
		gbc_btnShuffle.insets = new Insets(0, 0, 5, 0);
		gbc_btnShuffle.gridx = 0;
		gbc_btnShuffle.gridy = 2;
		panelRight.add(btnShuffle, gbc_btnShuffle);
		
		final GridBagConstraints gbc_btnRotate = new GridBagConstraints();
		gbc_btnRotate.gridwidth = 2;
		gbc_btnRotate.fill = GridBagConstraints.BOTH;
		gbc_btnRotate.insets = new Insets(0, 0, 5, 0);
		gbc_btnRotate.gridx = 0;
		gbc_btnRotate.gridy = 3;
		panelRight.add(btnRotate, gbc_btnRotate);
		
		final GridBagConstraints gbc_btnSelectAll = new GridBagConstraints();
		gbc_btnSelectAll.gridwidth = 2;
		gbc_btnSelectAll.fill = GridBagConstraints.BOTH;
		gbc_btnSelectAll.insets = new Insets(0, 0, 5, 0);
		gbc_btnSelectAll.gridx = 0;
		gbc_btnSelectAll.gridy = 5;
		panelRight.add(btnSelectAll, gbc_btnSelectAll);
		
		final GridBagConstraints gbc_btnSelectNone = new GridBagConstraints();
		gbc_btnSelectNone.fill = GridBagConstraints.BOTH;
		gbc_btnSelectNone.gridwidth = 2;
		gbc_btnSelectNone.gridx = 0;
		gbc_btnSelectNone.gridy = 6;
		panelRight.add(btnSelectNone, gbc_btnSelectNone);
		
		btnAdd.addActionListener(this);
		btnCancel.addActionListener(this);
		btnClear.addActionListener(this);
		btnDefault.addActionListener(this);
		btnDown.addActionListener(this);
		btnErase.addActionListener(this);
		btnLoad.addActionListener(this);
		btnOk.addActionListener(this);
		btnRotate.addActionListener(this);
		btnSave.addActionListener(this);
		btnShuffle.addActionListener(this);
		btnSort.addActionListener(this);
		btnUp.addActionListener(this);
		btnSelectAll.addActionListener(this);
		btnSelectNone.addActionListener(this);
		
		table.getSelectionModel().addListSelectionListener(this);
		
		updateButtons();
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		// execute
		execute(e.getSource());
		// update table contents
		table.refresh();
		updateButtons();
	}
	
	private boolean add() {
		kernel.pilots.add(new Pilot());
		table.refresh();
		table.editLastNickname();
		return false;
	}
	
	private boolean clear() {
		kernel.pilots.clear();
		table.refresh();
		return false;
	}
	
	private boolean close(final boolean reload) {
		if (reload) {
			kernel.pilots.load(); // reload last save (do not use changes)
		}
		else {
			kernel.pilots.save(); // save changes (use changes)
		}
		kernel.window.showLanes();
		return false;
	}
	
	private boolean defaults() {
		kernel.pilots.loadDefault();
		return false;
	}
	
	private boolean down() {
		kernel.pilots.moveDown(table.getSelectedRow());
		return false;
	}
	
	private boolean erase() {
		final int selected = table.getSelectedRow();
		if (selected == -1) return false;
		
		kernel.pilots.erase(selected);
		
		return false;
	}
	
	private boolean execute(final Object source) {
		if (source == btnCancel) return close(true);
		if (source == btnOk) return close(false);
		if (source == btnErase) return erase();
		if (source == btnAdd) return add();
		if (source == btnClear) return clear();
		if (source == btnDefault) return defaults();
		if (source == btnLoad) return load();
		if (source == btnSave) return save();
		if (source == btnUp) return up();
		if (source == btnDown) return down();
		if (source == btnSort) return sort();
		if (source == btnShuffle) return shuffle();
		if (source == btnRotate) return rotate();
		if (source == btnSelectAll) return selectAll();
		if (source == btnSelectNone) return selectNone();
		
		return false;
	}
	
	private boolean load() {
		final File f = Storage.choosefile(this, "Pilots Data", kernel.pilots, true);
		if (f != null) {
			kernel.pilots.load(f);
			table.refresh();
		}
		return false;
	}
	
	public void open() {
		kernel.window.show(this);
	}
	
	private boolean rotate() {
		kernel.pilots.rotate();
		table.refresh();
		return false;
	}
	
	private boolean save() {
		final File f = Storage.choosefile(this, "Pilots Data", kernel.pilots, false);
		if (f != null) {
			kernel.pilots.save(f);
			table.refresh();
		}
		return false;
	}
	
	private boolean selectAll() {
		kernel.pilots.activateAll();
		table.refresh();
		return false;
	}
	
	private boolean selectNone() {
		kernel.pilots.deactivateAll();
		table.refresh();
		return false;
	}
	
	private boolean shuffle() {
		kernel.pilots.shuffle();
		table.refresh();
		return false;
	}
	
	private boolean sort() {
		kernel.pilots.sort();
		table.refresh();
		return false;
	}
	
	private boolean up() {
		kernel.pilots.moveUp(table.getSelectedRow());
		return false;
	}
	
	private void updateButtons() {
		final int selected = table.getSelectedRow();
		final int pilots = kernel.pilots.count();
		final int actives = kernel.pilots.countActive();
		
		btnErase.setEnabled(selected != -1);
		btnUp.setEnabled(selected > 0);
		btnDown.setEnabled((selected >= 0) && ((selected + 1) < kernel.pilots.count()));
		btnSort.setEnabled(pilots > 1);
		btnShuffle.setEnabled(pilots > 1);
		btnRotate.setEnabled(actives > 1);
		btnOk.setEnabled(actives > 0);
		btnSelectAll.setEnabled((pilots > 0) && (pilots != actives));
		btnSelectNone.setEnabled((pilots > 0) && (actives > 0));
	}
	
	@Override
	public void valueChanged(final ListSelectionEvent e) {
		updateButtons();
	}
}
