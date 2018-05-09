package MongoConnection;



import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
	
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
		boolean verifier = verifyMessage(x);
		if (verifier) {
			BasicDBObject messageMongo = new BasicDBObject()
												.append("temperature", x.get("temperature").toString().replace(",", "."))
												.append("humidity", x.get("humidity").toString().replace(",", "."))
												.append("date", x.get("date").toString())
												.append("time", x.get("time").toString());
			table.insert(messageMongo);
			System.out.println("Mensagem Enviada");
		} else {
			System.out.println("Mensagem Descartada");
		}
	}
	
	private boolean verifyMessage(JSONObject x) {
		try {
			Double humD = Double.parseDouble(x.get("temperature").toString());
			Double tempD = Double.parseDouble(x.get("humidity").toString());
			Date dateD = sdf1.parse(x.get("date").toString());
			Date timeD = sdf2.parse(x.get("time").toString());
		}catch (Exception e) {
			return false;
		}
		return true;
	}
}
