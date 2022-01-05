package monitor;

import java.util.Date;

public class Measurement {
	private Date date;
	private double value;
	
	Measurement(String v){
		this.date = new Date();
		this.value = Double.parseDouble(v);
	}
	
	Measurement(Date d, String v){
		this.date = d;
		this.value = Double.parseDouble(v);
	}

	public Date getDate() {
		return date;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Measurement [date=" + date + ", value=" + value + "] \n";
	}
}
