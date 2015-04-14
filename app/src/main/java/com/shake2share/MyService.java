package com.shake2share;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

public class MyService extends Service  {

    /*rivate static final String TAG = "MyAlarmService";

    private Thread thread = null;
    private boolean running ;

 //   private File rootImage;


    public void setRunning(boolean running) {
        this.running = running;
    }

    private void startThread(){
        thread = new Thread(this);
        thread.start();
    }

    private void stopThread(){
        setRunning(false);
    }

    @Override
    public void onCreate() {
// TODO Auto-generated method stub


    }

    @Override
    public IBinder onBind(Intent intent) {
// TODO Auto-generated method stub

        return null;
    }

    @Override
    public void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        stopThread();

    }

    @Override
    public void onStart(Intent intent, int startId) {
// TODO Auto-generated method stub
        super.onStart(intent, startId);
        thread = new Thread(this);
        startThread();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void run() {
        while (true) {
            try {


                ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {

                    @Override
                    public void OnShake() {
                        Toast.makeText(getApplicationContext(), "Device shaken!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MyService.this,MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        //finish();

                    }
                });
                ShakeDetector.updateConfiguration(5,3);
                ShakeDetector.start();




              *//*
                Toast.makeText(getApplicationContext(), "Device shaken!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MyService.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);*//*

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

*/

	private static final String TAG = "MyService";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        mWakeLock.acquire();

		ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {

			@Override
			public void OnShake() {
                Toast.makeText(getApplicationContext(), "Device shaken!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MyService.this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                //finish();

			}
		});
		ShakeDetector.updateConfiguration(5,3);
		ShakeDetector.start();
	}


    

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		ShakeDetector.stop();
		ShakeDetector.destroy();
	}
}
