package application;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Client{
	private boolean power;
	private static ReadWriteLock powerLock = new ReentrantReadWriteLock();
	private int roomNumber;
	private Mode mode;
	private State state;
	private double temperature;
	private static ReadWriteLock temperatureLock = new ReentrantReadWriteLock();
	private double goalTemperature;
	private static ReadWriteLock goalTemperatureLock = new ReentrantReadWriteLock();
	private Wind wind;
	private double fee;
	private static ReadWriteLock feeLock = new ReentrantReadWriteLock();
	private double consumption;
	private static ReadWriteLock consumptionLock = new ReentrantReadWriteLock();
	private double sumFee;
	private static ReadWriteLock sumFeeLock = new ReentrantReadWriteLock();
	private double sumConsumption;
	private static ReadWriteLock sumConsumptionLock = new ReentrantReadWriteLock();

	public Client(int roomNumber) {
		this.roomNumber = roomNumber;
		mode = Mode.COOL;
		state = State.STANDBY;
		temperature = 25;
		goalTemperature = 25;
		wind = Wind.MEDIUM;
		fee = 0;
		consumption = 0;
		sumFee = 0;
		sumConsumption = 0;
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
		double tmp = -1;
		try {
			goalTemperatureLock.readLock().lock();
			tmp = goalTemperature;
		} finally {
			goalTemperatureLock.readLock().unlock();
		}
		return tmp;
	}
	
	public boolean getPower() {
		boolean tmp = true;
		try {
			powerLock.readLock().lock();
			tmp = power;
		} finally {
			powerLock.readLock().unlock();
		}
		return tmp;
	}

	public Wind getWind() {
		return wind;
	}

	public double getFee() {
		double tmp;
		try {
			feeLock.readLock().lock();
			tmp = fee;
		} finally {
			feeLock.readLock().unlock();
		}
		return tmp;
	}

	public double getConsumption() {
		double tmp;
		try {
			consumptionLock.readLock().lock();
			tmp = consumption;
		} finally {
			consumptionLock.readLock().unlock();
		}
		return tmp;
	}
	
	public double getSumFee() {
		double tmp;
		try {
			sumFeeLock.readLock().lock();
			tmp = sumFee;
		} finally {
			sumFeeLock.readLock().unlock();
		}
		return tmp;
	}
	
	public double getSumConsumption() {
		double tmp;
		try {
			sumConsumptionLock.readLock().lock();
			tmp = sumConsumption;
		} finally {
			sumConsumptionLock.readLock().unlock();
		}
		return tmp;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setTemperature(double temperature) {
		try {
			temperatureLock.writeLock().lock();
			this.temperature = temperature;
		} finally {
			temperatureLock.writeLock().unlock();
		}
	}

	public void setGoalTemperature(double goalTemperature) {
		try {
			goalTemperatureLock.writeLock().lock();
			this.goalTemperature = goalTemperature;
		} finally {
			goalTemperatureLock.writeLock().unlock();
		}
	}
	
	public void setPower(boolean power) {
		try {
			powerLock.writeLock().lock();
			this.power = power;
		} finally {
			powerLock.writeLock().unlock();
		}
	}

	public void setWind(Wind wind) {
		this.wind = wind;
	}

	public void setFee(double fee) {
		try {
			feeLock.writeLock().lock();
			this.fee = fee;
		} finally {
			feeLock.writeLock().unlock();
		}
	}

	public void setConsumption(double consumption) {
		try {
			consumptionLock.writeLock().lock();
			this.consumption = consumption;
		} finally {
			consumptionLock.writeLock().unlock();
		}
	}

	public void setSumFee(double sumFee) {
		try {
			sumFeeLock.writeLock().lock();
			this.sumFee = sumFee;
		} finally {
			sumFeeLock.writeLock().unlock();
		}
	}

	public void setSumConsumption(double sumConsumption) {
		try {
			sumConsumptionLock.writeLock().lock();
			this.sumConsumption = sumConsumption;
		} finally {
			sumConsumptionLock.writeLock().unlock();
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
