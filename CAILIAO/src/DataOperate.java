import java.util.List;

public interface DataOperate<T> {

	public void onDataReturned(RemoteData<T> data);

}

class RemoteData<T> {
	public boolean isSuccess;
	List<T> datas;
	
	String message;
}