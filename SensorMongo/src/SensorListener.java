import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.mongodb.*;

public class SensorListener {
	public static void main(String[] args) {

        String topic        = "iscte_sid_2018_teste";
        String content      = "Message from MqttPublishSample";
        int qos             = 0;
        String broker       = "tcp://iot.eclipse.org:1883";
        String clientId     = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
        	//-----Ligar ao Servidor-----//
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            //---------------------------//
            sampleClient.subscribe(topic);
            MqttCallback callback = new MqttCallback() {
				
				@Override
				public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
					System.out.println(arg1.toString());
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
					System.out.println("deliveryComplete");
					
				}
				
				@Override
				public void connectionLost(Throwable arg0) {
					System.out.println("connectionLost");
					
				}
			};
            sampleClient.setCallback(callback);
            
            //-----Disconnect-----//
//            sampleClient.disconnect();
//            System.out.println("Disconnected");
            //--------------------//
//            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
