package application;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Client implements Subject {
	private int roomNumber;
	private Mode mode;
	private State state;
	private double temperature;
	private double goalTemperature;
	private Wind wind;
	private double fee;
	private double consumption;
	private final ArrayList<Observer> observers;
	private static ReadWriteLock temperatureLock = new ReentrantReadWriteLock();

	public Client(int roomNumber) {
		this.roomNumber = roomNumber;
		mode = Mode.COOL;
		state = State.STANDBY;
		temperature = 27;
		goalTemperature = 27;
		wind = Wind.MEDIUM;
		fee = 0;
		consumption = 0;
		observers = new ArrayList<Observer>();
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public Mode getMode() {
		return mode;
	}

	public State getState() {
		return state;
	}

	public double getTemperature() {
		double tmp = -1;
		try {
			temperatureLock.readLock().lock();
			tmp = temperature;
		} finally {
			temperatureLock.readLock().unlock();
		}
		return tmp;
	}

	public double getGoalTemperature() {
		return goalTemperature;
	}

	public Wind getWind() {
		return wind;
	}

	public double getFee() {
		return fee;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
		this.notifyObservers();
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		this.notifyObservers();
	}

	public void setState(State state) {
		this.state = state;
		this.notifyObservers();
	}

	public void setTemperature(double temperature) {
		try {
			temperatureLock.writeLock().lock();
			this.temperature = temperature;
			this.notifyObservers();
		} finally {
			temperatureLock.writeLock().unlock();
		}
	}

	public void setGoalTemperature(double goalTemperature) {
		this.goalTemperature = goalTemperature;
	}

	public void setWind(Wind wind) {
		this.wind = wind;
		this.notifyObservers();
	}

	public void setFee(double fee) {
		this.fee = fee;
		this.notifyObservers();
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
		this.notifyObservers();
	}

	public void registerObserver(Observer observer) {
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public void notifyObservers() {
		for (Observer o : observers) {
			o.update(this);
		}
	}

}

enum Mode {
	COOL, HEAT// 制冷，制热
}

enum State {
	RUNNING, WAITING, STANDBY// 运行，等待，待机
}

enum Wind {
	LOW(0), MEDIUM(1), HIGH(2);// 低速风，中速风，高速风
	private final int value;

	private Wind(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
