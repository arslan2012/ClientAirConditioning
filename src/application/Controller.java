package application;

public class Controller{
	private static Controller instance;
	private SocketConnection socketConn;
	private Client defaultClient;
	private int state;
	private Controller(int roomNumber){
		if(initClient(roomNumber)){
			(new Thread(new getClientStats(this))).start();
			(new Thread(new RoomTemperature(this))).start();
			state = 1;
		}else state = 0;
	}
	public static synchronized Controller getInstance(int roomNumber) 
	{	
	   if ( instance == null )
	         instance = new Controller(roomNumber); 
	   return instance;
	}
	public boolean initClient(int roomNumber){
		socketConn = new SocketConnection();
		if (socketConn.createConnection() && socketConn.sendStartUp(roomNumber)) {
			defaultClient = new Client(roomNumber);
			return true;
		}else return false;
	}
	public boolean sendDesiredTemperatureAndWind(double temperature, Wind wind) {
		boolean tmp=socketConn.sendDesiredTemperatureAndWind(temperature, wind);
		if (temperature>defaultClient.getTemperature()) socketConn.sendStopWind();
		return tmp;
	}
	public void decode() {
		socketConn.decode(defaultClient);
	}
	public void registerObserver(Observer observer)
    {
		defaultClient.registerObserver(observer);
    }
	public int getState() {
		return state;
	}
	public void RoomTemperature() {
		if (defaultClient.getState()==State.RUNNING){
			if (defaultClient.getWind()==Wind.LOW)
				defaultClient.setTemperature(defaultClient.getTemperature()-0.8);
			else if (defaultClient.getWind()==Wind.MEDIUM)
				defaultClient.setTemperature(defaultClient.getTemperature()-1.0);
			else
				defaultClient.setTemperature(defaultClient.getTemperature()-1.2);
		}
	}
}
