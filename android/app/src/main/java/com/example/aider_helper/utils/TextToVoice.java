package com.example.aider_helper.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.example.aider_helper.classes.MessageBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextToVoice {
    private static final String TAG = "TextToVoice";
    MediaPlayer mediaPlayer;
    DescribeVoicesRequest describeVoicesRequest;
    List<Voice> voices;
    List<String> listLanguages = Arrays.asList("en-AU", "en-GB", "en-GB-WLS", "en-IN", "en-US");
    int i = 0;
    // Backend resources
    private AmazonPollyPresigningClient client;

    public TextToVoice(Context context){
        initPollyClient(context);
        setupNewMediaPlayer();
    }

    public Voice getVoice(int i){
        if(voices!=null)
            return voices.get(i%voices.size());
        return null;
    }

    void initPollyClient(Context context) {
        AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                // Create a client that supports generation of presigned URLs.
                client = new AmazonPollyPresigningClient(AWSMobileClient.getInstance());

                if (voices == null) {
                    // Create describe voices request.
                    DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();// , "en-GB" ,"en-GB-WLS " ,"en-IN " ,"en-US");

                    try {
                        // Synchronously ask the Polly Service to describe available TTS voices.
                        DescribeVoicesResult describeVoicesResult = client.describeVoices(describeVoicesRequest);

                        // Get list of voices from the result.
                        voices = describeVoicesResult.getVoices();
                        List<Voice> new_voices = new ArrayList<>();
                        for(Voice voice : voices){
                            if(listLanguages.contains(voice.getLanguageCode()) && !new_voices.contains(voice))
                                new_voices.add(voice);
                        }
                        new_voices.remove(14);
                        new_voices.remove(6);
                        voices = new_voices;
                        // Log a message with a list of available TTS voices.
                        Log.i(TAG, "Available Polly voices: " + voices);
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Unable to get available voices.", e);
                        return;
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("TAG-ERROR", "onError: Initialization error", e);
            }
        });
    }

    void setupNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                setupNewMediaPlayer();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }




    public String getVoiceURL_FromAWS(String textToRead, Voice voice){
        // Create speech synthesis request.
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        // Set text to synthesize.
                        .withText(textToRead)
                        // Set voice selected by the user.
                        .withVoiceId(voice.getId())
                        // Set format to MP3.
                        .withOutputFormat(OutputFormat.Mp3);

        // Get the presigned URL for synthesized speech audio stream.
        URL presignedSynthesizeSpeechUrl =
                client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);
        return presignedSynthesizeSpeechUrl.toString();
    }



    public void playVoice(String textToRead, Voice voice) {
        if(voice == null || textToRead.equals("") || textToRead == null)
            return;
        // Create a media player to play the synthesized audio stream.
        if (mediaPlayer.isPlaying()) {
            setupNewMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set media player's data source to previously obtained URL.
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getVoiceURL_FromAWS(textToRead,voice));
        } catch (IOException e) {
            Log.e("TAG-ERROR", "Unable to set data source for the media player! " + e.getMessage());
            return;
        }

        // Start the playback asynchronously (since the data source is a network stream).
        mediaPlayer.prepareAsync();
    }


    public void playVoice(MessageBox messageBox, Voice voice) {
        if(voice == null || messageBox.getContent().equals("") || messageBox.getContent() == null)
            return;
        // Create a media player to play the synthesized audio stream.
        if (mediaPlayer.isPlaying()) {
            setupNewMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set media player's data source to previously obtained URL.
            mediaPlayer.reset();
            mediaPlayer.setDataSource(messageBox.getVoiceURL());
        } catch (IOException e) {
            Log.e("TAG-ERROR", "Unable to set data source for the media player! " + e.getMessage());
            return;
        }

        // Start the playback asynchronously (since the data source is a network stream).
        mediaPlayer.prepareAsync();
    }


}
