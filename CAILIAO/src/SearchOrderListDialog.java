import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SearchOrderListDialog extends JDialog {

	/**
	 * 
	 */
	private JTextField jtf;
	private JButton search;
	private JTable jtable;
	private List<Data_OrderList> datas;
	private static final long serialVersionUID = 1L;
	private Data_OrderList result;

	private LoadingDialog dialog;

	public SearchOrderListDialog(JFrame owner, String title) {
		super(owner, title);
		this.setModal(true);

		JPanel jp = new JPanel(new BorderLayout());
		JLabel jlable = new JLabel("合同/客号/货号:");
		jp.add(jlable, BorderLayout.WEST);
		jtf = new JTextField();

		jtf.setToolTipText("请输入合同/货号/客号 进行模糊查询");

		jp.add(jtf, BorderLayout.CENTER);

		search = new JButton("搜索");
		search.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// do search
				final Thread t = new Thread(new SearchOrderListRunnable(jtf
						.getText().trim(), new DataOperate<Data_OrderList>() {

					@Override
					public void onDataReturned(RemoteData<Data_OrderList> result) {
						datas.clear();

						if (dialog != null)
							dialog.setVisible(false);

						datas.clear();
						if (!result.isSuccess) {
							JOptionPane.showMessageDialog(
									SearchOrderListDialog.this, result.message,
									"程序异常", JOptionPane.ERROR_MESSAGE);

						} else {
							if (result.datas.size() == 0) {
								// 为查询到数据
							} else {
								datas.addAll(result.datas);

							}
						}
						if (jtable.getModel() instanceof AbstractTableModel) {
							((AbstractTableModel) jtable.getModel())
									.fireTableDataChanged();
						}

					}
				}));
				t.start();
				dialog = new LoadingDialog(SearchOrderListDialog.this,
						"正在加载数据，请稍候...", new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								t.interrupt();
								dialog = null;
							}
						});

				dialog.setVisible(true);

			}
		});

		jp.add(search, BorderLayout.EAST);

		JPanel contentPane = new JPanel(new BorderLayout());

		contentPane.add(jp, BorderLayout.NORTH);

		jtable = new JTable();
		datas = new ArrayList<Data_OrderList>();
		jtable.setModel(new OrderListDataModel(datas));

		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				jtable.getModel());
		jtable.setRowSorter(sorter);
		jtable.setRowHeight(20);
		jtable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// code
					System.out.print("jt getClickCount ");

					int selectedRow = jtable.convertRowIndexToModel(jtable
							.getSelectedRow());
					result = datas.get(selectedRow);
					setVisible(false);

				}
			}
		});
		contentPane.add(new JScrollPane(jtable), BorderLayout.CENTER);
		setContentPane(contentPane);
		setSize(new Dimension(800, 600));
		setLocationRelativeTo(null);

		// TODO Auto-generated constructor stub
	}

	public Data_OrderList getResult() {
		result = null;

		this.setVisible(true);
		return result;
	}

	private class SearchOrderListRunnable implements Runnable {

		private DataOperate<Data_OrderList> operate;
		private String key;

		public SearchOrderListRunnable(String key,
				DataOperate<Data_OrderList> operate) {
			this.operate = operate;
			this.key = key;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			final RemoteData<Data_OrderList> result = new RemoteData<Data_OrderList>();
			result.isSuccess = true;
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {

				conn = SQLWorker.getConnection();
				st = conn.createStatement();
				String fitKey = "%" + key + "%";
				rs = st.executeQuery(String.format(sql, fitKey, fitKey, fitKey));
				List<Data_OrderList> datas = new ArrayList<Data_OrderList>(
						10000);
				while (rs.next()) {

					Data_OrderList data = new Data_OrderList();
					data.cName = rs.getString(rs.findColumn("cName"));
					data.orderName = rs.getString(rs.findColumn("orderName"));
					data.productName = rs.getString(rs
							.findColumn("productName"));
					data.customerName = rs.getString(rs
							.findColumn("customerName"));

					data.orderList_id = rs.getString(rs
							.findColumn("orderList_id"));
					data.productName = rs.getString(rs
							.findColumn("productName"));
					data.salesMan = rs.getString(rs.findColumn("salesMan"));
					data.orderDay = rs.getString(rs.findColumn("orderDay"));

					data.promptDay = rs.getString(rs.findColumn("promptDay"));

					data.qty = rs.getFloat(rs.findColumn("qty"));
					datas.add(data);

				}

				result.datas = datas;

			} catch (Throwable t) {

				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				result.isSuccess = false;
				result.message = sw.toString();

				pw.close();
				try {
					sw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} finally {
				try {
					rs.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				try {
					st.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}

				try {
					conn.close();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					operate.onDataReturned(result);

				}
			});

		}

		private String sql = " select ol.id as orderList_id,ol.qty,o.orderDay,o.promptDay, ol.cName,o.name as orderName,p.name as productName,c.name as customerName,o.salesMan from t_orderlist ol inner join t_order o on ol.order_id=o.id  inner join t_product p on p.id=ol.product_id  INNER JOIN  T_Customer c ON c.id = o.CustomerID   where o.deleted=0  and (o.name like '%s' or p.name like '%s' or ol.cName like '%s') order by o.orderDay desc ";

	}

}
