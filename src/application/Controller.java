package application;

import javafx.application.Platform;

public class Controller {
	private static Controller instance;
	private SocketConnection socketConn;
	private Client defaultClient;
	private int state;

	private Controller(int roomNumber) {
		if (initClient(roomNumber)) {
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

	public static synchronized Controller getInstance(int roomNumber) {
		if (instance == null)
			instance = new Controller(roomNumber);
		return instance;
	}

	public boolean initClient(int roomNumber) {
		socketConn = new SocketConnection();
		if (socketConn.createConnection() && socketConn.sendStartUp(roomNumber)) {
			defaultClient = new Client(roomNumber);
			return true;
		} else
			return false;
	}

	public boolean sendDesiredTemperatureAndWind(double temperature, Wind wind) {
		boolean tmp = socketConn.sendDesiredTemperatureAndWind(temperature, wind);
		if (temperature > defaultClient.getTemperature())
			socketConn.sendStopWind();
		return tmp;
	}

	public boolean setDesiredTemperatureAndWind(double temperature, Wind wind) {
		boolean tmp = socketConn.sendDesiredTemperatureAndWind(temperature, wind);
		if (temperature > defaultClient.getTemperature())
			socketConn.sendStopWind();
		return tmp;
	}

	public void decode() {
		socketConn.decode(defaultClient);
	}

	public int getState() {
		return state;
	}

	public void RoomTemperature() {
		if (defaultClient.getState() == State.RUNNING) {
			if (defaultClient.getMode() == Mode.COOL
					&& defaultClient.getTemperature() > defaultClient.getGoalTemperature()
					&& defaultClient.getGoalTemperature() < 25) {

				if (defaultClient.getWind() == Wind.LOW) {
					if (defaultClient.getTemperature() - 0.4 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() - 0.4);
				} else if (defaultClient.getWind() == Wind.MEDIUM) {
					if (defaultClient.getTemperature() - 0.5 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() - 0.5);
				} else {
					if (defaultClient.getTemperature() - 0.6 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() - 0.6);
				}

			} else if (defaultClient.getMode() == Mode.HEAT
					&& defaultClient.getTemperature() < defaultClient.getGoalTemperature()
					&& defaultClient.getGoalTemperature() > 25) {

				if (defaultClient.getWind() == Wind.LOW) {
					if (defaultClient.getTemperature() + 0.4 > defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() + 0.4);
				} else if (defaultClient.getWind() == Wind.MEDIUM) {
					if (defaultClient.getTemperature() + 0.5 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() + 0.5);
				} else {
					if (defaultClient.getTemperature() + 0.6 < defaultClient.getGoalTemperature())
						defaultClient.setTemperature(defaultClient.getGoalTemperature());
					else
						defaultClient.setTemperature(defaultClient.getTemperature() + 0.6);
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
			if (defaultClient.getTemperature() > 25) {
				defaultClient.setTemperature(defaultClient.getTemperature() - 0.1);
			} else if (defaultClient.getTemperature() < 25) {
				defaultClient.setTemperature(defaultClient.getTemperature() + 0.1);
			}
		}
	}
}
