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

public class SearchDeptDialog extends JDialog {

	/**
	 * 
	 */

	private JTable jtable;
	private List<Data_Dept> datas;
	private static final long serialVersionUID = 1L;
	private Data_Dept result;

	public SearchDeptDialog(JFrame owner, String key) {
		super(owner, "挑选部门");
		this.setModal(true);

		JPanel jp = new JPanel(new BorderLayout());
		JLabel jlable = new JLabel("合同/客号/货号:");
		jp.add(jlable, BorderLayout.WEST);
		new Thread(new SearchDeptRunnable(key, new DataOperate<Data_Dept>() {

			@Override
			public void onDataReturned(RemoteData<Data_Dept> result) {
				// TODO Auto-generated method stub
				datas.clear();

				datas.clear();
				if (!result.isSuccess) {
					JOptionPane.showMessageDialog(SearchDeptDialog.this,
							result.message, "程序异常", JOptionPane.ERROR_MESSAGE);

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
		})).start();

		JPanel contentPane = new JPanel(new BorderLayout());

		jtable = new JTable();
		datas = new ArrayList<Data_Dept>();
		jtable.setModel(new DeptDataModal(datas));

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
		setSize(new Dimension(400, 600));
		setLocationRelativeTo(null);

		// TODO Auto-generated constructor stub
	}

	public Data_Dept getResult() {
		result = null;

		this.setVisible(true);
		return result;
	}

	private class SearchDeptRunnable implements Runnable {

		private DataOperate<Data_Dept> operate;
		private String key;

		public SearchDeptRunnable(String key, DataOperate<Data_Dept> operate) {
			this.operate = operate;
			this.key = key;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			final RemoteData<Data_Dept> result = new RemoteData<Data_Dept>();
			result.isSuccess = true;
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {

				conn = SQLWorker.getConnection();
				st = conn.createStatement();
				String fitKey = "%" + key + "%";
				rs = st.executeQuery(String.format(sql, fitKey,fitKey));
				List<Data_Dept> datas = new ArrayList<Data_Dept>(100);
				while (rs.next()) {

					Data_Dept data = new Data_Dept();
					data.deptName = rs.getString(rs.findColumn("name"));
					data.deptFullName= rs.getString(rs.findColumn("fullName"));
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

		private String sql = "  select name,fullName from t_dept where name like '%s' or fullName like '%s' ";
	}

	private class DeptDataModal extends AbstractTableModel {

		private List<Data_Dept> datas;

		private String[] tableHead = new String[] { " 部 门 名 称    ","部 门 全 称       " };

		private static final long serialVersionUID = 1L;

		public DeptDataModal(List<Data_Dept> datas) {
			// TODO Auto-generated constructor stub
			this.datas = datas;
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return tableHead.length;

		}

		@Override
		public String getColumnName(int index) {
			return tableHead[index];

		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			Data_Dept data = datas.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return data.deptName;
			case 1:
				return data.deptFullName;

			}

			return null;

		}

	}

}
