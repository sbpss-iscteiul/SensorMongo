import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SensorWriter {
	
	private String topic        = "iscte_sid_2018_teste";
	private int qos             = 0;
	private String broker       = "tcp://iot.eclipse.org:1883";
	private String clientId		= "Writer";
	private MemoryPersistence persistence = new MemoryPersistence();
	private MqttClient sampleClient;
	private MqttConnectOptions connOpts;
	private MqttCallback callback;
	
	public SensorWriter() {
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
	}

	public void connect() {
			try {
	        	System.out.println("Connecting to broker: "+broker);
				sampleClient.connect(connOpts);
				System.out.println("Connected");
				
				sampleClient.setCallback(callback);
	            
				sampleClient.subscribe(topic);
				
				System.out.println("Subscribed to: "+topic);
				
			} catch(MqttException me) {
	            System.out.println("reason "+me.getReasonCode());
	            System.out.println("msg "+me.getMessage());
	            System.out.println("loc "+me.getLocalizedMessage());
	            System.out.println("cause "+me.getCause());
	            System.out.println("excep "+me);
	            me.printStackTrace();
	        }
			
	}
	
	private void publishMessage(String content) {
        try {
        	System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
			sampleClient.publish(topic, message);
			System.out.println("Message published");
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			sampleClient.disconnect();
			System.out.println("Disconnected");
		} catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
	}
	
	public void messageGenerator() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
		DecimalFormat df = new DecimalFormat("#0.00");
		Date now = new Date();
		double hum = 0.0;
		double temp = 0.0;
		for (int i = 0; i < 10; i++) {
			try {
				temp = (Math.random()*5);
				hum = 5+ (Math.random()*5);
				now = new Date();
				String string = String.format("{\"temperature\":\"%s\", \"humidity\": \"%s\", \"date\": \"%s\", \"time\": \"%s\"}", df.format(temp),df.format(hum),sdf1.format(now),sdf2.format(now));
				publishMessage(string);
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
