package com.web5team.plugins.aliyun.nls;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import org.json.JSONObject;

/**
 * Capacitor 插件入口，提供 SDK 初始化、开始和停止实时识别三个方法。
 * 识别事件通过 “nlsEvent” 事件发送给 JS 层，错误则通过 “nlsError” 事件通知。
 */
@CapacitorPlugin(name = "AliyunNls")
public class AliyunNlsPlugin extends Plugin {

    private AliyunNls implementation;

    @Override
    public void load() {
        // 使用 Capacitor 提供的 Context 初始化 SDK
        implementation = new AliyunNls(getContext());
        // 设置识别回调，将 SDK 事件转发为 JS 事件
        implementation.setRecognitionCallback(new AliyunNls.RecognitionCallback() {
            @Override
            public void onEvent(String event, JSONObject data) {
                JSObject ret = new JSObject();
                ret.put("event", event);
                ret.put("data", data.toString());
                notifyListeners("nlsEvent", ret);
            }
            @Override
            public void onError(String error) {
                JSObject ret = new JSObject();
                ret.put("error", error);
                notifyListeners("nlsError", ret);
            }
        });
    }

    /**
     * 初始化 SDK
     * 传入参数示例：
     * {
     *    initParams: "{ \"app_key\": \"xxx\", \"token\": \"xxx\", \"url\": \"wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1\", ... }"
     * }
     */
    @PluginMethod
    public void initialize(PluginCall call) {
        String initParams = call.getString("initParams");
        if (initParams == null) {
            call.reject("initParams is required");
            return;
        }
        implementation.initialize(initParams);
        call.resolve();
    }

    /**
     * 开始实时语音识别
     * 传入参数示例：
     * {
     *    nlsParams: "{ \"enable_intermediate_result\": true, \"enable_punctuation_prediction\": true, ... }",
     *    dialogParams: "{ \"token\": \"xxx\", ... }"
     * }
     */
    @PluginMethod
    public void startRealTimeRecognition(PluginCall call) {
        String nlsParams = call.getString("nlsParams");
        String dialogParams = call.getString("dialogParams");
        if (nlsParams == null || dialogParams == null) {
            call.reject("nlsParams and dialogParams are required");
            return;
        }
        implementation.startRealTimeRecognition(nlsParams, dialogParams);
        call.resolve();
    }

    /**
     * 停止实时语音识别
     */
    @PluginMethod
    public void stopRealTimeRecognition(PluginCall call) {
        implementation.stopRealTimeRecognition();
        call.resolve();
    }
}
