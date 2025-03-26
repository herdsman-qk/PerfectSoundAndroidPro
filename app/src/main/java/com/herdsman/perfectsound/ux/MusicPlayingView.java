/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 13/8/2023
 */

package com.herdsman.perfectsound.ux;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.herdsman.perfectsound.R;

public class MusicPlayingView extends AppCompatImageView {
    public MusicPlayingView(@NonNull Context context) {
        this(context, null);
    }

    public MusicPlayingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicPlayingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.FIT_XY);

        if (isInEditMode()) {
            setImageResource(R.drawable.music_note);
        }

    }

    private Handler handler = new Handler();

    private int counter = 0;

    private static final int ICONS[] = {
            R.drawable.music_note,
            R.drawable.music_note1,
            R.drawable.music_note2,
            R.drawable.music_note3
    };
    private static final int DELAY = 100;

    public void play() {
        setImageResource(ICONS[counter % 4]);
        ++counter;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }, DELAY);
    }

    public void stop() {
        setImageBitmap(null);
        handler.removeCallbacksAndMessages(null);
    }


}
