package com.example.lib_pullalive.app;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;
//执行一些小的任务逻辑，让系统觉得我们的应用总是有一些任务要处理的，从而提高不被回收的概率。
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AliveJobService extends JobService {
    private static final String TAG=AliveJobService.class.getName();
    private JobScheduler mJobScheduler;
    private static final int PULL_ALIVE=0x01;
    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PULL_ALIVE:
                    Log.d(TAG,"pull alive");
                    jobFinished((JobParameters)msg.obj,true);
                    break;
            }
        }
    };
    public static void start(Context context){
        Intent intent=new Intent(context,AliveJobService.class);
        context.startService(intent);
    }
    public AliveJobService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mJobScheduler=(JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JobInfo job=initJobInfo(startId);
        //提交了自己的Job到SystemProcess中
        if(mJobScheduler.schedule(job)<=0){
            Log.d(TAG,"AliveJobServiceFailed");
        }else{
            Log.d(TAG,"AliveJobServiceSuccess");
        }
        return START_STICKY;
    }

    //初始化JobInfo
    private JobInfo initJobInfo(int jobId) {
        JobInfo.Builder builder=new JobInfo.Builder(
                jobId,
                new ComponentName(getPackageName(),AliveJobService.class.getName()));
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            builder.setRequiresBatteryNotLow(true);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS,
                    JobInfo.BACKOFF_POLICY_LINEAR);//重试机制
        }else{
            builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
        }
        builder.setPersisted(false);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        builder.setRequiresCharging(true);


        return builder.build();
    }



    @Override
    public boolean onStartJob(JobParameters params) {
        mHandler.sendMessage(Message.obtain(mHandler,PULL_ALIVE,params));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacksAndMessages(null);
        return false;
    }
}
