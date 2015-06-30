package betweenbits.voicerecorder;

import android.content.pm.PackageManager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.util.concurrent.TimeUnit;

import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;

import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private Chronometer chronometer;
    private ImageButton btnPlay;
    private ImageButton btnStop;
    private ImageButton btnRecord;

    private boolean isRecording = false;
    private static String filePath;

    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        initializeViews();

        if (!hasMicrophone()) {
            btnPlay.setEnabled(false);
            btnStop.setEnabled(false);
            btnRecord.setEnabled(false);
        } else {
            btnPlay.setEnabled(true);
            btnStop.setEnabled(true);
        }

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.3gp";
    }

    private void initializeViews() {
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        btnPlay   = (ImageButton) findViewById(R.id.btnPlay);
        btnStop   = (ImageButton) findViewById(R.id.btnStop);
        btnRecord = (ImageButton) findViewById(R.id.btnRecord);
    }

    protected boolean hasMicrophone() {
        PackageManager manager = this.getPackageManager();
        return manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void play(View view) throws IOException {
        btnPlay.setEnabled(false);
        btnStop.setEnabled(true);
        btnRecord.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaCompleted();
            }

        });
        mediaPlayer.start();
    }

    private void mediaCompleted(){
        btnPlay.setEnabled(true);
        btnStop.setEnabled(false);
        btnRecord.setEnabled(true);
    }

    public void stop(View view) {
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);
        btnPlay.setAlpha((float)1.0);

        if (isRecording) {
            btnRecord.setEnabled(false);
            chronometer.stop();
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            btnRecord.setEnabled(true);
        }
    }

    public void record(View view) throws IOException {
        isRecording = true;

        btnPlay.setEnabled(false);
        btnPlay.setAlpha((float) 0.5);

        btnStop.setEnabled(true);
        btnStop.setAlpha((float)1.0);

        btnRecord.setEnabled(false);

        chronometer.setBase(SystemClock.elapsedRealtime());

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        chronometer.start();
        mediaRecorder.start();
    }
}
