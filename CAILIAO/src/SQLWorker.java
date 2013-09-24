import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLWorker {

	private static String url;// =
								// "jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=YSJJ";
	private static String classforname;// =
										// "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	private static String uid;// = "sa";
	private static String pwd;// = "sa";

	static {
		url = PropertyWorker.readData("url");
		uid = PropertyWorker.readData("userName");
		pwd = PropertyWorker.readData("password");
		classforname = PropertyWorker.readData("classforname");
	}

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(classforname);
			if (conn == null || conn.isClosed())
				conn = DriverManager.getConnection(url, uid, pwd);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		catch (SQLException ex) {
			throw new RuntimeException(ex);

		}

		return conn;
	}

}
