package sdu.edu.babydraw.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * ���������࣬����Ƿ�����
 * @author hzy
 *
 */
public class NetworkConnection {
	//������������
	private ConnectivityManager connManager;
	//������Ϣ����
	private NetworkInfo networkInfo;
	//������
	private Context context;

	public NetworkConnection(Context context) {
		this.context = context;
		//����������ӷ���
		this.connManager = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * ��������Ƿ�����
	 * @return
	 */
	public boolean checkNetworkStates() {
		this.networkInfo = connManager.getActiveNetworkInfo();
		boolean isConnected = false;
		if (networkInfo != null && networkInfo.isConnected())
			isConnected = true;
		return isConnected;
	}

	
	public boolean checkWifiStates() {
		boolean isWifi = false;
		State wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			isWifi = true;
		return isWifi;
	}

	
	public boolean check3GStates() {
		boolean is3G = false;
		State mobile = connManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			is3G = true;
		return is3G;
	}
}
