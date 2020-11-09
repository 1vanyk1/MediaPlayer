package com.example.mediaplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, MediaPlayer.OnCompletionListener {

    protected String convert_time(int time) {
        String min_str = Integer.toString(time / 60);
        int sec = time % 60;
        String sec_str = Integer.toString(sec);
        if (sec < 10) {
            sec_str = "0" + sec_str;
        }
        return min_str + ":" + sec_str;
    }

    class Count extends CountDownTimer {
        public Count(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            SeekBar sb = findViewById(R.id.seekBar2);
            TextView tv = findViewById(R.id.seconds);

            tv.setText(convert_time(mp.getCurrentPosition() / 1000) + "  /  " + convert_time(mp.getDuration() / 1000));
            sb.setProgress((int)((float)(mp.getCurrentPosition()) * 100f / (float)(mp.getDuration())));
        }

        @Override
        public void onFinish() {

        }
    }

    private MediaPlayer mp;
    private ImageView play;
    private ImageView stop;
    private float loud = 100;
    private SeekBar seekBar;
    private SeekBar seekBar2;
    private Count count;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = MediaPlayer.create(this, R.raw.song);
        play = findViewById(R.id.Play);
        play.setOnClickListener(this);
        stop = findViewById(R.id.Stop);
        stop.setOnClickListener(this);
        play.setImageResource(R.drawable.play_button);
        seekBar = findViewById(R.id.volume);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar2.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        count = new Count(1000000, 100);
        count.start();
    }

    private boolean is_playing = false;
    public void play(View v) {
        if (!is_playing) {
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
            is_playing = true;
            play.setImageResource(R.drawable.pause_button);
            mp.start();
        } else {
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
            is_playing = false;
            play.setImageResource(R.drawable.play_button);
            mp.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (is_playing) {
            mp.start();
        }
    }

    public void stop(View v) {
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        is_playing = false;
        play.setImageResource(R.drawable.play_button);
        mp.stop();
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.seekTo(0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (seekBar.getId() == R.id.volume) {
                loud = ((float) (progress)) / 100;
                mp.setVolume(loud, loud);
            } else if (seekBar.getId() == R.id.seekBar2) {
                mp.seekTo((int) ((float) (mp.getDuration()) / 100f * (float) (progress)));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Play) {
            play(v);
        } else if (v.getId() == R.id.Stop) {
            stop(v);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        SeekBar sb = this.findViewById(R.id.seekBar2);
        sb.setProgress(0);
        mp.stop();
        is_playing = false;
        play.setImageResource(R.drawable.play_button);
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}