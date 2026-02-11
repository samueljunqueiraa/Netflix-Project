package br.edu.ifsuldeminas.mch.netflix;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.media.MediaPlayer;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private SeekBar seekBar;
    private TextView timeElapsed;
    private TextView timeTotal;
    private ImageButton playPauseButton;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.vv);
        seekBar = findViewById(R.id.seekBar2);
        timeElapsed = findViewById(R.id.timeElapsed);
        timeTotal = findViewById(R.id.timeTotal);
        playPauseButton = findViewById(R.id.imageButton);

        Uri src  = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
        videoView.setVideoURI(src);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int totalDuration = videoView.getDuration();
                timeTotal.setText(formatTime(totalDuration));
                seekBar.setMax(totalDuration);

                updateSeekBar = new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = videoView.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        timeElapsed.setText(formatTime(currentPosition));
                        handler.postDelayed(this, 1000);
                    }
                };

                handler.postDelayed(updateSeekBar, 0);
                videoView.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    videoView.start();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                    timeElapsed.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(updateSeekBar, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        int hours = (milliseconds / (1000 * 60 * 60)) % 24;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
