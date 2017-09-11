package com.zyongjun.easytouch.screenshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import com.windhike.fastcoding.rx.SchedulersTransFormer;
import com.windhike.fastcoding.util.UIUtil;
import com.zyongjun.easytouch.R;
import com.zyongjun.easytouch.utils.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Aria on 2017/7/19.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NewScreenShotUtilImpl extends ScreenShotUtil{

    private MediaProjection mediaProjection;
    public static Intent data;

    private int screenWidth;
    private int screenHeight;
    private int screenDensity;

    private ImageReader imageReader;

    public NewScreenShotUtilImpl(Context context){
        super(context);
        initData();
    }

    private void initData(){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        screenDensity = displayMetrics.densityDpi;
        imageReader = ImageReader.newInstance(screenWidth,screenHeight, PixelFormat.RGBA_8888,1);
    }

    private boolean startVirtual(){
        if (data == null){
            Toast.makeText(context,context.getResources().getString(R.string.msg_reOpen_screenshot),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mediaProjection == null){
            mediaProjection = ((MediaProjectionManager)context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)).
                    getMediaProjection(Activity.RESULT_OK,data);;
        }
        VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay("screen_mirror", screenWidth, screenHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),null, null);
        return true;
    }

    private void startCapture(){
        Observable.just(1)
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(Integer integer) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                Image image = null;
                                if (imageReader != null) {
                                    image = imageReader.acquireLatestImage();
                                }
                                if (image == null && data!=null){
                                    subscriber.onNext("");
                                    subscriber.onCompleted();
                                }else {
                                    Bitmap bitmap = UIUtil.image2Bitmap(image);

                                    File fileImage = null;
                                    try {
                                        fileImage = new File(FileUtil.getScreenShotsName(context));
                                        if (!fileImage.getParentFile().exists()) {
                                            fileImage.getParentFile().mkdirs();
                                        }
                                        if (!fileImage.exists()){
                                            fileImage.createNewFile();
                                        }
                                        FileOutputStream outputStream = new FileOutputStream(fileImage);
                                        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                                        outputStream.flush();
                                        outputStream.close();

                                        Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        Uri contentUri = Uri.fromFile(fileImage);
                                        media.setData(contentUri);
                                        context.sendBroadcast(media);
                                        subscriber.onNext(fileImage.getAbsolutePath());
                                        subscriber.onCompleted();
                                    } catch (IOException e) {
                                        subscriber.onError(new Exception("截图异常"));
                                    } finally {
                                        mediaProjection.stop();
                                    }
                                }
                            }
                        });
                    }
                }).compose(SchedulersTransFormer.<String>applyExecutorSchedulers())
                .subscribe(getShotObserver());
    }

    public static void setData(Intent data1) {
        data = data1;
    }

    @Override
    public void startScreenshot() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startVirtual()) startCapture();
            }
        },80);
    }

    public void startScreenshotAfterGranted() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startVirtual()) startCapture();
            }
        },100);
    }

    @Override
    public boolean isSupportScreenshot() {
        return true;
    }

    @Override
    public void setHandler(Handler handler) {

    }


}
