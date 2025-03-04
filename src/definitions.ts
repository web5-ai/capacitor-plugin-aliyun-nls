import { PluginListenerHandle } from '@capacitor/core';

export interface AliyunNlsPlugin {
  /**
   * 初始化 SDK，传入完整的 JSON 格式初始化参数（包含鉴权信息、服务 URL 等）
   */
  initialize(options: { initParams: string }): Promise<void>;

  /**
   * 开始实时语音识别
   * @param options.nlsParams - 识别参数，例如是否返回中间结果、标点预测、采样率等（JSON 字符串）
   * @param options.dialogParams - 启动对话的参数，通常用于传入 token 等信息（JSON 字符串）
   */
  startRealTimeRecognition(options: {
    nlsParams: string;
    dialogParams: string;
  }): Promise<void>;

  /**
   * 停止实时语音识别
   */
  stopRealTimeRecognition(): Promise<void>;

  /**
   * 添加识别事件监听器，事件回调数据包含事件名称及对应的 JSON 字符串数据
   */
  addListener(
    eventName: 'nlsEvent',
    listenerFunc: (data: { event: string; data: string }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * 添加错误事件监听器，错误数据包含错误信息
   */
  addListener(
    eventName: 'nlsError',
    listenerFunc: (data: { error: string }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * 移除所有事件监听器
   */
  removeAllListeners(): Promise<void>;
}
