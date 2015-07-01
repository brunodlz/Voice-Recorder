package betweenbits.voicerecorder;

import android.content.pm.PackageManager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.Build;
import android.os.Environment;
import android.os.Bundle;

import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;

import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private Chronometer chronometer;
    private ImageButton btnPlay;
    private ImageButton btnStop;
    private ImageButton btnRecord;
    private TextView textType;

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

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MySound.mp3";
    }

    private void initializeViews() {
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        btnPlay   = (ImageButton) findViewById(R.id.btnPlay);
        btnStop   = (ImageButton) findViewById(R.id.btnStop);
        btnRecord = (ImageButton) findViewById(R.id.btnRecord);

        textType = (TextView) findViewById(R.id.textView);
    }

    protected boolean hasMicrophone() {
        PackageManager manager = this.getPackageManager();
        return manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void play(View view) throws IOException {
        btnPlay.setEnabled(false);
        btnPlay.setAlpha((float) 0.2);

        btnStop.setEnabled(true);

        btnRecord.setEnabled(false);
        btnRecord.setAlpha((float) 0.2);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaCompleted();
            }

        });
        initPlay();
        startChronometer();
        mediaPlayer.start();
    }

    private void mediaCompleted(){
        stopRecordOrPlay();

        btnPlay.setEnabled(true);
        btnPlay.setAlpha((float) 1.0);

        btnStop.setEnabled(false);
        btnStop.setAlpha((float) 0.2);

        btnRecord.setEnabled(true);
        btnRecord.setAlpha((float) 1.0);

        chronometer.stop();
    }

    public void stop(View view) {
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);
        btnPlay.setAlpha((float) 1.0);

        if (isRecording) {
            btnRecord.setEnabled(false);
            btnRecord.setAlpha((float) 0.2);

            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            btnRecord.setEnabled(true);
            btnRecord.setAlpha((float) 1.0);

            mediaPlayer.release();
            mediaPlayer = null;
            btnRecord.setEnabled(true);
        }
        chronometer.stop();
        stopRecordOrPlay();
    }

    public void record(View view) throws IOException {
        isRecording = true;

        btnPlay.setEnabled(false);
        btnPlay.setAlpha((float) 0.2);

        btnStop.setEnabled(true);
        btnStop.setAlpha((float) 1.0);

        btnRecord.setEnabled(false);

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
        initRecord();
        startChronometer();
        mediaRecorder.start();
    }

    private void startChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void initRecord() {
        textType.setText(R.string.record);
        textType.setTextColor(getResources().getColor(R.color.colorRed));
    }

    private void initPlay() {
        textType.setText(R.string.play);
        textType.setTextColor(getResources().getColor(R.color.colorBlue));
    }

    private void stopRecordOrPlay() {
        textType.setText(" ");
    }
}
