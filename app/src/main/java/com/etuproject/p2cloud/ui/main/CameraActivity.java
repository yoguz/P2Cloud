package com.etuproject.p2cloud.ui.main;

import android.os.CountDownTimer;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.etuproject.p2cloud.R;
import com.google.android.material.navigation.NavigationView;

public class CameraActivity extends AppCompatActivity {

    private static ArrayList<Size> resolutions;
    private static int countAllSizeMethod;
    private static Spinner spnLocale;
    private static String camId="none";
    private DrawerLayout mDrawerLayout;
    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private Button allResButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    private int resolutId=0;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    public static int count = 0;
    public static boolean isVideoDone=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (Button) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        resolutions= new ArrayList<>(  );
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             takePicture(0,0);
            }
        });

        allResButton = (Button) findViewById(R.id.button);
        assert allResButton != null;
        allResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAllResolFoto();
            }
        });


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        if(menuItem.getTitle().toString().equals("Photo")){
                            startActivity(new Intent(CameraActivity.this,CameraActivity.class));
                        }

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();

        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
            getResolutions();
            takeAllResolFoto();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void takeAllResolFoto() {
        takePicture(resolutions.get(0).getWidth(),resolutions.get(0).getHeight());
    }

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            //Toast.makeText( CameraActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void getResolutions(){
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Resolution:");
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        CameraCharacteristics characteristics = null;
        try {
            characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Size[] jpegSizes = null;
        if (characteristics != null) {
            jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
        }
        int width = 720;
        int height = 480;
        if (jpegSizes != null && 0 < jpegSizes.length) {
            width = jpegSizes[0].getWidth();
            height = jpegSizes[0].getHeight();
        }
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        List<Size> outputSizes = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG));

        String dirname=Environment.getExternalStorageDirectory()+"/P2Cloud";
        File dir = new File(dirname);
        dir.mkdir();

        String dirname3=Environment.getExternalStorageDirectory()+"/P2Cloud/Photo";
        File dir3 = new File(dirname3);
        dir3.mkdir();

        if(resolutions.size()==0)
            for(Size s : outputSizes){
                spinnerArray.add(s.getWidth()+" x "+s.getHeight());
                Size ob = new Size(s.getWidth(),s.getHeight());
                resolutions.add(ob);
            }
        Collections.sort(resolutions, new Comparator<Size>(){

            public int compare(final Size a, final Size b) {
                return a.getWidth() * a.getHeight() - b.getWidth() * b.getHeight();
            }
        });
        Collections.reverse(resolutions);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);
    }
    protected void getCameraIds() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            final String[] cameraIdList = manager.getCameraIdList();

            List<String> spinnerArray =  new ArrayList<String>();
            spinnerArray.add("Choose camera:");


            //mkcomment


            for(String s : cameraIdList ){
                spinnerArray.add(s);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.spinner4);
            sItems.setAdapter(adapter);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    protected void takePicture(final int w, final int h) {
        String resFileName="";
        count++;
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 720;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            List<Size> outputSizes = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG));


            Spinner sItems = (Spinner) findViewById(R.id.spinner);
            Size largest = Collections.max(outputSizes, new CompareSizesByArea());
            ImageReader reader;
            //Spinner sItems = (Spinner) findViewById(R.id.spinner);

            if(w==0 && h==0) {
                if (sItems.getSelectedItem().toString() == "Resolution:") {
                    //reader  = ImageReader.newInstance( 4000, 3000, ImageFormat.JPEG, 1);
                    reader = ImageReader.newInstance( largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 1 );
                    resFileName = largest.getWidth()+"x"+largest.getHeight();
                }
                else { //Çözünürlük seçimi yapıldığı takdirde.
                    String[] sizes = sItems.getSelectedItem().toString().split( " x " );
                    System.out.println( Integer.parseInt( sizes[1] ) + "***" );
                    reader = ImageReader.newInstance( Integer.parseInt( sizes[0] ), Integer.parseInt( sizes[1] ), ImageFormat.JPEG, 1 );
                    resFileName = Integer.parseInt( sizes[0] ) +"x"+Integer.parseInt( sizes[1] );
                }
            }
            else{

                resFileName = w+"x"+h;
                reader = ImageReader.newInstance( w, h, ImageFormat.JPEG, 1 );
            }

            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));


            //final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(Arrays.asList(previewSurface, imageReader.getSurface()), stateCallback, null);
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            captureBuilder.set( CaptureRequest.JPEG_QUALITY,(byte)100 );

            //createFolder
            String dirname=Environment.getExternalStorageDirectory()+"/P2Cloud";
            File dir = new File(dirname);
            dir.mkdir();

            String dirname3=Environment.getExternalStorageDirectory()+"/P2Cloud/Photo";
            File dir3 = new File(dirname3);
            dir3.mkdir();


            final File file = new File(dir3+"/"+System.currentTimeMillis()+".jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //Toast.makeText(CameraActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    //if(w==0)
                    if((w==resolutions.get( resolutions.size()-1 ).getWidth() && h==resolutions.get( resolutions.size()-1 ).getHeight())||w==0){
                        createCameraPreview();
                        Toast.makeText(CameraActivity.this, "Saved all", Toast.LENGTH_SHORT).show();}
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            if(camId.equals( "none" )){
                cameraId = manager.getCameraIdList()[0];
            }
            else
                cameraId = camId;
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }

            spnLocale = (Spinner) findViewById(R.id.spinner4);
            List<String> spinnerArray2 =  new ArrayList<String>();
            spinnerArray2.add("Choose camera:");


            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray2);

            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnLocale.setAdapter(adapter2);
            getCameraIds();
            spnLocale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                int iCurrentSelection = spnLocale.getSelectedItemPosition();
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (iCurrentSelection != i){
                        closeCamera();
                        camId = spnLocale.getSelectedItem().toString();
                        //mkcomment burada camera durdurulup tekrar başlatılacak
                        openCamera();
                    }
                    iCurrentSelection = i;
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Failed to start camera preview because it couldn't access camera", e);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Failed to start camera preview.", e);
                }
            }
        }, 500);

    }
    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(CameraActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        textureView.setVisibility(View.VISIBLE);
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        textureView.setVisibility(View.GONE);
        stopBackgroundThread();
        super.onPause();
    }

}
