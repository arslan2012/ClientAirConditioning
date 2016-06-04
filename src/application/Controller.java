package application;

public class Controller{
	private static Controller instance;
	private SocketConnection socketConn;
	private Client defaultClient;
	private int state;
	public boolean mutexLock;
	private Controller(int roomNumber){
		if(initClient(roomNumber)){
			(new Thread(new getClientStats())).start();
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
		mutexLock=true;
		boolean tmp=socketConn.sendDesiredTemperatureAndWind(temperature, wind);
		mutexLock=false;
		return tmp;
	}
	public void decode() {
		socketConn.decode(defaultClient);
	}
	public int getState() {
		return state;
	}
}
