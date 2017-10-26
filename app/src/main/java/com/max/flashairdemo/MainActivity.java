package com.max.flashairdemo;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.demo_start)
    BootstrapButton demoStart;
    @BindView(R.id.demo_stop)
    BootstrapButton demoStop;
    @BindView(R.id.rv_photo)
    RecyclerView rvPhoto;
    private Handler updateHandler = new Handler();
    private int checkInterval = 5000;
    private String localDir;
    private List<PhotoBean> photoBeans;
    private PhotosAdapter photosAdapter;

    Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            listDirectory();
            updateHandler.postDelayed(statusChecker, checkInterval);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({R.id.demo_start, R.id.demo_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.demo_start:
                startToCheckPhoto();
                break;
            case R.id.demo_stop:
                stop();
                break;
        }
    }

    private void init() {
        mkPhotoDir();
        showLocalPhotos();
    }

    private void mkPhotoDir() {
        localDir = getExternalFilesDirs(null)[0].getAbsolutePath();
        File file = new File(localDir);
        if (file.exists()) {
            file.mkdir();
        }
    }

    private void showLocalPhotos() {
        photoBeans = new ArrayList<>();
        File file = new File(localDir);
        if (file.isDirectory()) {
            for (String s : file.list()) {
                PhotoBean photoBean = new PhotoBean(s, localDir + File.separator + s);
                photoBeans.add(photoBean);
            }
        }
        photosAdapter = new PhotosAdapter(this, photoBeans);
        rvPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPhoto.setAdapter(photosAdapter);
    }

    private void startToCheckPhoto() {
        updateHandler.postDelayed(statusChecker, 0);
    }

    private void listDirectory() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return FlashAirRequest.getString(params[0]);
            }

            @Override
            protected void onPostExecute(String result) {
//                Log.d("Mainactivity", result+"");
                String[] allFiles = result.split("([,\n])");
                for (int i = 2; i < allFiles.length; i++) {
                    if (allFiles[i].contains(".")) {
                        Log.d("Mainactivity", allFiles[i]);
                        if ((allFiles[i].toLowerCase().endsWith(".jpg"))
                                || (allFiles[i].toLowerCase().endsWith(".jpeg"))
                                || (allFiles[i].toLowerCase().endsWith(".jpe"))
                                || (allFiles[i].toLowerCase().endsWith(".png"))) {
                            // Image file
                            Integer date = Integer.parseInt(allFiles[i + 3]);
                            Integer time = Integer.parseInt(allFiles[i + 4]);
                            Log.d("Mainactivity", "date:" + getDate(date, "/"));
                            Log.d("Mainactivity", "time:" + time);
                            File file = new File(localDir + File.separator + allFiles[i]);
                            if (!file.exists()) {
                                downloadFile(allFiles[i], "DCIM/101D5200");

                            } else {
                                Log.d("Mainactivity", "图片已存在:" + allFiles[i]);
                            }
                        }
                    }
                }
            }
        }.execute("http://myflashair/command.cgi?op=100&&DIR=/DCIM/101D5200");
    }

    void downloadFile(final String downloadFile, String directory) {
        Log.d("MainActivity", "下载图片：" + downloadFile);
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                String fileName = params[0];
                return FlashAirRequest.getBitmap(fileName);
            }

            @Override
            protected void onPostExecute(Bitmap resultBitmap) {
                saveBitmap(resultBitmap, downloadFile);
            }
        }.execute("http://myflashair/" + directory + "/" + downloadFile.toString());
    }

    public void saveBitmap(Bitmap bm, String fileName) {
        Log.d("MainActivity", "保存图片" + fileName);
        File f = new File(localDir, fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            if (null != photoBeans) {
                PhotoBean photoBean = new PhotoBean(fileName, localDir + File.separator + fileName);
                photoBeans.add(photoBean);
                photosAdapter.notifyDataSetChanged();
            }
            Log.i("MainActivity", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getDate(Integer date, String sep) {
        return String.format("%04d", ((date >> 9) & 0x1FF) + 1980) + sep +
                String.format("%02d", (date >> 5) & 0xF) + sep +
                String.format("%02d", date & 0x1F);
    }

    private void stop() {
        updateHandler.removeCallbacks(statusChecker);
        Log.i("MainActivity", "停止检测");
    }
}
