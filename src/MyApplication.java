import com.geostar.smackandroid.config.Configuration;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		Configuration.getInstance(this);
		super.onCreate();
	}
	
	

}
