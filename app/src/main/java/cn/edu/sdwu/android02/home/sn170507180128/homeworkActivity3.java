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

import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
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
    private  SurfaceTexture surfaceTexture;
    private CameraDevice.StateCallback stateCallback;
    private  CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;//请求的构造器
    private  CaptureRequest previewRequest;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;//接收相机生成的静态图像
    public void takephoto(View view){
        //点击快门生成静态图像
        if(cameraDevice!=null){
            //使用Builder,创建请求
            try {
                CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                builder.addTarget(imageReader.getSurface());
                //停止原来的请求，停止连续取景
                cameraCaptureSession.stopRepeating();
                //捕获静态图像
                cameraCaptureSession.capture(builder.build(), new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        //捕获完成后，恢复连续取景
                        try {
                            session.setRepeatingRequest(previewRequest, null, null);
                        }catch (Exception e){
                            Log.e(homeworkActivity3.class.toString(),e.toString());
                        }
                    }
                },null);
            }catch (Exception e){
                Log.e(homeworkActivity3.class.toString(),e.toString())  ;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断当前用户是否授权过
            int result = checkSelfPermission(Manifest.permission.CAMERA);
            if (result == PackageManager.PERMISSION_GRANTED) {
                setCameraLayout();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 104);
            }
        }
        //实例化stateCallback,她用来当打开相机时执行的方法（便于我们进行会话的创建）
        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice cameraDevice) {
                //摄像头打开后，执行本方法，可以获取CameraDevice对象
                homeworkActivity3.this.cameraDevice = cameraDevice;
                //准备预览时使用的组件
                Surface surface = new Surface(surfaceTexture);
                try {
                    //创建一个捕捉请求CaptureRequest
                    captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    captureRequestBuilder.addTarget(surface);//指定视频输出的位置


                    imageReader=ImageReader.newInstance(1024,768, ImageFormat.JPEG,2);
                    imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader imageReader) {
                            //当照片数据可用时，激发该方法
                            //获取捕获的照片数据
                            int REQUEST_EXTERNAL_STORAGE = 1;
                            String[] PERMISSIONS_STORAGE = {
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            };
                            int permission = ActivityCompat.checkSelfPermission(homeworkActivity3.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                            if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
                                ActivityCompat.requestPermissions(homeworkActivity3.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                            }
                            Image image= imageReader.acquireNextImage();
                            ByteBuffer buffer= image.getPlanes()[0].getBuffer();
                            byte[] bytes=new byte[buffer.remaining()];
                            buffer.get(bytes);
                            //写文件
                            File file=new File(Environment.getExternalStorageDirectory(),"abcd.jpg");
                            FileOutputStream outputStream=null;
                            try{
                                outputStream=new FileOutputStream(file);
                                outputStream.write(bytes);
                                Toast.makeText(homeworkActivity3.this,"save:"+file,Toast.LENGTH_LONG).show();
                            }catch (Exception e){
                                Log.e(homeworkActivity3.class.toString(), e.toString());
                            }finally {
                                try{
                                    outputStream.flush();
                                    outputStream.close();
                                }catch (Exception ee){
                                    Log.e(homeworkActivity3.class.toString(), ee.toString()) ;
                                }
                            }

                        }
                    },null);
                    //创建一个相机捕捉会话
                    //参数1代表后续预览或拍照使用的组件
                    //参数2代表的是监听器，创建会话完成后执行的方法
                    cameraDevice.createCaptureSession(Arrays.asList(surface,imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

                            //创建完成后，我们可以在参数中得到会话的对象
                            //开始显示相机的预览
                            homeworkActivity3.this.cameraCaptureSession = cameraCaptureSession;
                            try {
                                previewRequest = captureRequestBuilder.build();
                                //在会话中发出重复请求，进行预览(捕捉连续的图像）
                                cameraCaptureSession.setRepeatingRequest(previewRequest, null, null);
                            } catch (Exception e) {
                                Log.e(homeworkActivity3.class.toString(),e.toString());

                            }

                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                        }
                    },null);
                } catch (Exception e) {

                }

            }

            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                homeworkActivity3.this.cameraDevice=null;
            }

            @Override
            public void onError(@NonNull CameraDevice cameraDevice, int i) {

            }
        };
    }





    private void openCamera(int width,int height){
        //使用getsystemservice得到相机管理器
        CameraManager cameraManager=(CameraManager)getSystemService(CAMERA_SERVICE);
        try{
            cameraManager.openCamera("0",stateCallback,null);//0代表后置，1代表前置
        }catch (Exception e){
            Log.e(homeworkActivity3.class.toString(),e.toString());
        }

    }

    private void setCameraLayout(){
        //用户授权后加载界面
        setContentView(R.layout.layout_homework_3);
        textureView=(TextureView)findViewById(R.id.ch16_tv);
        //当Texture准备好之后，自动调用setsurfaceTextureListener的监听器
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                //当textureView可用是打开摄像头
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

        if (requestCode==104) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCameraLayout();
            }
        }
    }


}