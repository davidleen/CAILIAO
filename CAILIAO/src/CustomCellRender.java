import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomCellRender extends DefaultTableCellRenderer {
	private List<Data_Storage> datas;

	public CustomCellRender(List<Data_Storage> datas) {
		setOpaque(true);
		this.datas = datas;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component component = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		int modelRow = table.convertRowIndexToModel(row);
		if (datas != null && modelRow < datas.size()) {
			Data_Storage data = datas.get(modelRow);

			if (data.changed)

				component.setBackground(Color.pink);

			else
				component.setBackground(Color.white);

		}

		return component;
	}
}