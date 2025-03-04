package com.web5team.plugins.aliyun.nls;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.content.ContextCompat;

import com.alibaba.idst.nui.NativeNui;
import com.alibaba.idst.nui.INativeNuiCallback;
import com.alibaba.idst.nui.Constants;
import com.alibaba.idst.nui.AsrResult;
import com.alibaba.idst.nui.KwsResult;

import org.json.JSONObject;

/**
 * 此类封装了 Aliyun NLS SDK 的基本调用逻辑，参考了官方 SpeechTranscriberActivity 示例 :contentReference[oaicite:2]{index=2}，
 * 实现了 SDK 初始化、参数设置、音频录制及事件回调，并将识别事件通过自定义回调传递给上层。
 */
public class AliyunNls implements INativeNuiCallback {

    private static final String TAG = "AliyunNls";
    private NativeNui nuiInstance;
    private AudioRecord audioRecord;
    private Context context;
    private boolean isRecognizing = false;
    private Thread audioThread;
    private int sampleRate = 16000; // 默认采样率，可通过参数调整
    private int minBufferSize;
    private RecognitionCallback recognitionCallback;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface RecognitionCallback {
        /**
         * 当 SDK 有事件回调时调用，事件名称和数据以 JSON 格式传递给 JS 层
         */
        void onEvent(String event, JSONObject data);
        void onError(String error);
    }

    public AliyunNls(Context context) {
        this.context = context;
        nuiInstance = new NativeNui();
        // 初始化 SDK，此处 initParams 可为空或预先构造的 JSON 字符串（见下文 initialize 方法）
        nuiInstance.initialize(context, null, Constants.LogLevel.LOG_LEVEL_VERBOSE, true);
        nuiInstance.setCallback(this);
    }

    public void setRecognitionCallback(RecognitionCallback callback) {
        this.recognitionCallback = callback;
    }

    /**
     * 初始化 SDK，可传入完整的 JSON 配置参数（包括鉴权信息、debug 配置等），参考 Auth.java 中的逻辑 :contentReference[oaicite:3]{index=3}
     * @param initParams JSON 字符串，描述初始化参数
     */
    public void initialize(String initParams) {
        nuiInstance.initialize(context, initParams, Constants.LogLevel.LOG_LEVEL_VERBOSE, true);
    }

    /**
     * 开启实时识别
     * @param nlsParams JSON 字符串，设置识别相关参数（如中间结果、标点预测、采样率等）
     * @param dialogParams JSON 字符串，传入对话参数（可用于更新 Token 等），参考官方示例中 genDialogParams 的逻辑
     */
    public void startRealTimeRecognition(String nlsParams, String dialogParams) {
        if (isRecognizing) {
            Log.w(TAG, "Already recognizing");
            return;
        }
        // 设置识别参数
        nuiInstance.setParams(nlsParams);
        int ret = nuiInstance.startDialog(Constants.VadMode.TYPE_P2T, dialogParams);
        Log.i(TAG, "startDialog returned " + ret);
        if (ret != Constants.NuiResultCode.SUCCESS) {
            if (recognitionCallback != null) {
                recognitionCallback.onError("startDialog failed with code " + ret);
            }
            return;
        }
        isRecognizing = true;
        startAudioRecording();
    }

    /**
     * 停止实时识别：停止 SDK 对话及音频录制
     */
    public void stopRealTimeRecognition() {
        if (!isRecognizing) return;
        nuiInstance.stopDialog();
        stopAudioRecording();
        isRecognizing = false;
    }

    // 音频录制：采用 AudioRecord 从麦克风获取 PCM 数据
    private void startAudioRecording() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "RECORD_AUDIO permission not granted");
            if (recognitionCallback != null) {
                recognitionCallback.onError("RECORD_AUDIO permission not granted");
            }
            return;
        }
        minBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize);
        audioRecord.startRecording();
        audioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[minBufferSize];
                while (isRecognizing && !Thread.interrupted()) {
                    // SDK 会主动调用 onNuiNeedAudioData 以获取数据，
                    // 此处可用于额外处理或日志记录，如有需要可自行扩展
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read < 0) {
                        Log.e(TAG, "AudioRecord read error: " + read);
                    }
                }
            }
        });
        audioThread.start();
    }

    private void stopAudioRecording() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }
    }

    // 当 SDK 需要音频数据时调用，此处直接从 AudioRecord 获取数据
    @Override
    public int onNuiNeedAudioData(byte[] buffer, int len) {
        if (audioRecord == null) return -1;
        int read = audioRecord.read(buffer, 0, len);
        return read;
    }

    // SDK 事件回调，将事件及相关数据封装成 JSON 后通知上层
    @Override
    public void onNuiEventCallback(Constants.NuiEvent event, int resultCode, int arg2, KwsResult kwsResult, AsrResult asrResult) {
        try {
            JSONObject data = new JSONObject();
            data.put("event", event.name());
            data.put("resultCode", resultCode);
            if (asrResult != null) {
                data.put("asrResult", asrResult.allResponse);
            }
            if (recognitionCallback != null) {
                // 切换到主线程通知 JS 层
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        recognitionCallback.onEvent(event.name(), data);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "onNuiEventCallback exception: " + e.getMessage());
        }
    }

    @Override
    public void onNuiAudioStateChanged(Constants.AudioState state) {
        Log.i(TAG, "Audio state changed: " + state);
        if (recognitionCallback != null) {
            try {
                JSONObject data = new JSONObject();
                data.put("audioState", state.name());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        recognitionCallback.onEvent("AudioStateChanged", data);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "onNuiAudioStateChanged exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onNuiAudioRMSChanged(float val) {
        // 可选：返回音量变化信息
    }

    @Override
    public void onNuiVprEventCallback(Constants.NuiVprEvent event) {
        Log.i(TAG, "VPR event: " + event);
    }
}
