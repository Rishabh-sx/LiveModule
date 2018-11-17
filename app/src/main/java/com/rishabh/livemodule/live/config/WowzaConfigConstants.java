package com.rishabh.livemodule.live.config;

/**
 * Created by Rishabh Saxena.
 */

public class WowzaConfigConstants {


    public static final boolean SCALE_TO_ASPECT_RATIO = false;
    //Server Config
    public static String HOST_ADDRESS = "184.169.197.143";
    public static int PORT = 1935;
    public static String APP_NAME = "live";
    public static String WOWZA_USERNAME = "live";
    public static String WOWZA_PASSWORD = "123";


    //Video Configuration
    public static int VIDEO_FRAME_SIZE_WIDTH = 360;
    public static int VIDEO_FRAME_SIZE_HEIGHT = 640;
    public static int VIDEO_BITRATE = 800;
    public static int VIDEO_FRAMERATE = 30;
    public static int VIDEO_KEYFRAME_INTERVAL = 30;
    public static boolean ENABLE_ADAPTIVE_BITRATE = true;


    //Audio Configuration
    public static boolean ENABLE_AUDIO_STREAMING = true;
    public static boolean ENABLE_STREAM_STEREO_AUDIO = true;
    public static int AUDIO_BITRATE = 64000;
    public static int AUDIO_SAMPLE_RATE = 44100;

}
