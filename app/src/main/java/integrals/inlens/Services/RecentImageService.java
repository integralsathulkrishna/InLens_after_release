 package integrals.inlens.Services;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import integrals.inlens.Helper.BitmapConfig;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.MainActivity;
import integrals.inlens.Models.SituationModel;
import integrals.inlens.R;

 public class RecentImageService extends Service {
     private Handler     handler;
     private Runnable    runnable;
     final   String[][]  Projection = new String[1][1];
     final   String[]    ImageLocation = {null};
     final   File[]      file = {null};
     private Boolean     Index =     false;
     final   String[]    CurrentImage = {null};
     final   SharedPreferences[] sharedPreferences1 = {null};
     final   File[]       file1 = {null};
     final   Bitmap[]     bitmap1 ={null};
     private File         pictureFile,pictureFile1;
     private Uri          ImageUri;
     private RemoteViews  remoteViews;
     private String       CommunityID;
     private int          UploadingIntegerID;
     private int          Record;
     private String       UPLOAD_STATUS;
     private File         OriginalImageFile;
     private File         ImageFile,ThumbnailFile;
     private String       OriginalImageName;
     private Uri          DownloadUri,ThumbImageUri,DownloadThumbUri;
     private Calendar     calendar;
     private DatabaseReference ComNotyRef;
     private String MyUserID;
     private NotificationCompat.Builder noty  ;
     private static int notyid =808679;
     long[] pattern = {};
     String AlbumTime;
     private int RecentImage=0;
     private BitmapConfig bitmapConfig;
     private int COMPRESSION_WIDTH=400;
     private int COMPRESSION_HEIGHT=400;
     private String AlbumExpiry="";
     /*
     private String       sowner,stime,stitle,sKey,sTime;
     private DatabaseReference databaseReference;
     private List<SituationModel> SituationList;
     private List<String>         SituationIDList;
     private String PhotoIntervel=null;
     private String SituationID;
     */
     public RecentImageService(Context applicationContext) {
         super();
     }

     public RecentImageService()
     {

     }



     @Override
         public void onCreate() {
         super.onCreate();



         handler=new Handler();
         remoteViews=new RemoteViews(getPackageName(),R.layout.notification_layout);

         if(FirebaseAuth.getInstance().getCurrentUser()!=null)
         {
             MyUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
             ComNotyRef = FirebaseDatabase.getInstance().getReference().child("ComNoty").child(MyUserID);
         }

         noty = new NotificationCompat.Builder(this);
         noty.setAutoCancel(true);
         noty.setSmallIcon(R.drawable.inlens_notification);
         noty.setContentTitle("New Situation Detected");
         Intent intent = new Intent(this, MainActivity.class);
         intent.putExtra("code",1);
         PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
         noty.setContentIntent(pendingIntent);
         noty.setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/impulse"));
         //Added for perfect uploading;
         UploadDatabaseHelper uploadDatabaseHelper=new UploadDatabaseHelper(getApplicationContext(),"",null,1);
         CurrentDatabase      currentDatabase     =new CurrentDatabase(getApplicationContext(),"",null,1);
         AlbumExpiry=currentDatabase.GetAlbumExpiry();
         uploadDatabaseHelper.UpdateUploadStatus(currentDatabase.GetUploadingTargetColumn(),"NOT_UPLOADED");
         currentDatabase.close();
         uploadDatabaseHelper.close();
         //
         Toast.makeText(getApplicationContext(),"InLens  Service created.",Toast.LENGTH_SHORT).show();
    }

         @Override
         public int onStartCommand(Intent intent, int flags, int startId) {
             runnable=new Runnable() {
                 @Override
                 public void run() {


                     if (CheckAlbumActive() <=0) {

                         Projection[0] = new String[]{
                                 MediaStore.Images.ImageColumns._ID,
                                 MediaStore.Images.ImageColumns.DATA,
                                 MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                 MediaStore.Images.ImageColumns.DATE_MODIFIED,
                                 MediaStore.Images.ImageColumns.MIME_TYPE

                         };


                         Cursor cursor = getApplicationContext().getContentResolver().
                                 query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                         Projection[0], null, null,
                                         MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC");

                            //Cursor Control
                         try {

                             if (cursor.moveToFirst()) {
                                 ImageLocation[0] = cursor.getString(1);
                                 file[0] = new File(ImageLocation[0]);
                                 file1[0] = new File(ImageLocation[0]);

                                 if (file[0].exists()) {
                                     String CurrentImageX = "KKKK";
                                     sharedPreferences1[0] = getApplicationContext().getSharedPreferences("PhotoUpdate.pref", Context.MODE_PRIVATE);
                                     CurrentImage[0] = sharedPreferences1[0].getString("CurrentImage::", CurrentImageX);
                                     if (ImageLocation[0].contentEquals(CurrentImage[0])) {
                                         //Do not do anything,if the current image matches the image in SharedPreference
                                     } else if ((!ImageLocation[0].contains("/WhatsApp/")) && !ImageLocation[0].contains("/Screenshots/") && !ImageLocation[0].contains(CurrentImage[0])) {
                                         calendar = Calendar.getInstance();
                                         String TimeTaken = calendar.get(Calendar.YEAR) + "-"
                                                 + calendar.get(Calendar.MONTH) + "-"
                                                 + calendar.get(Calendar.DAY_OF_MONTH) + "T"
                                                 + calendar.get(Calendar.HOUR_OF_DAY) + "-"
                                                 + calendar.get(Calendar.MINUTE) + "-"
                                                 + calendar.get(Calendar.SECOND);

                                         RecentImageDatabase recentImageDatabase = new RecentImageDatabase(getApplicationContext(), "", null, 1);
                                         recentImageDatabase.InsertUploadValues(
                                                 ImageLocation[0].toString(),
                                                 "NULL",
                                                 "NULL",
                                                 "NULL",
                                                 TimeTaken,
                                                 "NULL",
                                                 "NULL",
                                                 "NULL",
                                                 "NULL",
                                                 "NULL"
                                         );
                                         //try {
                                         //  bitmap1[0] = new Compressor(getApplicationContext())
                                         //        .setMaxHeight(100)
                                         //      .setMaxWidth(125)
                                         //    .setQuality(100)

                                         //  .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                         //  .compressToBitmap(file1[0]);
                                         // } catch (IOException e) {
                                         //    e.printStackTrace();
                                         //}
                                         CreateNotification();
                                         SharedPreferences.Editor e = sharedPreferences1[0].edit();
                                         e.putString("CurrentImage::", ImageLocation[0]);
                                         e.apply();
                                         CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                                         int Value = currentDatabase.GetRecentTotal();
                                         currentDatabase.ResetResentTotal((Value + 1));
                                         currentDatabase.close();
                                         cursor.close();

                                     }

                                 } else {
                                     Projection[0] = null;

                                 }

                             }
                         }
                         //Error Fix 7
                         catch (NullPointerException e) {
                             e.printStackTrace();
                         }

                         //Situation Operation
                         SituationOperation();
                         //Upload Operation
                         UploadOperation();


                         handler.postDelayed(this, 2500);


                     } else if (CheckAlbumActive() > 0) {
                         CurrentDatabase currentDatabase1 = new CurrentDatabase(getApplicationContext(), "", null, 1);
                         if (currentDatabase1.GetUploadingTargetColumn() >= currentDatabase1.GetUploadingTotal()) {
                             QuitCloudAlbum(0);
                         } else {
                             QuitCloudAlbum(1);
                         }
                     }

            }
        };
        handler.postDelayed(runnable,2000);

        return START_STICKY;

    }

     private void QuitCloudAlbum(int XYZ) {

             if(XYZ==1){
             //Do not Quit Cloud-Album because photos are being uploaded
             }else {
             final SharedPreferences sharedPreferences4=getSharedPreferences("Owner.pref",MODE_PRIVATE);
             CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),"",null,1);
             final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference()
                     .child("Communities")
                     .child(currentDatabase.GetLiveCommunityID())
                     .child("ActiveIndex");
                     if (sharedPreferences4.getBoolean("ThisOwner::", false) == true) {
                         databaseReference.setValue("F")
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                                         currentDatabase.DeleteDatabase();
                                         RecentImageDatabase recentImageDatabase = new RecentImageDatabase(getApplicationContext(), "", null, 1);
                                         recentImageDatabase.DeleteDatabase();
                                         UploadDatabaseHelper uploadDatabaseHelper = new UploadDatabaseHelper(getApplicationContext(), "", null, 1);
                                         uploadDatabaseHelper.DeleteDatabase();
                                         SharedPreferences sharedPreferencesC = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                                         SharedPreferences.Editor editorC = sharedPreferencesC.edit();
                                         editorC.putBoolean("UsingCommunity::", false);
                                         editorC.commit();
                                         stopService(new Intent(getApplicationContext(), RecentImageService.class));
                                         JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                                         jobScheduler.cancel(7907);
                                         NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                         notificationManager.cancelAll();
                                         Toast.makeText(getApplicationContext(), "Successfully left from the current Cloud-Album", Toast.LENGTH_SHORT).show();

                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                             }
                         });
                     }else {
                         CurrentDatabase currentDatabase1 = new CurrentDatabase(getApplicationContext(), "", null, 1);
                         currentDatabase1.DeleteDatabase();
                         RecentImageDatabase recentImageDatabase = new RecentImageDatabase(getApplicationContext(), "", null, 1);
                         recentImageDatabase.DeleteDatabase();
                         UploadDatabaseHelper uploadDatabaseHelper = new UploadDatabaseHelper(getApplicationContext(), "", null, 1);
                         uploadDatabaseHelper.DeleteDatabase();
                         SharedPreferences sharedPreferencesC = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                         SharedPreferences.Editor editorC = sharedPreferencesC.edit();
                         editorC.putBoolean("UsingCommunity::", false);
                         editorC.commit();
                         stopService(new Intent(getApplicationContext(), RecentImageService.class));
                         JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                         jobScheduler.cancel(7907);
                         NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                         notificationManager.cancelAll();
                         Toast.makeText(getApplicationContext(), "Successfully left from the current Cloud-Album", Toast.LENGTH_SHORT).show(); }
                 }
               }

















     private int CheckAlbumActive() {
         Calendar calendarW=Calendar.getInstance();
         AlbumTime = calendarW.get(Calendar.DAY_OF_MONTH) + "-" + (calendarW.get(Calendar.MONTH)+1) + "-" + calendarW.get(Calendar.YEAR);
         SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
         Date d1 = null,d2=null;
         try {

             d1=dateFormat.parse(AlbumTime);
             d2=dateFormat.parse(AlbumExpiry);

         } catch (ParseException e) {
             e.printStackTrace();
         }

         return d1.compareTo(d2);
     }

     private void SituationOperation() {

         ComNotyRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                 for(DataSnapshot snapshot : dataSnapshot.getChildren())
                 {   try {
                     String notificationid = snapshot.getKey();
                     String sitname = snapshot.child("name").getValue().toString();
                     String ownername = snapshot.child("ownername").getValue().toString();
                     noty.setContentText("  New situation created , "+sitname+". Check it out now.");
                     NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                     manager.notify(notyid,noty.build());
                     ComNotyRef.child(notificationid).removeValue();
                 }catch (NullPointerException e){
                     e.printStackTrace();
                 }

                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
     }

     private void UploadOperation() {
         CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),"",null,1);
         CommunityID=currentDatabase.GetLiveCommunityID();
         UploadingIntegerID=currentDatabase.GetUploadingTargetColumn();
         Record=currentDatabase.GetUploadingTotal();
         currentDatabase.close();


         UploadDatabaseHelper uploadDatabaseHelper= new UploadDatabaseHelper(getApplicationContext(),"",null,1);
         UPLOAD_STATUS=uploadDatabaseHelper.GetUploadStatus(UploadingIntegerID);
         if((UploadingIntegerID<=Record)) {
             try {
                 if(UPLOAD_STATUS.contentEquals("NOT_UPLOADED")) {

                     StartUpload(UploadingIntegerID,Record);

                 }

             }catch (NullPointerException e){
                 e.printStackTrace();
             }

         }






     }


     private void StartUpload(final int uploadID,final int Record) {
         Toast.makeText(getApplicationContext(), "Uploading :: " + uploadID + "/" + Record, Toast.LENGTH_SHORT).show();
         final DatabaseReference
                 InUserReference,
                 PostDatabaseReference;
         StorageReference PostStorageReference;
         FirebaseAuth InAuthentication;
         final FirebaseUser InUser;
         Bitmap bitmap = null;
         Bitmap ThumbBitmap = null;
         InAuthentication = FirebaseAuth.getInstance();
         InUser = InAuthentication.getCurrentUser();
         PostStorageReference = FirebaseStorage.getInstance().getReference();

         final UploadDatabaseHelper uploadDatabaseHelper = new UploadDatabaseHelper(getApplicationContext(), "", null, 1);
         uploadDatabaseHelper.UpdateUploadStatus(uploadID, "UPLOADING");
         ImageFile = new File(uploadDatabaseHelper.GetPhotoUri(uploadID));
         OriginalImageFile = new File(uploadDatabaseHelper.GetPhotoUri(uploadID));
         ThumbnailFile = new File(uploadDatabaseHelper.GetPhotoUri(uploadID));

         // GetSituationID(uploadDatabaseHelper.GetTimeTaken(uploadID));
         // Compressing the image
         try {
             Bitmap config=BitmapFactory.decodeFile(ThumbnailFile.getAbsolutePath());
             BitmapConfig bitmapConfig=new BitmapConfig(config);
             if(bitmapConfig.GetPhotoType()==bitmapConfig.POTRAIT){
                 COMPRESSION_HEIGHT=700;
                 COMPRESSION_WIDTH=470;
                 Toast.makeText(getApplicationContext(),"Potrait",Toast.LENGTH_SHORT).show();
                 }
                 else if(bitmapConfig.GetPhotoType()==bitmapConfig.LANDSCAPE) {
                 COMPRESSION_HEIGHT=470;
                 COMPRESSION_WIDTH=700;
                 Toast.makeText(getApplicationContext(),"Landscape",Toast.LENGTH_SHORT).show();
             }

         }catch (NullPointerException e){
             e.printStackTrace();
         }

         try {
             bitmap = new Compressor(getApplicationContext())
                     .setMaxHeight(100)
                     .setMaxWidth(100)
                     .setQuality(100)
                     .setCompressFormat(Bitmap.CompressFormat.JPEG)
                     .compressToBitmap(ImageFile);
         } catch (IOException e) {
             e.printStackTrace();
         }

         try {
             ThumbBitmap = new Compressor(getApplicationContext())
                     .setMaxHeight(COMPRESSION_HEIGHT)
                     .setMaxWidth(COMPRESSION_WIDTH)
                     .setQuality(100)
                     .setCompressFormat(Bitmap.CompressFormat.JPEG)
                     .compressToBitmap(ThumbnailFile);
         } catch (IOException e) {
             e.printStackTrace();
         }

         //Saving that compressed file to storage
         storeImage(bitmap);
         storeThumbImage(ThumbBitmap);


         //Accesing the title value
         final String TitleValue = uploadDatabaseHelper.GetTextCaption(uploadID);
         PostDatabaseReference = FirebaseDatabase.getInstance().getReference()
                 .child("Communities")
                 .child(CommunityID)
                 .child("BlogPosts");
         InUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(InUser.getUid());
         final StorageReference FilePath = PostStorageReference.child("CommunityPosts").child(ImageUri.getLastPathSegment());
         Uri OriginalImageUri = Uri.fromFile(OriginalImageFile);
         //Error Fix  1
        // final StorageReference OriginalFilePath = PostStorageReference.child("OriginalImage").child(OriginalImageUri.getLastPathSegment() + System.currentTimeMillis());
         //Error Fix 1
         //Error Fix 6
         final StorageReference ThumbNailImage = PostStorageReference.child("OriginalImage_thumb").child(OriginalImageUri.getLastPathSegment() + System.currentTimeMillis());
         //Error Fix 6



           /*OriginalFilePath.putFile(OriginalImageUri)
                          .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                  OriginalImageName=taskSnapshot.getDownloadUrl().toString();
                                  }
                              }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                */

         ThumbNailImage.putFile(ThumbImageUri)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         DownloadThumbUri = taskSnapshot.getDownloadUrl();
                     }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 //OriginalFilePath.delete();
                 ThumbNailImage.delete();
                 uploadDatabaseHelper.UpdateUploadStatus(uploadID, "NOT_UPLOADED");
                 CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                 int Value = currentDatabase.GetUploadingTargetColumn();
                 currentDatabase.ResetUploadTargetColumn((Value));
                 currentDatabase.close();

             }
         }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                 if (task.isSuccessful()) {
                     FilePath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             DownloadUri = taskSnapshot.getDownloadUrl();
                         }
                     }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                             if (task.isSuccessful()) {
                                 final DatabaseReference NewPost = PostDatabaseReference.push();
                                 InUserReference.addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(DataSnapshot dataSnapshot) {

                                         NewPost.child("BlogTitle").setValue(TitleValue);
                                         NewPost.child("BlogDescription").setValue("NULLX");
                                         NewPost.child("Image").setValue((DownloadUri).toString());
                                         NewPost.child("ImageThumb").setValue((DownloadThumbUri).toString());
                                         NewPost.child("User_ID").setValue(InUser.getUid());
                                         NewPost.child("OriginalImageName").setValue(OriginalImageName);
                                         NewPost.child("Location").setValue(uploadDatabaseHelper.GetLocationDetails(uploadID));
                                         NewPost.child("TimeTaken").setValue(uploadDatabaseHelper.GetTimeTaken(uploadID));
                                         NewPost.child("WeatherDetails").setValue(uploadDatabaseHelper.GetWeatherDetails(uploadID));
                                         NewPost.child("AudioCaption").setValue("NULLX");
                                         NewPost.child("PostedByProfilePic").setValue(dataSnapshot.child("Profile_picture").getValue());
                                         NewPost.child("UserName").setValue(dataSnapshot.child("Name").getValue());
                                         SharedPreferences sh = getApplicationContext().getSharedPreferences("UploadTargetColumn.pref", MODE_PRIVATE);
                                         SharedPreferences.Editor E = sh.edit();
                                         E.putInt("Upload:", (uploadID + 1));
                                         E.commit();
                                         try {
                                             uploadDatabaseHelper.UpdateUploadStatus(uploadID, "UPLOADED");

                                         } catch (SQLiteReadOnlyDatabaseException e) {
                                             e.printStackTrace();
                                         }
                                         pictureFile.delete();
                                         pictureFile1.delete();
                                         CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                                         int Value = currentDatabase.GetUploadingTargetColumn();
                                         currentDatabase.ResetUploadTargetColumn((Value + 1));
                                         currentDatabase.close();

                                     }

                                     @Override
                                     public void onCancelled(DatabaseError databaseError) {
                                        // OriginalFilePath.delete();
                                         ThumbNailImage.delete();
                                         FilePath.delete();
                                         uploadDatabaseHelper.UpdateUploadStatus(uploadID, "NOT_UPLOADED");
                                         CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                                         int Value = currentDatabase.GetUploadingTargetColumn();
                                         currentDatabase.ResetUploadTargetColumn((Value));
                                         currentDatabase.close();
                                     }
                                 });


                             }
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             //OriginalFilePath.delete();
                             ThumbNailImage.delete();
                             FilePath.delete();
                             uploadDatabaseHelper.UpdateUploadStatus(uploadID, "NOT_UPLOADED");
                             CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                             int Value = currentDatabase.GetUploadingTargetColumn();
                             currentDatabase.ResetUploadTargetColumn((Value));
                             currentDatabase.close();

                         }
                     });


                 }
             }
         });























/*


                  }
                 }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
             OriginalFilePath.delete();
             uploadDatabaseHelper.UpdateUploadStatus(uploadID,"NOT_UPLOADED");
                 CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),"",null,1);
                 int Value=currentDatabase.GetUploadingTargetColumn();
                 currentDatabase.ResetUploadTargetColumn((Value));
                 currentDatabase.close();


             }
         });


     }

*/
     }
     private void storeImage(Bitmap image) {
         pictureFile = getOutputMediaFile();

         if (pictureFile == null)
         {
             Toast.makeText(getApplicationContext(),"Unable to create file " +
                     ",Please check Storage Permission",Toast.LENGTH_SHORT).show();
             return;
         }
         try
         {
             FileOutputStream fos = new FileOutputStream(pictureFile);
             image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
             ImageUri=Uri.fromFile(pictureFile);
             fos.close();
         } catch (FileNotFoundException e) {
             Toast.makeText(getApplicationContext(),"File not found",Toast.LENGTH_SHORT).show();
         } catch (IOException e) {
             Toast.makeText(getApplicationContext(),"Error accessing file.",Toast.LENGTH_SHORT).show();
         }
     }

     private void storeThumbImage(Bitmap image) {
         pictureFile1 = getOutputMediaFile();

         if (pictureFile1 == null)
         {
             Toast.makeText(getApplicationContext(),"Unable to create file " +
                     ",Please check Storage Permission",Toast.LENGTH_SHORT).show();
             return;
         }
         try
         {
             FileOutputStream fos = new FileOutputStream(pictureFile1);
             image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
             ThumbImageUri=Uri.fromFile(pictureFile1);
             fos.close();
         } catch (FileNotFoundException e) {
             Toast.makeText(getApplicationContext(),"File not found",Toast.LENGTH_SHORT).show();
         } catch (IOException e) {
             Toast.makeText(getApplicationContext(),"Error accessing file.",Toast.LENGTH_SHORT).show();
         }
     }

     /** Create a File for saving an image or video */
     private  File getOutputMediaFile(){
         // To be safe, you should check that the SDCard is mounted
         // using Environment.getExternalStorageState() before doing this.
         File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                 + "/Android/data/"
                 + getApplicationContext().getPackageName()
                 + "/Files");

         // This location works best if you want the created images to be shared
         // between applications and persist after your app has been uninstalled.
         // Create the storage directory if it does not exist
         if (! mediaStorageDir.exists()){
             if (! mediaStorageDir.mkdirs()){
                 return null;
             }
         }
         // Create a media file name
         File mediaFile;
         String mImageName="MI_"+ System.currentTimeMillis() +".jpg";
         mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
         return mediaFile;
     }













     private void CreateNotification() {
       RecentImage++;
       //  remoteViews.setImageViewBitmap(R.id.UploadImageViewNotification, bitmap1[0]);
           remoteViews.setTextViewText(R.id.recentImageTextView,RecentImage +" recent image(s) updated for album.\n Tap to upload each");
         NotificationManager notificationManager =
                         (NotificationManager)
                         getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
         Intent upload_intent = new Intent("ADD_FOR_UPLOAD_INLENS");
         Intent attach_intent = new Intent("ATTACH_ACTIVITY_INLENS");
         Intent upload_activity_intent = new Intent("RECENT_IMAGES_GRID_INLENS");
         Intent intent= new Intent(getApplicationContext(), integrals.inlens.GridView.MainActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
         PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

         PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 9388,upload_intent , 0);
         PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(), 8086,  attach_intent, 0);
         PendingIntent pendingIntent3 = PendingIntent.getBroadcast(getApplicationContext(), 1428,  upload_activity_intent, 0);

         remoteViews.setOnClickPendingIntent(R.id.AddForUpload,pendingIntent1);
         remoteViews.setOnClickPendingIntent(R.id.GoToUploadTask, pendingIntent2);
         remoteViews.setOnClickPendingIntent(R.id.GotoUploadActivity, pendingIntent3);


         NotificationCompat.Builder builder =
                 (NotificationCompat.Builder)
                         new NotificationCompat.Builder(getApplicationContext())
                         .setDefaults(Notification.DEFAULT_ALL)
                         .setOnlyAlertOnce(true)
                         .setContent(remoteViews)
                         .setWhen(System.currentTimeMillis())

                 .setSmallIcon(R.drawable.inlens_logo_m)
                 .setPriority(Notification.PRIORITY_MAX)
                 ;
         builder.setContentIntent(pendingIntent);
         notificationManager.notify(0, builder.build());

         }

         @Override
         public void onDestroy() {
            super.onDestroy();
            handler.removeMessages(0);
            Toast.makeText(getApplicationContext(),"InLens Service destroyed.",Toast.LENGTH_SHORT).show();
        }


/*
     private void GetSituationID(final String PhotoDateTime){
         PhotoIntervel=PhotoDateTime;
         databaseReference = FirebaseDatabase.getInstance().getReference().child("Communities")
                 .child(CommunityID).child("Situations");
         SituationList =   new ArrayList<>();
         SituationIDList = new ArrayList<>();



         }
*/





/*
      private Boolean IntervelBefore(String PhotoDate,String SituationDate){
         Boolean Result=false;
          try{
              SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
              Date dt_1 = objSDF.parse(PhotoDate);
              Date dt_2 = objSDF.parse(SituationDate);


              if (dt_1.compareTo(dt_2) > 0) {
                   Result=true;
              } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
              else if (dt_1.compareTo(dt_2) < 0) {
                   Result=false;
              } // compareTo method returns the value less than 0 if this Date is before the Date argument;
              else if (dt_1.compareTo(dt_2) == 0) {
                   Result=true;
                  } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
              else {
                    Result=false;
                 }
          }
          catch (ParseException e){
              Toast.makeText(getApplicationContext(),"Parse Exception",Toast.LENGTH_SHORT).show();
              e.printStackTrace();
          }

          return Result;
      }
*/
         @Nullable
         @Override
         public IBinder onBind(Intent intent) {
        return null;
    }
/*

     private class CreatingModel extends AsyncTask<String, Void, String> {

         @Override
         protected String doInBackground(String... params) {

             databaseReference.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     for(DataSnapshot snapshot : dataSnapshot.getChildren())
                     { if(snapshot.hasChildren())
                         {
                             String SituationId = snapshot.getKey();


                             if(snapshot.hasChild("owner"))
                             {
                                 String ownerid = snapshot.child("owner").getValue().toString();
                                 sowner = ownerid;
                             }

                             if(snapshot.hasChild("time"))
                             {
                                 String time = snapshot.child("time").getValue().toString();
                                 stime = time;
                             }

                             if(snapshot.hasChild("name"))
                             {
                                 String title = snapshot.child("name").getValue().toString();
                                 stitle = title;

                                 if(snapshot.hasChild("SituationKey"))
                                 {
                                     String SituationKey = snapshot.child("SituationKey").getValue().toString();
                                     sKey = SituationKey;

                                 }
                                 if(snapshot.hasChild("SituationKey"))
                                 {
                                     String SituationKey = snapshot.child("SituationKey").getValue().toString();
                                     sKey = SituationKey;

                                 }

                                 if(snapshot.hasChild("SituationTime"))
                                 {
                                     String SituationTime = snapshot.child("SituationTime").getValue().toString();
                                     sTime = SituationTime;

                                 }


                             }

                             if(!SituationIDList.contains(SituationId))
                             {
                                 SituationIDList.add(SituationId);
                                 SituationModel model = new SituationModel(sowner,stime,stitle,sKey,sTime);
                                 SituationList.add(model);
                             }

                         }

                     }

                 }



                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });

             return "Executed";
         }

         @Override
         protected void onPostExecute(String result) {
             String SituationX="";
             try {
                 for (int i=0;!(SituationList.isEmpty());i++){
                        SituationX+=SituationList.get(i).getSituationTime();
                 }

             }catch (IndexOutOfBoundsException e){
                 e.printStackTrace();
             }
                Toast.makeText(getApplicationContext(),SituationX,Toast.LENGTH_SHORT).show();
             }

         @Override
         protected void onPreExecute() {

         }

         @Override
         protected void onProgressUpdate(Void... values) {

         }

     }




*/








 }
