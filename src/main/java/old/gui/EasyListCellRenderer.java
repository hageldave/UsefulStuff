package old.gui;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class EasyListCellRenderer<T> implements ListCellRenderer<T> {

	private CellTextRetriever<T> cellTextRetriever;
	private DefaultListCellRenderer delegateRenderer;
	
	public EasyListCellRenderer() {
		this(null);
	}
	
	public EasyListCellRenderer(CellTextRetriever<T> cellTextRetriever) {
		if(cellTextRetriever == null)
			cellTextRetriever = new DefaultCellTextRetreiver();
		this.setCellTextRetriever(cellTextRetriever);
		this.delegateRenderer = new DefaultListCellRenderer();
	}
	
	public CellTextRetriever<T> getCellTextRetriever() {
		return cellTextRetriever;
	}

	public void setCellTextRetriever(CellTextRetriever<T> cellTextRetriever) {
		this.cellTextRetriever = cellTextRetriever;
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends T> list,
			T value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		JLabel label = (JLabel) this.delegateRenderer
				.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value != null)
			label.setText(this.cellTextRetriever.getCellText(value));
		return label;
	}

	// ---

	public static interface CellTextRetriever<T> {
		public String getCellText(T value);
	}
	
	private class DefaultCellTextRetreiver implements CellTextRetriever<T> {
		@Override
		public String getCellText(T value) {
			return value.toString();
		}
	}

}
