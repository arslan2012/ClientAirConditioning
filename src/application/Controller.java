package application;

import javafx.application.Platform;

public class Controller {
	private static Controller instance;
	private SocketConnection socketConn;
	private Client defaultClient;
	private int state;

	private Controller(int roomNumber,String IP,int port) {
		if (initClient(roomNumber,IP,port)) {
			new Thread(() -> {
				while (true) {
					this.decode();
				}
			}).start();
			new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException exc) {
						throw new Error("Unexpected interruption", exc);
					}
					Platform.runLater(() -> this.RoomTemperature());
				}
			}).start();
			state = 1;
		} else
			state = 0;
	}

	public void registerObserver(Main main) {
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException exc) {
					throw new Error("Unexpected interruption", exc);
				}
				Platform.runLater(() -> main.update(defaultClient));
			}
		}).start();
	}

	public static synchronized Controller getInstance(int roomNumber,String IP,int port) {
		if (instance == null)
			instance = new Controller(roomNumber,IP,port);
		return instance;
	}

	public boolean initClient(int roomNumber,String IP,int port) {
		socketConn = new SocketConnection();
		if (socketConn.createConnection(IP,port) && socketConn.sendStartUp(roomNumber)) {
			defaultClient = new Client(roomNumber);
			return true;
		} else
			return false;
	}

	public boolean sendDesiredTemperatureAndWind(double temperature, Wind wind) {
		if ((temperature < defaultClient.getTemperature() && defaultClient.getMode()==Mode.COOL && temperature < 25)
				||(temperature > defaultClient.getTemperature() && defaultClient.getMode()==Mode.HEAT && temperature >25)){
			boolean tmp = socketConn.sendDesiredTemperatureAndWind(temperature, wind);
//			defaultClient.setGoalTemperature(temperature);
//			defaultClient.setWind(wind);
			return tmp;
			}
		else {
			boolean tmp = socketConn.sendStopWind();
			defaultClient.setGoalTemperature(temperature);
			defaultClient.setWind(wind);
			return tmp;
		}
	}

	public void decode() {
		socketConn.decode(defaultClient);
	}
	public boolean closeConnection(){
		return socketConn.closeConnection();
	}

	public int getState() {
		return state;
	}

	public void RoomTemperature() {
		if (defaultClient.getState() == State.RUNNING  && defaultClient.getPower()) {
			if (defaultClient.getMode() == Mode.COOL
					&& defaultClient.getTemperature() > defaultClient.getGoalTemperature()
					&& defaultClient.getGoalTemperature() < 25) {

				if (defaultClient.getWind() == Wind.LOW) {
					if (defaultClient.getTemperature() - 0.2 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() - 0.2);
				} else if (defaultClient.getWind() == Wind.MEDIUM) {
					if (defaultClient.getTemperature() - 0.25 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() - 0.25);
				} else {
					if (defaultClient.getTemperature() - 0.3 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() - 0.3);
				}

			} else if (defaultClient.getMode() == Mode.HEAT
					&& defaultClient.getTemperature() < defaultClient.getGoalTemperature()
					&& defaultClient.getGoalTemperature() > 25) {

				if (defaultClient.getWind() == Wind.LOW) {
					if (defaultClient.getTemperature() + 0.2 > defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() + 0.2);
				} else if (defaultClient.getWind() == Wind.MEDIUM) {
					if (defaultClient.getTemperature() + 0.25 > defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() + 0.25);
				} else {
					if (defaultClient.getTemperature() + 0.3 > defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() + 0.3);
				}
			}
			if (defaultClient.getMode() == Mode.COOL
					&& defaultClient.getTemperature() <= defaultClient.getGoalTemperature()) {
				socketConn.sendStopWind();
			} else if (defaultClient.getMode() == Mode.HEAT
					&& defaultClient.getTemperature() >= defaultClient.getGoalTemperature()) {
				socketConn.sendStopWind();
			}
		} else {
			if ((defaultClient.getTemperature() - defaultClient.getGoalTemperature() > 1.0
					&& defaultClient.getMode() == Mode.COOL
					&& defaultClient.getTemperature() > defaultClient.getGoalTemperature()
					&& defaultClient.getGoalTemperature() < 25)
					|| (defaultClient.getGoalTemperature() - defaultClient.getTemperature() > 1.0
							&& defaultClient.getMode() == Mode.HEAT
							&& defaultClient.getTemperature() < defaultClient.getGoalTemperature()
							&& defaultClient.getGoalTemperature() > 25))
				socketConn.sendDesiredTemperatureAndWind(defaultClient.getGoalTemperature(), defaultClient.getWind());
			if (defaultClient.getTemperature() <= 25.1 && defaultClient.getTemperature() >= 24.9){
				defaultClient.setTemperature(25.0);
			}else if (defaultClient.getTemperature() > 25.1) {
				defaultClient.setTemperature(defaultClient.getTemperature() - 0.1);
			} else if (defaultClient.getTemperature() < 24.9) {
				defaultClient.setTemperature(defaultClient.getTemperature() + 0.1);
			}
		}
	}
}
