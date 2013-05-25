package sdu.edu.babydraw.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * 网络连接类，检测是否联网
 * @author hzy
 *
 */
public class NetworkConnection {
	//网络连接引用
	private ConnectivityManager connManager;
	//网络信息引用
	private NetworkInfo networkInfo;
	//上下文
	private Context context;

	public NetworkConnection(Context context) {
		this.context = context;
		//获得网络连接服务
		this.connManager = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * 检测网络是否连接
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
