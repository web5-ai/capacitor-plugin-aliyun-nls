import { ListenerCallback, PluginListenerHandle, WebPlugin } from '@capacitor/core';

import type { AliyunNlsPlugin } from './definitions';

export class AliyunNlsWeb extends WebPlugin implements AliyunNlsPlugin {
  initialize(): Promise<void> {
    throw new Error('Method not implemented.');
  }
  startRealTimeRecognition(): Promise<void> {
    throw new Error('Method not implemented.');
  }
  stopRealTimeRecognition(): Promise<void> {
    throw new Error('Method not implemented.');
  }

  addListener(
    eventName: 'nlsEvent',
    listenerFunc: (data: { event: string; data: string }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
  // 方法重载：nlsError
  addListener(
    eventName: 'nlsError',
    listenerFunc: (data: { error: string }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
  // 实际实现
  addListener(eventName: string, listenerFunc: ListenerCallback): Promise<PluginListenerHandle> & PluginListenerHandle {
    console.warn(`AliyunNlsWeb: addListener is not implemented on web for event "${eventName}"`);
    const result: PluginListenerHandle = {
      remove: () => Promise.resolve()
    };
    return Object.assign(Promise.resolve(result), result);
  }

  removeAllListeners(): Promise<void> {
    // Web 端暂无事件监听实现，直接返回 resolved Promise
    return Promise.resolve();
  }
}
