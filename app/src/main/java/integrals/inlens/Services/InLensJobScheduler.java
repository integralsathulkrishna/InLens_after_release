package integrals.inlens.Services;
import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
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

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                }
                else {
                     getApplicationContext().startService(new Intent(getApplicationContext(), RecentImageService.class));
                     }


               }

                return "Task done";

            }
    }



}
