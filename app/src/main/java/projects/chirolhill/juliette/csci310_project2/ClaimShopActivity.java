package projects.chirolhill.juliette.csci310_project2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import projects.chirolhill.juliette.csci310_project2.model.Shop;

public class ClaimShopActivity extends AppCompatActivity {
    private TextView textClaimHeader;
    private Button btnCapture;
    private ImageView imgVerifDocs;
    private Button btnSubmit;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_shop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textClaimHeader = findViewById(R.id.textClaimHeader);
        btnCapture = findViewById(R.id.btnCapture);
        imgVerifDocs = findViewById(R.id.imageVerifDocs);
        btnSubmit = findViewById(R.id.btnSubmit);

        Intent i = getIntent();
        textClaimHeader.setText(R.string.claim + " " + i.getStringExtra(Shop.PREF_BASIC_SHOP_NAME));

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    // create intent to use another app to take picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            btnCapture.setVisibility(View.GONE);
            imgVerifDocs.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgVerifDocs.setImageBitmap(imageBitmap);
        }
    }
}
