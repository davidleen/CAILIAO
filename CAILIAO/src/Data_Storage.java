
public class Data_Storage {
	String storageList_id;
	public String dept;
	public String code;
	public String name;
	public String fullName;
	public float qty;
	public float price;
	public String date;
	public String unit;
	public String ticketNumber;
	public String type;
	public String orderList_id;
	public String orderName;
	public String productName;
	public String cName;
	public String memo;
	public String storageOrderList_id;
	public boolean changed=false;
	 
	public Data_Storage() {
		// TODO Auto-generated constructor stub
		
		
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Data_Storage [storageList_id=");
		builder.append(storageList_id);
		builder.append(", dept=");
		builder.append(dept);
		builder.append(", code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", qty=");
		builder.append(qty);
		builder.append(", price=");
		builder.append(price);
		builder.append(", date=");
		builder.append(date);
		builder.append(", unit=");
		builder.append(unit);
		builder.append(", ticketNumber=");
		builder.append(ticketNumber);
		builder.append(", type=");
		builder.append(type);
		builder.append(", orderList_id=");
		builder.append(orderList_id);
		builder.append(", orderName=");
		builder.append(orderName);
		builder.append(", productName=");
		builder.append(productName);
		builder.append(", cName=");
		builder.append(cName);
		builder.append(", memo=");
		builder.append(memo);
		builder.append(", storageOrderList_id=");
		builder.append(storageOrderList_id);
		builder.append(", changed=");
		builder.append(changed);
		builder.append("]");
		return builder.toString();
	}

}
