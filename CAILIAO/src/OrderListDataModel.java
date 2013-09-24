import java.util.List;

import javax.swing.table.AbstractTableModel;

public class OrderListDataModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Data_OrderList> datas;
	private String[] column = new String[] {
			"合同号                                        ",
			"    客号            ", "   货号            ", "  客户               ",
			"业务员", "   下单日期             ", "  交货日期         ", "   数量   " };

	public OrderListDataModel(List<Data_OrderList> datas) {
		// TODO Auto-generated constructor stub
		this.datas = datas;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		if (datas == null)
			return 0;
		return datas.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return column.length;
	}

	@Override
	public String getColumnName(int index) {
		// TODO Auto-generated method stub
		return column[index];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		Data_OrderList data = datas.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return data.orderName;
		case 1:
			return data.cName;
		case 2:
			return data.productName;
		case 3:
			return data.customerName;

		case 4:
			return data.salesMan;
		case 5:
			return data.orderDay;
		case 6:
			return data.promptDay;
		case 7:
			return data.qty;

		}

		return null;

	}

}
