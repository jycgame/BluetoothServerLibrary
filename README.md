# AndroidBluetoothCommucate
Build Instruction:
1、Android Studio 3.4.2
2、Java (JRE) 1.8.x
3、Android SDK Platform 9+
4、minSdkVersion 16

Use Android Studio, Open project, Build > Make Project. This will output an AAR file. Put AAR file to Unity3d project's Assets folder, any subfolder is OKAY.

In your Unity3d scene, you should:
1、Create an gameobject named “JavaMessageReceiver”
2、Attach a script to this gameobject
3、Inside the script，write a public function，prototype：
public void ReceiveJavaMessage(string msg);
4、Inside this function，you write your code to deal with 3 kinds of message
(a) Remote(Client) connected message
(b) Receive remote data
(c) Remote shut down

Since UnityPlayer.UnitySendMessage() only takes string as parameter, all data from BluetoothServerLibrary send to Unity3d thru ReceiveJavaMessage was packed as string.
You can use Encoding.ASCII.getBytes() from string. So, here is example:

public void ReceiveJavaMessage(string msg) {
	Debug.Log ("Receive msg.");

	byte[] bytes = Encoding.ASCII.GetBytes (msg);

	if (bytes.Length != 0) {
		switch (bytes [0]) {
		case 1:
			Debug.Log ("CLIENT_CONNECTED");
			string real = msg.Substring (1);
			Debug.Log ("Content is: " + real);
			break;
		case 2:
			Debug.Log ("RECEIVE_CLIENT_DATA");
			for (int i = 1; i < bytes.Length; ++i) {
				Debug.Log ("index = " + i + ":" + bytes[i]);
			}
			break;
		case 3:
			Debug.Log ("REMOTE_SHUTDOWN");
			string realll = msg.Substring (1);
			Debug.Log ("Content is: " + realll);
			break;
		default:
			Debug.Log ("Received from java, tag = " + bytes[0]);
			break;
		}
	}
}

5、Initialise server

AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
AndroidJavaObject currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
AndroidJavaObject unityContext = currentActivity.Call<AndroidJavaObject>("getApplicationContext");

blueToothServer = new AndroidJavaClass ("com.huashi.bluetuth.BlueTuthServer");
blueToothServer.CallStatic ("init", unityContext);

6、Start Server
blueToothServer.CallStatic ("startServer");	

7、Stop Server
blueToothServer.CallStatic ("stopServer");

If you can read Chinese, reference article here:
http://www.moonsun.xyz/2019/08/01/09/663/android%e8%93%9d%e7%89%99%e5%ba%94%e7%94%a8-%e8%bf%9e%e6%8e%a5/
