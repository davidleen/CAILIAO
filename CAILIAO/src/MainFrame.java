import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.freixas.jcalendar.DateEvent;
import org.freixas.jcalendar.DateListener;
import org.freixas.jcalendar.JCalendar;

public class MainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JDialog loadingDialog;
	private List<Data_Storage> lists;

	private JTable jt;
	private JTextField jtf;
	private JButton jb_start, jb_end, jb_search, jb_save, jb_export;

	private DateChooserDialog dateDialog;

	public MainFrame() throws HeadlessException {
		// TODO Auto-generated constructor stub

		iniCoponent();

	}

	private void iniCoponent() {

		setTitle("材料领料");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.setProperty("swing.pla metal.controlFont", "宋体");

		lists = new ArrayList<Data_Storage>();

		jt = new JTable(new StorageDataModel(lists));

		setRowSorter(jt);
		jt.setRowHeight(20);

		JScrollPane scrollPane = new JScrollPane(jt);
		jt.setFillsViewportHeight(true);
		jt.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				

				if (jt.convertColumnIndexToModel(jt.getSelectedColumn()) < StorageDataModel.tableHeadWidth.length - 4)
					return;
				
				final int selectedRow = jt.convertRowIndexToModel(jt
						.getSelectedRow());

				if (e.getClickCount() == 2
						&& e.getButton() == MouseEvent.BUTTON1) {

					System.out.print("jt getClickCount ");
					SearchOrderListDialog d = new SearchOrderListDialog(
							MainFrame.this, "挑选与出入库配对的货款");
					Data_OrderList data = d.getResult();
					if (data != null) {

						Data_Storage storage = lists.get(selectedRow);
						storage.orderList_id = data.orderList_id;
						storage.productName = data.productName;
						storage.orderName = data.orderName;
						storage.cName = data.cName;
						storage.changed = true;
						((AbstractTableModel) jt.getModel())
								.fireTableRowsUpdated(selectedRow, selectedRow);
					}

				} else {

					if (e.getButton() == MouseEvent.BUTTON3) {

						int row = e.getY() / jt.getRowHeight();
						if (row != selectedRow) {
							jt.setRowSelectionInterval(row, row);

							return;
						}

						JPopupMenu popupMenu = new JPopupMenu();
						JMenuItem tableNameItem = new JMenuItem("删除");
						popupMenu.add(tableNameItem);
						popupMenu.show(e.getComponent(), e.getX(), e.getY());

						tableNameItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {

								Data_Storage storage = lists.get(selectedRow);
								if (!TextUtils.isEmpty(storage.orderList_id)) {
									storage.changed = true;
									storage.orderList_id = null;
									storage.orderName = "";
									storage.productName = "";
									storage.cName = "";
									((AbstractTableModel) jt.getModel())
											.fireTableRowsUpdated(selectedRow,
													selectedRow);

								}

							}
						});

					}

				}
			}
		});

		jt.setDefaultRenderer(Object.class, new CustomCellRender(lists));

		TableColumn column = null;
		for (int i = 0; i < jt.getColumnCount(); i++) {
			column = jt.getColumnModel().getColumn(i);

			column.setPreferredWidth(StorageDataModel.tableHeadWidth[i] * 2);

		}

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		jb_start = new JButton();

		jb_start.setText(DateChooserDialog.sdf.format(new Date(Calendar
				.getInstance().getTimeInMillis() - 30l * 24 * 60 * 60 * 1000)));
		jb_start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dateDialog = new DateChooserDialog(MainFrame.this, jb_start
						.getText());
				jb_start.setText(dateDialog.getResult());

			}
		});

		jb_end = new JButton();
		jb_end.setText(DateChooserDialog.sdf.format(Calendar.getInstance()
				.getTime()));

		jb_end.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dateDialog = new DateChooserDialog(MainFrame.this, jb_end
						.getText());
				jb_end.setText(dateDialog.getResult());

			}
		});
		// 头部搜索
		JPanel jp_head = new JPanel(new FlowLayout());
		jp_head.add(new JLabel("部门名称："));
		jtf = new JTextField(20);
		jp_head.add(jtf);
		jtf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SearchDeptDialog d = new SearchDeptDialog(MainFrame.this, jtf
						.getText());
				Data_Dept data = d.getResult();
				jtf.setText(data.deptFullName);
			}
		});

		jp_head.add(new JLabel("  日期选择--从: "));

		jp_head.add(jb_start);
		jp_head.add(new JLabel(" 到: "));

		jp_head.add(jb_end);

		jb_search = new JButton(" 搜索");
		jp_head.add(jb_search);
		jp_head.add(new JLabel("   "));

		jp_head.add(new JLabel("                                   "));

		jb_export = new JButton("导出excel");
		jp_head.add(jb_export);

		jb_export.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				JFileChooser jf = new JFileChooser();

				// 设置选择路径模式
				jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				// 设置对话框标题
				jf.setDialogTitle("请选择路径");
				if (JFileChooser.APPROVE_OPTION == jf
						.showOpenDialog(MainFrame.this)) {// 用户点击了确定
					File file = jf.getSelectedFile();// 取得路径选择

					JTableToExcel.export(file, jb_start.getText() + " 到  "
							+ jb_end.getText() + " 物料表 ", null, jt);
				}

			}
		});

		jb_search.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final Thread t = new Thread(new SearchStorageListRunnable(jtf
						.getText().trim(), jb_start.getText().trim(), jb_end
						.getText().trim(), new DataOperate<Data_Storage>() {

					@Override
					public void onDataReturned(RemoteData<Data_Storage> result) {
						// TODO Auto-generated method stub
						loadingDialog.setVisible(false);
						lists.clear();
						if (!result.isSuccess) {
							// 加载失败
							JOptionPane.showMessageDialog(MainFrame.this,
									result.message, "程序异常",
									JOptionPane.ERROR_MESSAGE);

						} else {
							if (result.datas.size() == 0) {
								// 为查询到数据
							} else {
								lists.addAll(result.datas);

							}
						}

						((AbstractTableModel) jt.getModel())
								.fireTableDataChanged();

					}
				}));
				t.start();
				loadingDialog = new LoadingDialog(MainFrame.this,
						"正在加载数据，请稍候...", new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								t.interrupt();
								loadingDialog = null;
							}
						});

				loadingDialog.setVisible(true);
			}
		});

		getContentPane().add(jp_head, BorderLayout.NORTH);

		jb_save = new JButton("保存修改");

		jb_save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				 
				final Thread t = new Thread(new SaveDataRunnable(lists,
						new DataOperate<Data_Storage>() {

							@Override
							public void onDataReturned(
									RemoteData<Data_Storage> result) {
								 
								loadingDialog.setVisible(false);

								if (result.isSuccess) {
									JOptionPane.showMessageDialog(
											MainFrame.this, "保存成功");

								} else {

									// 加载失败
									JOptionPane.showMessageDialog(
											MainFrame.this, result.message,
											"保存失败", JOptionPane.ERROR_MESSAGE);

								}

								((AbstractTableModel) jt.getModel())
										.fireTableDataChanged();

							}
						}));
				t.start();
				loadingDialog = new LoadingDialog(MainFrame.this,
						"正在保存数据，请稍候...", new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								 
								t.interrupt();
								loadingDialog = null;
							}
						});

				loadingDialog.setVisible(true);

			}
		});
		JPanel jp_south = new JPanel();
		jp_south.add(jb_save);
		getContentPane().add(jp_south, BorderLayout.SOUTH);
		setSize(new Dimension(1200, 800));
		pack();
		setLocationRelativeTo(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * 加载线程
	 * 
	 * @author david
	 * 
	 */
	private class SearchStorageListRunnable implements Runnable {

		private DataOperate<Data_Storage> dataOperate;
		String dept;
		String date_start;
		String date_end;

		public SearchStorageListRunnable(String dept, String date_start,
				String date_end, DataOperate<Data_Storage> dataOperate) {
			this.dataOperate = dataOperate;
			this.dept = dept;
			this.date_start = date_start;
			this.date_end = date_end;

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			final RemoteData<Data_Storage> result = new RemoteData<Data_Storage>();
			result.isSuccess = true;
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {

				conn = SQLWorker.getConnection();
				st = conn.createStatement();

				rs = st.executeQuery(String.format(SQL_SEARCH_STORGELIST,
						date_start, date_end, "%" + dept + "%"));
				List<Data_Storage> datas = new ArrayList<Data_Storage>(10000);
				while (rs.next()) {

					Data_Storage data = new Data_Storage();
					data.cName = rs.getString(rs.findColumn("CName"));
					data.code = rs.getString(rs.findColumn("code"));
					data.name = rs.getString(rs.findColumn("name"));
					data.fullName = rs.getString(rs.findColumn("fullName"));
					data.price = rs.getFloat(rs.findColumn("price"));
					data.qty = rs.getFloat(rs.findColumn("qty"));
					data.unit = rs.getString(rs.findColumn("unit"));
					data.ticketNumber = rs
							.getString(rs.findColumn("ticketNum"));
					data.orderName = rs.getString(rs.findColumn("orderName"));
					data.orderList_id = rs.getString(rs
							.findColumn("orderList_id"));
					data.productName = rs.getString(rs
							.findColumn("productName"));
					data.storageList_id = rs.getString(rs
							.findColumn("storageList_id"));
					data.date = rs.getString(rs.findColumn("date"));
					data.type = rs.getString(rs.findColumn("type"));
					data.dept = rs.getString(rs.findColumn("deptName"));
					data.memo = rs.getString(rs.findColumn("memo"));
					data.storageOrderList_id = rs.getString(rs
							.findColumn("storageOrderList_id"));

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
					dataOperate.onDataReturned(result);

				}
			});
		}
	}

	/**
	 * 保存操作线程
	 * 
	 * @author g42
	 * 
	 */

	private class SaveDataRunnable implements Runnable {

		private DataOperate<Data_Storage> operate;
		private List<Data_Storage> dataToSave;

		public SaveDataRunnable(List<Data_Storage> dataToSave,
				DataOperate<Data_Storage> operate) {
			this.operate = operate;
			this.dataToSave = dataToSave;
		}

		@Override
		public void run() {
			 
			final RemoteData<Data_Storage> result = new RemoteData<Data_Storage>();
			result.isSuccess = true;
			Connection conn = null;
			Statement st = null;
			try {

				conn = SQLWorker.getConnection();
				st = conn.createStatement();
				for (Data_Storage storage : dataToSave) {
					if (storage.changed) {

						int affectRow = 0;
						//

						if (TextUtils.isEmpty(storage.storageOrderList_id)) {
							// insert
							System.out.println(" execute insert ");
							String id = String.valueOf(Calendar.getInstance()
									.getTimeInMillis());
							
							System.out.println("generate id:"+id);
							// 插入新数据
							affectRow = st
									.executeUpdate(String
											.format(" insert into  t_storageOrderlist(id,storagelist_id,orderlist_id,memo) values ('%s','%s','%s','%s')",
													id, storage.storageList_id,
													storage.orderList_id,
													storage.memo == null ? ""
															: storage.memo));
							if (affectRow > 0) {
								storage.storageOrderList_id = id;
							}

						}else
						if (!TextUtils.isEmpty(storage.storageOrderList_id)
								&& TextUtils.isEmpty(storage.orderList_id)) {
							System.out.println(" execute delete ");
							// 删除记录。
							affectRow = st
									.executeUpdate(String
											.format(" delete from  t_storageOrderlist where id='%s' ",
													storage.storageOrderList_id));

							if (affectRow > 0) {
								storage.storageOrderList_id = null;

							}

						} else {
							// update

							System.out.println(" execute update ");
							affectRow = st
									.executeUpdate(String
											.format(" update t_storageOrderlist set storagelist_id='%s',orderlist_id='%s',memo='%s' where id='%s'",
													storage.storageList_id,
													storage.orderList_id,
													storage.memo == null ? ""
															: storage.memo,
													storage.storageOrderList_id));
						}

						if (affectRow == 1)
							storage.changed = false;// 标记保存成功
						else {
							throw new RuntimeException(" 保存 数据失败， 请重试！！！");
						}

					}

				}

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
					 
					e.printStackTrace();
				}
			} finally {
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
	}

	// 查询出入库sql字串。
	public static final String SQL_SEARCH_STORGELIST = " SELECT tsm.code, tsm.name, tsm.fullName, tsm.price, tsl.qty, tu.name AS unit, "
			+ "  tst.name AS type, tsr.name AS ticketNum, tsr.date_storage AS [date], tol.CName, "
			+ "    tor.name AS orderName, tp.name AS productName, td.fullName AS deptName, "
			+ "    tsol.orderlist_id AS orderlist_id, tsl.id AS storagelist_id,isnull(tsol.memo ,'') as memo,isnull(tsol.id,'') as storageOrderList_id "
			+ " FROM T_StorageList tsl INNER JOIN        (SELECT id, name, date_storage, storageType_id, dept_id "
			+ "       FROM T_StorageRecord        WHERE date_storage >= '%s' AND date_storage <= '%s') tsr ON "
			+ "    tsl.storage_id = tsr.id INNER JOIN         (SELECT id, code, name, fullName, price, unit1_id"
			+ "       FROM T_Storage_Material) tsm ON tsm.id = tsl.material_id INNER JOIN     T_Unit tu ON tsm.unit1_id = tu.id INNER JOIN "
			+ "    T_StorageType tst ON tst.id = tsr.storageType_id INNER JOIN         (SELECT id, name,fullName"
			+ "       FROM T_Dept        WHERE fullName LIKE '%s') td ON td.id = tsr.dept_id LEFT OUTER JOIN"
			+ "    T_StorageOrderList tsol ON tsl.id = tsol.storageList_id LEFT OUTER JOIN     T_OrderList tol ON tsol.orderlist_id = tol.ID LEFT OUTER JOIN"
			+ "        (SELECT id, name        FROM t_order) tor ON tor.id = tol.Order_ID LEFT OUTER JOIN"
			+ "        (SELECT id, name        FROM t_product) tp ON tp.id = tol.PRODUCT_ID  order by tsr.date desc ";

	/**
	 * 设置行排序
	 * 
	 * @param jtable
	 */
	private void setRowSorter(JTable jtable) {

		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				jtable.getModel());
		jtable.setRowSorter(sorter);

		jtable.setIntercellSpacing(new Dimension(5, 5));

	}

}
