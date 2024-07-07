package com.firstapp.wheresmywater;
//Code sourced from https://github.com/Everyday-Programmer/Android-Camera-using-CameraX/tree/a8d962161b9adf2cd21a04f1fb70b0a653f9022d

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ImageButton captureButton;
    private PreviewView previewView;
CameraApp
    // Qualcomm HDK8450 has back cameras (despite facing forwards) so we initialize the back-facing lenses
    int cameraFacing = CameraSelector.LENS_FACING_BACK;

    // Requests permission from the user to use the camera
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        // result is the user input to giving permission to use the camera: true for yes and false for no
        public void onActivityResult(Boolean result) {
            if (result) {
                // camera starts with the back cameras
MobileApp
                startCamera(cameraFacing);
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
CameraApp
        // sets the layout of the application based on the activity_main.xml file
        setContentView(R.layout.activity_main);

        // initialize camera preview viewer and button to take photo
        previewView = findViewById(R.id.viewFinder);
        captureButton = findViewById(R.id.captureButton);

        // starts camera only if the user gives permission to use the camera, otherwise nothing happens
MobileApp
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }
    }

CameraApp
    // uses the CameraX API to set up and start the camera in the application
    public void startCamera(int cameraFacing) {
        // set aspect ratio
        // int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        int aspectRatio = AspectRatio.RATIO_16_9;

        // event listener waits for user input, ie. waits for the user to click the button
MobileApp
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

CameraApp
                // creates the camera preview with the 16:9 aspect ratio initialized above
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                // minimizes latency when capturing image
                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                // selects the intended camera (back)
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                // unbinds previous
MobileApp
                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

CameraApp
                // initializes click listener to detect user input when they click the button
                captureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // request permission to write to external storage if the app does not already have permission
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                        // takes a picture even if there is no permission to external storage, ie. it will not save to the user's device
MobileApp
                        takePicture(imageCapture);
                    }
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
CameraApp
            // handles potential exceptions
MobileApp
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(ImageCapture imageCapture) {
CameraApp
        // initialize file object where the jpg image will be saved
        final File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        // specifies where the image should be saved to
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            // runs when the image is saved successfully, ie. shows a success message at the bottom of the screen
MobileApp
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show();
                    }
                });
CameraApp
                // "starts" the camera again
MobileApp
                startCamera(cameraFacing);
            }

            @Override
CameraApp
            // runs when the image is not saved successfully, ie. shows an error message at the bottom of the screen
MobileApp
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
CameraApp
                // "starts" the camera again
MobileApp
                startCamera(cameraFacing);
            }
        });
    }

CameraApp
    // determine aspectRatio based on width and height of the camera preview (not necessary for this application since it will always be fixed)
//    private int aspectRatio(int width, int height) {
//        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
//        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
//            return AspectRatio.RATIO_4_3;
//        }
//        return AspectRatio.RATIO_16_9;
//    }
MobileApp
}