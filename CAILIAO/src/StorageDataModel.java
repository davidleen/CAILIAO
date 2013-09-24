import java.util.List;

import javax.swing.table.AbstractTableModel;

public class StorageDataModel extends AbstractTableModel {

	private List<Data_Storage> datas;

	private String[] tableHead = new String[] { "来往单位/部门", "编码", "名称",
			"   全称        ", "数量", "单价", "单位", "进出库时间", "出入库单据号", "类型", "合同",
			"客号", "货号", "备注" };

	public static int[] tableHeadWidth = new int[] { 100, 60, 80, 120, 40, 40,
			40, 60, 80, 60, 60, 60, 60, 80 };
	private static final long serialVersionUID = 1L;

	public StorageDataModel(List<Data_Storage> datas) {
		 
		this.datas = datas;
	}

	@Override
	public int getRowCount() {
		 
		return datas.size();
	}

	@Override
	public int getColumnCount() {
	 
		return tableHead.length;

	}

	@Override
	public String getColumnName(int index) {
		return tableHead[index];

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		 
		Data_Storage data = datas.get(rowIndex);
		if (columnIndex == tableHead.length - 1) {
			return data.memo;

		}

		switch (columnIndex) {
		case 0:
			return data.dept;
		case 1:
			return data.code;
		case 2:
			return data.name;
		case 3:
			return data.fullName;
		case 4:
			return Math.abs(data.qty);
		case 5:
			return data.price;
		case 6:
			return data.unit;
		case 7:
			return data.date;
		case 8:
			return data.ticketNumber;
		case 9:
			return data.type;
		case 10:
			return data.orderName;
		case 11:
			return data.cName;
		case 12:
			return data.productName;
		case 13:
			return data.memo;
		}

		return null;

	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == tableHead.length - 1) {
			return true;
		}
		return false;

	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		System.out.println("setValueAt  rowIndex: " + rowIndex
				+ ",columnIndex:" + columnIndex + ",value:" + value);
		if (columnIndex == tableHead.length - 1) {

			datas.get(rowIndex).memo = value == null ? "" : value.toString();
			datas.get(rowIndex).changed = true;
		}

	}
}
