package com.web5team.plugins.aliyun.nls;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;
import org.json.JSONObject;

@CapacitorPlugin(name = "AliyunNls")
public class AliyunNlsPlugin extends Plugin {
    private AliyunNls nlsClient;

    @Override
    public void load() {
        nlsClient = new AliyunNls(getContext());
        nlsClient.setRecognitionCallback(new AliyunNls.RecognitionCallback() {
            @Override
            public void onEvent(String event, org.json.JSONObject data) throws JSONException {
                notifyListeners("nlsEvent", JSObject.fromJSONObject(data));
            }

            @Override
            public void onError(String error) {
                JSObject ret = new JSObject();
                ret.put("error", error);
                notifyListeners("nlsError", ret);
            }
        });
    }

    @PluginMethod
    public void initialize(PluginCall call) {
        try {
            JSONObject params = call.getData().getJSONObject("options");
            if (params == null) throw new JSONException("Missing init params");
            
            // 参数完整性校验
            if (!params.has("appKey") && !params.has("token") && 
               !params.has("accessKey")) {
                call.reject("Missing auth parameters");
                return;
            }

            nlsClient.initialize(params);
            call.resolve();
        } catch (JSONException e) {
            call.reject("Invalid parameters: " + e.getMessage());
        }
    }

    @PluginMethod
    public void startRealTimeRecognition(PluginCall call) {
        try {
            JSONObject options = call.getData().getJSONObject("options");
            String nlsParams = options.optString("nlsParams", getDefaultNlsParams());
            String dialogParams = options.optString("dialogParams", "{}");
            
            nlsClient.startRealTimeRecognition(nlsParams, dialogParams);
            call.resolve();
        } catch (Exception e) {
            call.reject("Start failed: " + e.getMessage());
        }
    }

    private String getDefaultNlsParams() {
        try {
            return new JSONObject()
                .put("enable_intermediate_result", true)
                .put("enable_punctuation_prediction", true)
                .toString();
        } catch (JSONException e) {
            return "{}";
        }
    }

    @PluginMethod
    public void stopRealTimeRecognition(PluginCall call) {
        nlsClient.stopRealTimeRecognition();
        call.resolve();
    }
}
