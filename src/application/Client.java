package application;

public class Client {
	private int roomNumber;
	private Mode mode;
	private State state;
	private double temperature;
	private double goalTemperature;
	private Wind wind;
	private double fee;
	private double consumption;
	
	public Client(int roomNumber) {
		this.roomNumber = roomNumber;
		mode=Mode.COOL;
		state=State.STANDBY;
		temperature=27;
		goalTemperature=27;
		wind=Wind.MEDIUM;
		fee=0;
		consumption=0;
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
		return temperature;
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
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public void setGoalTemperature(double goalTemperature) {
		this.goalTemperature = goalTemperature;
	}

	public void setWind(Wind wind) {
		this.wind = wind;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}
}

enum Mode {
	COOL,HEAT//制冷，制热
}

enum State {
	RUNNING, WAITING, STANDBY//运行，等待，待机
}

enum Wind {
	LOW(0), MEDIUM(1), HIGH(2);//低速风，中速风，高速风
	private final int value;
    private Wind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
