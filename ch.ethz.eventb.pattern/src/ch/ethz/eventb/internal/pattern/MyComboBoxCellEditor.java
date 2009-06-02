/*******************************************************************************
 * Copyright (c) 2009 ETH Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package ch.ethz.eventb.internal.pattern;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class is an extension of the ComboBoxCellEditor. It enables getting and
 * setting the user input of the combo box.
 * 
 * @author fuersta
 *
 */
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
	
	@Override
	protected Control createControl(Composite parent) {
		comboBox = (CCombo)super.createControl(parent);
		return comboBox;
	}
	
	/**
	 * This method returns the user input of the combo box.
	 * @return the user input string of the combo box 
	 */
	public String getText() {
		return comboBox.getText();
	}
	
	/**
	 * This method sets the user input of the combo box.
	 * @param text is set as user input
	 */
	public void setText(String text) {
		comboBox.setText(text);
	}
}
