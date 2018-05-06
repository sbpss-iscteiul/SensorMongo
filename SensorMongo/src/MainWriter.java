
public class MainWriter {
	public static void main(String[] args) {
		SensorWriter writer = new SensorWriter();
		writer.connect();
		writer.messageGenerator();
		writer.disconnect();
	}
}
