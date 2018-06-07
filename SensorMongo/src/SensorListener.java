import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import MongoConnection.MongoConnection;

public class SensorListener {
	
	private String topic        = "iscte_sid_2018_teste";
	private String broker       = "tcp://iot.eclipse.org:1883";
	private String clientId     = "Reader";
	private MemoryPersistence persistence = new MemoryPersistence();
	private MqttClient sampleClient;
	private MqttConnectOptions connOpts;
	private MqttCallback callback;
	private MongoConnection mongoConnection;
	
	public SensorListener(){
        try {
        	//Definir a ligação 
            sampleClient = new MqttClient(broker, clientId, persistence);
            connOpts = new MqttConnectOptions();
            
            connOpts.setCleanSession(true);
            
            //Implementação do Callback
            callback = new MqttCallback() {
				
				@Override
				public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
					//Caso exista uma ligação estabelecida ao mongo
					//é realizado o insert
					if(mongoConnection.isConnected())
						mongoConnection.insertData(arg1);
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
					System.out.println("deliveryComplete");
					
				}
				
				@Override
				public void connectionLost(Throwable arg0) {
					System.out.println("connectionLost");
					//Força uma nova ligação
					connect();
				}
			};
			//Ligação ao Mongo
			mongoConnection = new MongoConnection();

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
	        	//Estabelecer Ligação ao Servidor
				sampleClient.connect(connOpts);
				System.out.println("Connected");
				
				//Definir a interface Callback
				sampleClient.setCallback(callback);
	            
				//Subscrever o Topico escolhido
				sampleClient.subscribe(topic);
				
				System.out.println("Subscribed to: "+topic);
				
//				...
				
			} catch(MqttException me) {
	            System.out.println("reason "+me.getReasonCode());
	            System.out.println("msg "+me.getMessage());
	            System.out.println("loc "+me.getLocalizedMessage());
	            System.out.println("cause "+me.getCause());
	            System.out.println("excep "+me);
	            me.printStackTrace();
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

}
