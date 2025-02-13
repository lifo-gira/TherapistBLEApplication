package com.example.therapistbluelock;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageClassification extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private String mCurrentPhotoPath;

    private TextView uploadFileName;
    private Uri fileUri;

    LinearLayout horizontal_layout;

    public static String currentPhotoPath;
    public static Uri photoUri;
    private ImageView my_image;

    LinearLayout upload_button;

    TextView therapistname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_classification);
        hideSystemUI();

        String itemType = getIntent().getStringExtra("itemType");
        String itemTitle = getIntent().getStringExtra("itemTitle");

        therapistname = findViewById(R.id.therapistname);
        therapistname.setText(MainActivity.therapistname);

        my_image = findViewById(R.id.my_image);
        my_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    launchCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        upload_button = findViewById(R.id.upload_button);
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        TextView assessmentTextView = findViewById(R.id.assessment_text_view);
        if (itemType != null) {
            assessmentTextView.setText(itemType); // Set the text dynamically
        }

        // Set up the OnClickListener for the button
        LinearLayout analyzeButton = findViewById(R.id.analyze_button);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate back to AssessmentList
                Intent intent = new Intent(ImageClassification.this, DetailFrag_5.class);

                // Pass the updated values back
                intent.putExtra("itemTitle", itemTitle);
                intent.putExtra("itemStatus", "Completed");
                intent.putExtra("itemColor", Color.GREEN);

                // Start the AssessmentList activity with the updated data
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }


    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a file to save the image
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(this, "Error while creating file", Toast.LENGTH_SHORT).show();
        }

        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.therapistbluelock.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        // Save the file path for later use
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Image selected from gallery
                Uri imageUri = data.getData();
                handleImageFromUri(imageUri);  // Handle image selection from the gallery
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Image captured from camera
                if (data != null && data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        // Handle the captured image
                        my_image.setImageBitmap(bitmap);
                        detectPose(bitmap);
                    }
                }

                // Load the image from the saved file path (for captured image)
                if (mCurrentPhotoPath != null) {
                    File imgFile = new File(mCurrentPhotoPath);
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        my_image.setImageBitmap(bitmap);  // Display the captured image in the ImageView
                        detectPose(bitmap);  // Process the image if needed
                    }
                }
            }
        }
    }

    private void handleImageFromUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            Bitmap rotatedBitmap = rotateImageIfRequired(bitmap, imageUri);
            my_image.setImageBitmap(rotatedBitmap);
            detectPose(rotatedBitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap, Uri imageUri) throws IOException {
        InputStream input = getContentResolver().openInputStream(imageUri);
        ExifInterface exif = new ExifInterface(input);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        input.close();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void detectPose(Bitmap bitmap) {
        AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                .build();

        PoseDetector poseDetector = PoseDetection.getClient(options);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        poseDetector.process(image)
                .addOnSuccessListener(pose -> {
                    Bitmap resultBitmap = drawPoseOnBitmap(bitmap, pose);
                    my_image.setImageBitmap(resultBitmap);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Pose detection failed", Toast.LENGTH_SHORT).show()
                );
    }

    private Bitmap drawPoseOnBitmap(Bitmap bitmap, Pose pose) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);

        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);

        Paint paint = new Paint();
        paint.setColor(Color.RED); // Point color
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(8);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GREEN);  // Line color
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);   // Text color
        textPaint.setTextSize(70);        // Text size
        textPaint.setStyle(Paint.Style.FILL);

        Paint textPaint2 = new Paint();
        textPaint2.setColor(Color.RED);   // Text color
        textPaint2.setTextSize(70);        // Text size
        textPaint2.setStyle(Paint.Style.FILL);

        Paint textPaint3 = new Paint();
        textPaint3.setColor(Color.BLACK);   // Text color
        textPaint3.setTextSize(70);        // Text size
        textPaint3.setStyle(Paint.Style.FILL);


        drawPoint(canvas, pose, PoseLandmark.LEFT_KNEE, paint);
        drawPoint(canvas, pose, PoseLandmark.LEFT_FOOT_INDEX, paint);

        drawPoint(canvas, pose, PoseLandmark.RIGHT_KNEE, paint);
        drawPoint(canvas, pose, PoseLandmark.RIGHT_FOOT_INDEX, paint);



        if (leftKnee != null && leftAnkle != null) {

            float kneeX = leftKnee.getPosition3D().getX();
            float kneeY = leftKnee.getPosition3D().getY();
            float kneeZ = leftKnee.getPosition3D().getZ();

            float ankleX = leftAnkle.getPosition3D().getX();
            float ankleY = leftAnkle.getPosition3D().getY();
            float ankleZ = leftAnkle.getPosition3D().getZ();

            double distanceInPixels = Math.sqrt(
                    Math.pow(ankleX - kneeX, 2) +
                            Math.pow(ankleY - kneeY, 2) +
                            Math.pow(ankleZ - kneeZ, 2)
            );



//            Pixel Spacing         = REAL WORLD distance * PIXEL distance
//            REAL WORLD DISTANCE   = Pixel Width * Pixel Spacing
//            double Real_Wold_distance = 41.5;
//            double pixel_spacing =    Real_Wold_distance /distanceInPixels;
            double pixel_spacing =   0.08937773637096169;
            double distanceInCm = distanceInPixels*pixel_spacing;
            DetailFrag_5.leftleglength = distanceInCm;


            android.util.Log.d("PoseDetection", "Distance (Left Knee to Left Ankle): " + distanceInCm + ","+pixel_spacing + " cm");
            float textX = (leftKnee.getPosition().x + leftAnkle.getPosition().x) / 2;
            float textY = (leftKnee.getPosition().y + leftAnkle.getPosition().y) / 2;
            canvas.drawText(String.format("%.2f cm", distanceInCm), textX, textY - 20, textPaint);

//            Toast.makeText(this, "Leg Length: " + distanceInCm + " cm", Toast.LENGTH_LONG).show();
        }
        else
        {
            android.util.Log.d("PoseDetection", "Left Knee or Left Ankle not detected");
        }

        if (rightKnee != null && rightAnkle != null) {
            float kneeX_ = rightKnee.getPosition3D().getX();
            float kneeY_ = rightKnee.getPosition3D().getY();
            float kneeZ_ = rightKnee.getPosition3D().getZ();

            float ankleX_ = rightAnkle.getPosition3D().getX();
            float ankleY_ = rightAnkle.getPosition3D().getY();
            float ankleZ_ = rightAnkle.getPosition3D().getZ();

            double distanceInPixels_right = Math.sqrt(
                    Math.pow(ankleX_ - kneeX_, 2) +
                            Math.pow(ankleY_ - kneeY_, 2) +
                            Math.pow(ankleZ_ - kneeZ_, 2)
            );
            double pixel_spacing = 0.08937773637096169;
            double distanceInCm_right = distanceInPixels_right * pixel_spacing;
            DetailFrag_5.rightleglength = distanceInCm_right;
            float textX = (rightKnee.getPosition().x + rightAnkle.getPosition().x) / 2;
            float textY = (rightKnee.getPosition().y + rightAnkle.getPosition().y) / 2;
            canvas.drawText(String.format("%.2f cm", distanceInCm_right), textX - 340, textY - 20, textPaint);
            android.util.Log.d("PoseDetection", "Distance (right Knee to right  Ankle): " + distanceInCm_right + " cm");

        } else {
            android.util.Log.d("PoseDetection", "Left Knee or Left Ankle not detected");
        }

        // Valgus and varus
        if (rightKnee != null && rightAnkle != null && rightHip!= null && leftKnee!= null && leftAnkle!= null && leftHip != null)

        {

            float kneeX_right = rightKnee.getPosition3D().getX();
            float kneeY_right = rightKnee.getPosition3D().getY();
            float kneeZ_right = rightKnee.getPosition3D().getZ();

            float ankleX_right = rightAnkle.getPosition3D().getX();
            float ankleY_right = rightAnkle.getPosition3D().getY();
            float ankleZ_right = rightAnkle.getPosition3D().getZ();

            float HipX_right = rightHip.getPosition3D().getX();
            float HipY_right = rightHip.getPosition3D().getY();
            float HipZ_right = rightHip.getPosition3D().getZ();

            float kneeX_left = leftKnee.getPosition3D().getX();
            float kneeY_left = leftKnee.getPosition3D().getY();
            float kneeZ_left = leftKnee.getPosition3D().getZ();

            float ankleX_left = leftAnkle.getPosition3D().getX();
            float ankleY_left = leftAnkle.getPosition3D().getY();
            float ankleZ_left = leftAnkle.getPosition3D().getZ();

            float HipX_left = leftHip.getPosition3D().getX();
            float HipY_left = leftHip.getPosition3D().getY();
            float HipZ_left = leftHip.getPosition3D().getZ();

            double right_angle = calculateAngle(HipX_right,HipY_right,kneeX_right,kneeY_right,ankleX_right,ankleY_right);
            System.out.println(right_angle);
            double left_angle = calculateAngle(HipX_left,HipY_left,kneeX_left,kneeY_left,ankleX_left,ankleY_left);
            System.out.println(left_angle);

            if (left_angle > 185 || right_angle < 175) {
                float a = 80f;
                float b = 1490f;
                double an = 180 - left_angle;
                double an1 = 180 - right_angle;

                String test2 = "VALGUS (KNOCKED-LEG)";
                String text = "LEG ALIGNMENT";
                String text3 = "Left Angle Deviation in degrees: " + String.format("%.2f", Math.abs(an1));
                String text4 = "Right Angle Deviation in degrees: " + String.format("%.2f", Math.abs(an));

                // Draw the text on the canvas
                canvas.drawText(text, a, b + 10, textPaint3);
                canvas.drawText(test2, a, b + 80f, textPaint2);
                canvas.drawText(text3, a, b + 160f, textPaint3);
                canvas.drawText(text4, a, b + 240f, textPaint3);
            }
            else if (left_angle < 175 || right_angle > 185)
            {
                float a = 80f;
                float b = 1490f;
                String test2 = "VARUS (BOW-LEG)";
                String text = "LEG ALIGNMENT";
                double an = 180 - left_angle;
                double an1 = 180 - right_angle;

                // Draw the text on the canvas
                canvas.drawText(text, a, b + 10, textPaint2);
                canvas.drawText(test2, a, b + 80f, textPaint3);
                String text3 = "Left Angle Deviation in deg: " + String.format("%.2f", Math.abs(an1));
                String text4 = "Right Angle Deviation in deg: " + String.format("%.2f", Math.abs(an));

                canvas.drawText(text3, a, b + 160f, textPaint3);
                canvas.drawText(text4, a, b + 240f,textPaint3);
            }
            else
            {
                float a = 80f;
                float b = 1650f;
                String test2 = "NORMAL LEG ALIGNMENT";
                String text = "LEG ALIGNMENT : ";

                // Draw the text on the canvas
                canvas.drawText(text, a, b, textPaint3);
                canvas.drawText(test2, a, b + 80f, textPaint2);
            }


//            float textX = (rightKnee.getPosition().x + rightAnkle.getPosition().x) / 2;
//            float textY = (rightKnee.getPosition().y + rightAnkle.getPosition().y) / 2;
//            canvas.drawText(String.format("%.2f cm", distanceInCm_right), textX - 340, textY - 20, textPaint);
//            android.util.Log.d("PoseDetection", "Distance (right Knee to right  Ankle): " + distanceInCm_right + " cm");

        } else {
            android.util.Log.d("PoseDetection", "Left Knee or Left Ankle not detected");
        }


        drawLineBetweenPoints(canvas, pose, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_FOOT_INDEX, linePaint);
        drawLineBetweenPoints(canvas, pose, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_FOOT_INDEX, linePaint);

        return mutableBitmap;
    }

    private void drawPoint(Canvas canvas, Pose pose, int landmarkType, Paint paint) {
        PoseLandmark landmark = pose.getPoseLandmark(landmarkType);
        if (landmark != null) {
            PointF point = landmark.getPosition();
            canvas.drawCircle(point.x, point.y, 8, paint);  // Draw circle at the point
        }
    }

    public double calculateAngle(float a1, float b1, float a2, float b2, float a3, float b3) {
        // Extract coordinates
        float ax = a1;
        float ay = b1;
        float bx = a2;
        float by = b2;
        float cx = a3;
        float cy = b3;

        // Calculate the angle in radians
        double radians = Math.atan2(cy - by, cx - bx) - Math.atan2(ay - by, ax - bx);

        // Convert to degrees
        double angle = Math.abs(radians * 180.0 / Math.PI);

        // Optionally adjust the angle
        // Uncomment if needed:
        // if (angle > 180.0) {
        //     angle = 360 - angle;
        // }

        return angle;
    }

    private void drawLineBetweenPoints(Canvas canvas, Pose pose, int startLandmarkType, int endLandmarkType, Paint paint) {
        PoseLandmark startLandmark = pose.getPoseLandmark(startLandmarkType);
        PoseLandmark endLandmark = pose.getPoseLandmark(endLandmarkType);

        if (startLandmark != null && endLandmark != null) {
            PointF start = startLandmark.getPosition();
            PointF end = endLandmark.getPosition();
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
        }}


//    private void openFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/jpeg");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
//            fileUri = data.getData();
//            String fileName = getFileName(fileUri);
//            uploadFileName.setText(fileName);
//        }
//    }
//
//    @SuppressLint("Range")
//    private String getFileName(Uri uri) {
//        String result = null;
//        if ("content".equals(uri.getScheme())) {
//            ContentResolver resolver = getApplication().getContentResolver();
//            Cursor cursor = resolver.query(uri, null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//            } else {
//                // Permission denied
//            }
//        }
//    }
//
//    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK) {
//                    if (photoUri != null) {
//                        // Get the file name from the current photo path
//                        String fileName = new File(currentPhotoPath).getName();
//                        // Update the TextView with the file name
//                        uploadFileName.setText(fileName);
//                        // Set the fileUri to photoUri for consistency
//                        fileUri = photoUri;
//
//                        // Add photo to gallery
//                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        mediaScanIntent.setData(photoUri);
//                        getApplication().sendBroadcast(mediaScanIntent);
//                        Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//    );
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getApplication().getPackageManager()) != null) {  // Fixed: Use requireActivity()
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
//            }
//
//            if (photoFile != null) {
//                photoUri = FileProvider.getUriForFile(getApplication(),
//                        getApplicationContext().getPackageName() + ".provider", // Make sure to use your app's package name
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                cameraLauncher.launch(takePictureIntent);
//            }
//        }
//    }
//
//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new java.util.Date());  // Fixed: Use java.util.Date
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);  // Fixed: Use requireContext()
//        File image = File.createTempFile(
//                imageFileName,
//                ".jpg",
//                storageDir
//        );
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }


}
