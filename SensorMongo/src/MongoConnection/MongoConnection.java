package MongoConnection;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class MongoConnection {
	
	private Mongo mongo = null;
	private DB database = null;
	private DBCollection table=null;
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
	
	public MongoConnection() {
		System.out.println("Connection Process Beginning");
		connect();
	}
	
	@SuppressWarnings("deprecation")
	private void connect() {
		//Defini��o da liga��o ao Mongo
		mongo= new MongoClient("localhost",27017);
		try {
			//Obter Base de Dados
			database = mongo.getDB("HumidadeTemperatura");
		} catch (MongoException e) {
			System.out.println("apanhei isto");
		}
		//Obter Colec��o da Base de Dados
		table = database.getCollection("RegistosNaoMigrados");
	}
	
	public void insertData(MqttMessage message) {
			//Transforma��o da messagem para JSON
			JSONObject x = new JSONObject(message.toString());
			//Verifica��o da mensagem
			boolean verifier = verifyMessage(x);
			if (verifier) {
				//Constru��o de um dado em formato MongoDB
				BasicDBObject messageMongo;
				try {
					messageMongo = new BasicDBObject()
														.append("temperature", x.get("temperature").toString().replace(",", "."))
														.append("humidity", x.get("humidity").toString().replace(",", "."))
														.append("date", sdf1.format(sdf1.parse(x.get("date").toString())).replace("/", "-"))
														.append("time", sdf2.format(sdf2.parse(x.get("time").toString())));
					table.insert(messageMongo);
					System.out.println("Mensagem Enviada");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Mensagem Descartada");
			}
	}
	
	private boolean verifyMessage(JSONObject x) {
		//Verifica��o dos dados obtidos 
		//	Verificado se est�o preenchidos
		//	Verificado se est�o nos formatos correctos 
		try {
			Double humD = Double.parseDouble(x.get("temperature").toString().replace(",", "."));
			Double tempD = Double.parseDouble(x.get("humidity").toString().replace(",", "."));
			Date dateD = sdf1.parse(x.get("date").toString());
			Date timeD = sdf2.parse(x.get("time").toString());
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	public Boolean isConnected() {
		return mongo.getAddress()!=null;
	}
}
