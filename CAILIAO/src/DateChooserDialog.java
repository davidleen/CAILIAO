import java.text.SimpleDateFormat;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.freixas.jcalendar.DateEvent;
import org.freixas.jcalendar.DateListener;
import org.freixas.jcalendar.JCalendar;

public class DateChooserDialog extends JDialog {

	public static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd");
	private String date;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateChooserDialog(JFrame owner, String orignDate) {
		super(owner);
		// TODO Auto-generated constructor stub
		this.date = orignDate;
		setModal(true);
		setSize(500, 400);
		setLocationRelativeTo(owner);
		final JCalendar jc = new JCalendar();
		try {
			jc.setDate(sdf.parse(date));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		getContentPane().add(jc);
		jc.addDateListener(new DateListener() {

			@Override
			public void dateChanged(DateEvent arg0) {
				// TODO Auto-generated method stub
				date = sdf.format(jc.getDate());
				setVisible(false);

			}
		});
	}

	public String getResult() {
		setVisible(true);
		return date;

	}

}
