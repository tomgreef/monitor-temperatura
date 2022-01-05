package monitor;

import static spark.Spark.get;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

public class Main {
	static final String REDIS_HOST = System.getenv().getOrDefault("REDIS_HOST", "localhost");

	public static void main(String[] args) {
		get("/listar", (req, res) -> new Gson().toJson(getValues(-1)));
		// NO SE MUESTRAN LOS VALORES EN EL GET
		// POST NO ME FUNCIONA AQUÍ, me devuelve un 404 NOT FOUND
		get("/nuevo/:dato", (req, res) -> {
			if (req.params(":dato") != null) {
				Measurement m = new Measurement(req.params(":dato"));
				writeValue(m);
				return ("ADD ->\t" + m.getDate() + ": " + m.getValue());
			} else
				return "Error en el formato del URL";
		});
		get("/limpiar", (req, res) -> {
			flushRegisters();
			return "Registro de datos limpiado";
		});
		get("/grafica", (req, res) -> GraficaChart.crea_grafica());
		get("/listajson", (req, res) -> new Gson().toJson(getValues(10)));
	}

	public static void writeValue(Measurement m) {
		try {
			Jedis jedis = new Jedis(REDIS_HOST);
			// jedis.flushAll();

			DateFormat df = new SimpleDateFormat("dd MM yyyy HH:mm:ss");

			jedis.lpush("queue#dates", df.format(m.getDate()));
			jedis.lpush("queue#values", String.valueOf(m.getValue()));
			System.out.println();

			jedis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static ArrayList<Measurement> getValues(int length) {
		ArrayList<Measurement> list = new ArrayList<Measurement>();

		try {
			Jedis jedis = new Jedis(REDIS_HOST);
			String date = "", value = "";
			long last;
			
			if(length == -1 || jedis.llen("queue#dates") < length)
				last = jedis.llen("queue#dates");
			else {
				last = length;		
			}
				

			for (long i = 0; i < last; i++) {
				date = jedis.lindex("queue#dates", i);
				value = jedis.lindex("queue#values", i);

				DateFormat df = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
				Date d = df.parse(date);
				Measurement m = new Measurement(d, value);

				list.add(m);
			}

			jedis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static void flushRegisters() {
		try {
			Jedis jedis = new Jedis(REDIS_HOST);
			jedis.flushAll();
			jedis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
