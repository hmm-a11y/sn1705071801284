package cn.edu.sdwu.android02.home.sn170507180128;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class homeworkActivity3 extends AppCompatActivity {
    private TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private CameraDevice.StateCallback stateCallback;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest previewRequest;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;

    public void takephoto(View view){
        if (cameraDevice!=null){
            try {
                CaptureRequest.Builder builder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                builder.addTarget(imageReader.getSurface());
                cameraCaptureSession.stopRepeating();
                cameraCaptureSession.capture(builder.build(),new CameraCaptureSession.CaptureCallback(){
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        try {
                            session.setRepeatingRequest(previewRequest,null,null);
                        }catch (Exception e){
                            Log.e(homeworkActivity3.class.toString(),e.toString());
                        }
                    }
                },null);
            }catch (Exception e){
                Log.e(homeworkActivity3.class.toString(),e.toString());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_homework_3);
        //检查相机的使用权限
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int result=checkSelfPermission(Manifest.permission.CAMERA);
            if (result== PackageManager.PERMISSION_GRANTED){

            }else {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},104);
            }
            //实例化stateCallback,当打开相机时执行stateCallBack
            stateCallback= new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    //摄像头打开后，执行本方法，可以获取CamereDevice对象
                    homeworkActivity3.this.cameraDevice=cameraDevice;
                    //准备预览时使用的组件
                    Surface surface=new Surface(surfaceTexture);
                    //创建一个捕捉请求
                    try {
                        captureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        captureRequestBuilder.addTarget(surface);
                        imageReader=ImageReader.newInstance(1024,768, ImageFormat.JPEG,2);
                        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                            @Override
                            public void onImageAvailable(ImageReader imageReader) {
                                Image image=imageReader.acquireNextImage();
                                ByteBuffer buffer=image.getPlanes()[0].getBuffer();
                                byte[] bytes=new byte[buffer.remaining()];
                                buffer.get(bytes);
                                //写文件
                                File file=new File(Environment.getExternalStorageDirectory(),"abcd.jpg");
                                FileOutputStream outputStream=null;
                                try {
                                    outputStream=new FileOutputStream(file);
                                    outputStream.write(bytes);
                                    Toast.makeText(homeworkActivity3.this,"save:"+file,Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    Log.e(homeworkActivity3.class.toString(),e.toString());
                                }finally {
                                    try {
                                        outputStream.flush();
                                        outputStream.close();
                                    }catch (Exception ee){
                                        Log.e(homeworkActivity3.class.toString(),ee.toString());
                                    }
                                }
                            }
                        },null);
                        cameraDevice.createCaptureSession(Arrays.asList(surface,imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                homeworkActivity3.this.cameraCaptureSession=cameraCaptureSession;
                                try {
                                    previewRequest=captureRequestBuilder.build();
                                    cameraCaptureSession.setRepeatingRequest(previewRequest,null,null);
                                }catch (Exception e){
                                    Log.e(homeworkActivity3.this.toString(),e.toString());
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {


                            }
                        },null);
                    }catch (Exception e){

                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                    homeworkActivity3.this.cameraDevice=null;
                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice,  int i) {
                }
            };
        }


    }
    private void openCamera(int width,int height){
        CameraManager cameraManager=(CameraManager)getSystemService(CAMERA_SERVICE);
        try {
            cameraManager.openCamera("0",stateCallback,null);
        }catch (Exception e){
            Log.e(homeworkActivity3.this.toString(),e.toString());
        }
    }
    private void setCameraLayout(){
        setContentView(R.layout.layout_homework_3);
        textureView=(TextureView)findViewById(R.id.hw3_tv);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                homeworkActivity3.this.surfaceTexture=surfaceTexture;
                openCamera(width,height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==104){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                setCameraLayout();
            }
        }
    }
}