package integrals.inlens.InLensJobScheduler;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import integrals.inlens.Services.RecentImageService;
import integrals.inlens.Services.SituationNotyService;

public class InLensJobScheduler extends JobService {
    private JobExcecute  inLensJobExecuter;
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters params) {
        inLensJobExecuter= new JobExcecute(){
            @Override
            protected void onPostExecute(String s) {
            jobFinished(params,false);
            }
        } ;
        inLensJobExecuter.execute();
        return true;
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        inLensJobExecuter.cancel(false);
        Toast.makeText(getApplicationContext(),"Uploading Job cancelled.",Toast.LENGTH_SHORT).show();
        return false;
    }



     private class JobExcecute extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {

            Boolean Default = false;
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
            if (sharedPreferences.getBoolean("UsingCommunity::", Default) == true) {

                RecentImageService recentImageService;
                recentImageService = new RecentImageService(getApplicationContext());
                if (!isMyServiceRunning(recentImageService.getClass())) {
                    getApplicationContext().startService(new Intent(getApplicationContext(), RecentImageService.class));
                    getApplicationContext().startService(new Intent(getApplicationContext(), SituationNotyService.class));





                }

            }

                return "Task done";

            }
    }

    //Created By Elson
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }

        return false;
    }


}
