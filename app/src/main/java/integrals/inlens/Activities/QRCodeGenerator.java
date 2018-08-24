package integrals.inlens.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.R;

public class QRCodeGenerator extends AppCompatActivity {

    private   String PhotographerID;
    ImageView QRCodeImageView;
    ActionBar actionBar;
    private String Default="No current community";
    private String CommunityID="1122333311101";
    private TextView textView;
    private Button InviteLinkButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);
        actionBar=getSupportActionBar();
        CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),"",null,1);
        CommunityID=currentDatabase.GetLiveCommunityID();
        currentDatabase.close();
        InviteLinkButton=(Button)findViewById(R.id.InviteLinkButton);
        PhotographerID=CommunityID;
        textView=(TextView)findViewById(R.id.textViewAlbumQR);
        QRCodeImageView=(ImageView)findViewById(R.id.QR_Display);
        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        try {
            BitMatrix bitMatrix=multiFormatWriter.encode(PhotographerID, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
            QRCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            actionBar.setTitle("No current album");
            QRCodeImageView.setVisibility(View.INVISIBLE);
            textView.setText("You must be in an album to generate QR code");
        }catch (NullPointerException e){
            actionBar.setTitle("No current album");
            QRCodeImageView.setVisibility(View.INVISIBLE);
            textView.setText("You must be in an album to generate QR code");

        }
        InviteLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent SharingIntent = new Intent(Intent.ACTION_SEND);
                SharingIntent.setType("text/plain");
                String CommunityPostKey=CommunityID;
                SharingIntent.putExtra(Intent.EXTRA_TEXT,"https://inlens.in/joins/"+CommunityPostKey);
                startActivity(SharingIntent);

            }
        });




    }

}

