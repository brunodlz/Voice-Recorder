package betweenbits.voicerecorder;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private Toolbar myToolbar;
    private Button btnPlay;
    private Button btnStop;
    private Button btnRecord;

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
        btnPlay   = (Button) findViewById(R.id.btnPlay);
        btnStop   = (Button) findViewById(R.id.btnStop);
        btnRecord = (Button) findViewById(R.id.btnRecord);
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
        mediaPlayer.start();
    }

    public void stop(View view) {
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);

        if (isRecording) {
            btnRecord.setEnabled(false);
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
        btnStop.setEnabled(true);
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
        mediaRecorder.start();
    }
}
