package MongoConnection;



import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

public class MongoConnection {
	
	private Mongo mongo = null;
	private DB database = null;
	private DBCollection table=null;
	
	public MongoConnection() {
		System.out.println("Connection Process Beginning");
		connect();
	}
	
	@SuppressWarnings("deprecation")
	private void connect() {
		mongo= new MongoClient("localhost",27017);
		database = mongo.getDB("HumTemp");
		table = database.getCollection("Registos");
	}
	
	public void insertData(MqttMessage message) {
		JSONObject x = new JSONObject(message.toString());
		BasicDBObject messageMongo = new BasicDBObject()
												.append("temperature", x.get("temperature").toString())
												.append("humidity", x.get("humidity").toString())
												.append("date", x.get("date").toString())
												.append("time", x.get("time").toString());
		table.insert(messageMongo);
	}
	
}
