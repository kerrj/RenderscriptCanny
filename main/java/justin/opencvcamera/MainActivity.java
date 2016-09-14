package justin.opencvcamera;

import android.app.ActivityManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.renderscript.Allocation;
import android.renderscript.AllocationAdapter;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptGroup;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.widget.Toast;

import com.justin.opencvcamera.ScriptC_canny;
import com.justin.opencvcamera.ScriptC_colorsplit;
import com.justin.opencvcamera.ScriptC_hysteresis;
import com.justin.opencvcamera.ScriptC_sobel;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class MainActivity extends ActionBarActivity {
    private int WIDTH=1280;
    private int HEIGHT=720;
    private short UPPER=50;
    private short LOWER=20;
    private int BLUR_RADIUS=3;
    private int RED=1;
    private int GREEN=1;
    private float GRADIENT_THRESHOLD=.0f;
    private int BLUE=1;
    private int FLASH_MODE=CaptureRequest.FLASH_MODE_OFF;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private String mCameraId;
    private Allocation mAllocationIn;
    private Allocation mAllocationOut;
    private Allocation mAllocationEdges;
    private Allocation mAllocationAccumulation;
    private RenderScript mRS;
    private ScriptC_colorsplit colorSplit;
    private ScriptIntrinsicBlur blur;
    private ScriptC_sobel sobel;
    private ScriptC_canny canny;
    private ScriptC_hysteresis hysteresis;
    //private ScriptC_accumulator accumulator;
    private ScriptGroup.Builder mScriptGroupBuilderCanny;
    private ScriptGroup mScriptGroupCanny;
    private TextureView mTextureView;
    private Surface mSurface;
    private MySurfaceView mSurfaceView;
    private Allocation mAllocation512Unit;
    TextureView.SurfaceTextureListener mSurfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            mSurface=new Surface(surfaceTexture);
            Matrix matrix=new Matrix();
            float x=(float)WIDTH/HEIGHT;
            float y=(float)HEIGHT/WIDTH;
            matrix.setRotate(90);
            matrix.postScale(y, x);
            //800x480 not fullscreen: 1000
            //1920x1080 and 1280x720 not fullscreen: 1040
            //1920x1080 and 1280x720 fullscreen:1060
            matrix.postTranslate(1060, 0);
            mTextureView.setTransform(matrix);
            startHandler();
            setupCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            mCameraDevice.close();
            stopHandler();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    AllocationAdapter.OnBufferAvailableListener mAllocationListener=new Allocation.OnBufferAvailableListener() {
        @Override
        public void onBufferAvailable(Allocation allocation) {
            mAllocationIn.ioReceive();
            mScriptGroupCanny.execute();
//            accumulator.forEach_accumulate(mAllocation512Unit);
//            accumulator.forEach_setIndexes(mAllocation512Unit);
//            accumulator.forEach_midpointCircle(mAllocation512Unit);
            mSurfaceView.update(WIDTH / 2 * 1845 / WIDTH, HEIGHT / 2 * 1005 / HEIGHT, 0);
            mAllocationOut.ioSend();
        }
    };
    CameraDevice.StateCallback callback=new CameraDevice.StateCallback(){
        @Override
        public void onOpened(CameraDevice cameraDevice){
            mCameraDevice=cameraDevice;
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice){
            cameraDevice.close();
            mCameraDevice=null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {

        }
    };
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private void startPreview(){
        mAllocationIn=Allocation.createTyped(mRS,Type.createXY(mRS, Element.U8_4(mRS), WIDTH, HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_IO_INPUT | Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT);
        mAllocationOut=Allocation.createTyped(mRS,Type.createXY(mRS, Element.U8_4(mRS), WIDTH, HEIGHT),
                Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_GRAPHICS_TEXTURE |  Allocation.USAGE_SCRIPT);
//        mAllocationEdges=Allocation.createTyped(mRS, Type.createXY(mRS, Element.U16_2(mRS), 400,600), Allocation.MipmapControl.MIPMAP_NONE,
//                Allocation.USAGE_SCRIPT|Allocation.USAGE_GRAPHICS_TEXTURE);
//        mAllocationAccumulation=Allocation.createTyped(mRS, Type.createXY(mRS, Element.U16_2(mRS), 400,2000), Allocation.MipmapControl.MIPMAP_NONE,
//                Allocation.USAGE_SCRIPT|Allocation.USAGE_GRAPHICS_TEXTURE);
//        mAllocation512Unit =Allocation.createTyped(mRS,Type.createX(mRS, Element.U8(mRS), 400),Allocation.MipmapControl.MIPMAP_NONE,
//                Allocation.USAGE_GRAPHICS_TEXTURE | Allocation.USAGE_SCRIPT);
        mAllocationOut.setSurface(mSurface);
        canny.set_height(HEIGHT);
        canny.set_width(WIDTH);
        canny.set_UPPER(UPPER);
        canny.set_LOWER(LOWER);
        hysteresis.set_height(HEIGHT);
//        accumulator.set_height(HEIGHT);
//        accumulator.set_width(WIDTH);
//        accumulator.set_inAllocation(mAllocationOut);
//        accumulator.set_accumulationAllocation(mAllocationAccumulation);
//        accumulator.set_edgesAllocation(mAllocationEdges);
        hysteresis.set_width(WIDTH);
        hysteresis.set_gradientThreshold(GRADIENT_THRESHOLD);
        colorSplit.invoke_setRGB((short) RED, (short) GREEN, (short) BLUE);
        blur.setRadius(BLUR_RADIUS);
        sobel.set_width((long) WIDTH);
        sobel.set_height((long) HEIGHT);
        //setup scriptgroup flow chain
        mScriptGroupBuilderCanny =new ScriptGroup.Builder(mRS);
        mScriptGroupBuilderCanny.addKernel(colorSplit.getKernelID_split());
        mScriptGroupBuilderCanny.addKernel(blur.getKernelID());
        mScriptGroupBuilderCanny.addKernel(sobel.getKernelID_sobel());
        mScriptGroupBuilderCanny.addKernel(canny.getKernelID_suppress());
        mScriptGroupBuilderCanny.addKernel(hysteresis.getKernelID_hysteresis());
        Type u8=Type.createXY(mRS,Element.U8(mRS),WIDTH,HEIGHT);
        mScriptGroupBuilderCanny.addConnection(u8,
                colorSplit.getKernelID_split(), blur.getFieldID_Input());
        mScriptGroupBuilderCanny.addConnection(u8,
                blur.getKernelID(), sobel.getFieldID_inAllocation());
        mScriptGroupBuilderCanny.addConnection(Type.createXY(mRS, Element.F32_2(mRS), WIDTH, HEIGHT),
                sobel.getKernelID_sobel(), canny.getFieldID_inAllocation());
        mScriptGroupBuilderCanny.addConnection(Type.createXY(mRS, Element.F32_2(mRS), WIDTH, HEIGHT),
                canny.getKernelID_suppress(), hysteresis.getFieldID_inAllocation());
        mScriptGroupCanny = mScriptGroupBuilderCanny.create();
        mScriptGroupCanny.setOutput(hysteresis.getKernelID_hysteresis(), mAllocationOut);
        mScriptGroupCanny.setInput(colorSplit.getKernelID_split(), mAllocationIn);
        mAllocationIn.setOnBufferAvailableListener(mAllocationListener);
        try {
            mCaptureRequestBuilder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE,FLASH_MODE);
            if(mAllocationIn.getSurface()==null){
                Toast.makeText(getBaseContext(),"Sorry, something went wrong when initializing renderscript",Toast.LENGTH_LONG).show();
                return;
            }
            mCaptureRequestBuilder.addTarget(mAllocationIn.getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(mAllocationIn.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    try {
                        CaptureRequest mCaptureRequest = mCaptureRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startHandler(){
        mHandlerThread=new HandlerThread("CameraThread");
        mHandlerThread.start();
        mHandler=new Handler(mHandlerThread.getLooper());

    }
    private void stopHandler(){
        if(mHandlerThread!=null) {
            mHandlerThread.quitSafely();
            try {
                mHandlerThread.join();
                mHandlerThread = null;
                mHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getBaseContext(),"You can find settings in storage/emulated/0/OpenCVCamera/settings.txt file",Toast.LENGTH_LONG).show();
    }
    public void setupCamera(){
        mCameraManager= (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try {
            for(String id:mCameraManager.getCameraIdList()){
                CameraCharacteristics mCameraCharacteristics=mCameraManager.getCameraCharacteristics(id);
                if(mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING)==CameraCharacteristics.LENS_FACING_BACK){
                    mCameraId=id;
                }
            }
            CameraCharacteristics mCameraCharacteristics=mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap mStreamConfigurationMap=mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes=mStreamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888);
            mCameraManager.openCamera(mCameraId,callback,mHandler);
            startPreview();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    public void settings(){
        File open=new File("storage/emulated/0/OpenCVCamera");
        open.mkdir();
        File settings=new File("storage/emulated/0/OpenCVCamera/settings.txt");
        if(!settings.exists()){
            try {
                settings.createNewFile();
                FileOutputStream fos=new FileOutputStream(settings);
                fos.write(("ALL SETTINGS MUST END IN A \".\" \n " +
                                  "width:1280. (set image width, must be compatible with your phone's camera)\n " +
                                  "height:720. (set image width, must be compatible with your phone's camera)\n " +
                                  "blur:3. (set size of internal blur, between 1 and 25)\n " +
                                  "red:1.(set the red channel portion, must be positive or 0) \n" +
                                  "blue:1. (set the blue channel proportion, must be positive or 0) \n" +
                                  "green:1. (set the green channel proportion, must be positive or 0 \n" +
                                "lower:20. (set the lower threshold for the Canny algorithm\n" +
                                "upper:40. (set the upper threshold for the Canny algorithm").getBytes());
            }catch(java.io.IOException e){
                e.printStackTrace();
            }
        }
        try {
            FileInputStream fis = new FileInputStream(settings);
            byte[] buffer=new byte[fis.available()];
            fis.read(buffer);
            String contents=new String(buffer,"UTF-8");
            //find width
            if(contents.contains("width")){
                String parsed=contents.substring(contents.indexOf("width:"));
                String width=parsed.substring(parsed.indexOf("width:")+"width:".length(),parsed.indexOf("."));
                WIDTH=Integer.decode(width);
            }else{
                WIDTH=1280;
            }
            if(contents.contains("height")){
                String parsed=contents.substring(contents.indexOf("height:"));
                String height=parsed.substring(parsed.indexOf("height:")+"height:".length(),parsed.indexOf("."));
                HEIGHT=Integer.decode(height);
            }else{
                HEIGHT=720;
            }
            if(contents.contains("blur")){
                String parsed=contents.substring(contents.indexOf("blur:"));
                String blur=parsed.substring(parsed.indexOf("blur:")+"blur:".length(),parsed.indexOf("."));
                BLUR_RADIUS=Integer.decode(blur);
                if(BLUR_RADIUS>25){
                    BLUR_RADIUS=25;
                }
            }else{
                BLUR_RADIUS=3;
            }
            if(contents.contains("lower")){
                String parsed=contents.substring(contents.indexOf("lower:"));
                String blur = parsed.substring(parsed.indexOf("lower:")+"lower:".length(),parsed.indexOf("."));
                LOWER=Short.decode(blur);
            }
            if(contents.contains("upper")){
                String parsed=contents.substring(contents.indexOf("upper:"));
                String blur = parsed.substring(parsed.indexOf("upper:")+"upper:".length(),parsed.indexOf("."));
                UPPER=Short.decode(blur);
            }
            if(contents.contains("flash")){
                String parsed=contents.substring(contents.indexOf("flash:"));
                String blur=parsed.substring(parsed.indexOf("flash:") + "flash:".length(), parsed.indexOf("."));
                if(blur.equals("on")){
                    FLASH_MODE=CaptureRequest.FLASH_MODE_TORCH;
                }else{
                    FLASH_MODE=CaptureRequest.FLASH_MODE_OFF;
                }
            }
            if(contents.contains("red")){
                String parsed=contents.substring(contents.indexOf("red:"));
                String blur=parsed.substring(parsed.indexOf("red:") + "red:".length(), parsed.indexOf("."));
                RED=Integer.decode(blur);
            }
            if(contents.contains("blue")){
                String parsed=contents.substring(contents.indexOf("blue:"));
                String blur=parsed.substring(parsed.indexOf("blue:") + "blue:".length(), parsed.indexOf("."));
                BLUE=Integer.decode(blur);
            }
            if(contents.contains("green")){
                String parsed=contents.substring(contents.indexOf("green:"));
                String blur=parsed.substring(parsed.indexOf("green:") + "green:".length(), parsed.indexOf("."));
                GREEN=Integer.decode(blur);
            }
            if(contents.contains("gradient")){
                String parsed=contents.substring(contents.indexOf("gradient:"));
                String blur=parsed.substring(parsed.indexOf("gradient:") + "gradient:".length(), parsed.indexOf(";"));
                GRADIENT_THRESHOLD=Float.valueOf(blur);
            }
        }catch (java.io.FileNotFoundException e){
            e.printStackTrace();
        }catch(java.io.IOException e ){
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,this, mLoaderCallback);
        ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        mRS=RenderScript.create(this);
        mTextureView=(TextureView)findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        mSurfaceView=(MySurfaceView)findViewById(R.id.surfaceView);
        mSurfaceView.setZOrderOnTop(true);
        SurfaceHolder holder=mSurfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        //accumulator=new ScriptC_accumulator(mRS);
        colorSplit=new ScriptC_colorsplit(mRS);
        blur=ScriptIntrinsicBlur.create(mRS,Element.U8(mRS));
        hysteresis=new ScriptC_hysteresis(mRS);
        sobel=new ScriptC_sobel(mRS);
        canny=new ScriptC_canny(mRS);
        settings();
    }
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//
//                } break;
//                default:
//                {
//                    super.onManagerConnected(status);
//                } break;
//            }
//        }
//    };
}