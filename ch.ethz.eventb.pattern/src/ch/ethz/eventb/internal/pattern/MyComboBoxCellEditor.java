package ch.ethz.eventb.internal.pattern;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class MyComboBoxCellEditor extends ComboBoxCellEditor {
	CCombo comboBox;

	public MyComboBoxCellEditor() {
		super();
	}
		
	public MyComboBoxCellEditor(Composite parent, String[] items) {
		super(parent, items);
	}
	
	public MyComboBoxCellEditor(Composite parent, String[] items, int style) {
		super(parent, items, style);
	}
	
	
	protected Control createControl(Composite parent) {
		comboBox = (CCombo)super.createControl(parent);
		return comboBox;
	}
	
	public String getText() {
		return comboBox.getText();
	}
	
	public void setText(String text) {
		comboBox.setText(text);
	}
}
