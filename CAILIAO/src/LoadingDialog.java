import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class LoadingDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoadingDialog(Frame owner, String message,
			ActionListener cancleListener) {
		super(owner);
		iniDialog(message, cancleListener);
		// TODO Auto-generated constructor stub
	}

	public LoadingDialog(Dialog owner, String message,
			ActionListener cancleListener) {
		super(owner);
		iniDialog(message, cancleListener);
		// TODO Auto-generated constructor stub
	}

	private void iniDialog(String message, final ActionListener cancleListener) {
		setModal(true);
		final JPanel mainPane = new JPanel(null);
		JProgressBar progressBar = new JProgressBar();
		JLabel lbStatus = new JLabel("" + message);
		lbStatus.setHorizontalAlignment(JLabel.CENTER);
		JButton btnCancel = new JButton("Cancel");
		progressBar.setIndeterminate(true);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setVisible(false);
				cancleListener.actionPerformed(e);
			}
		});
		mainPane.setLayout(new BorderLayout());
		mainPane.add(progressBar,BorderLayout.NORTH);
		mainPane.add(lbStatus,BorderLayout.CENTER);
		  mainPane.add(btnCancel,BorderLayout.SOUTH);
		  mainPane.setBackground(Color.white);
		getContentPane().add(mainPane);
		setUndecorated(true); // 除去title
		setResizable(true);
		setSize(300, 100);
		setLocationRelativeTo(getParent()); // 设置此窗口相对于指定组件的位置
		 setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); 

	}

}
