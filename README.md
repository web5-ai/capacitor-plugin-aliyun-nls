# capacitor-plugin-aliyun-nls

Aliyun NLS Capacitor Plugin

🚧 目前不走这个方案了，代码还有问题，只作为代码保留

## Install

```bash
npm install capacitor-plugin-aliyun-nls
npx cap sync
```

## API

<docgen-index>

- [capacitor-plugin-aliyun-nls](#capacitor-plugin-aliyun-nls)
  - [Install](#install)
  - [API](#api)
    - [initialize(...)](#initialize)
    - [startRealTimeRecognition(...)](#startrealtimerecognition)
    - [stopRealTimeRecognition()](#stoprealtimerecognition)
    - [addListener('nlsEvent', ...)](#addlistenernlsevent-)
    - [addListener('nlsError', ...)](#addlistenernlserror-)
    - [removeAllListeners()](#removealllisteners)
    - [Interfaces](#interfaces)
      - [AliyunNlsInitParams](#aliyunnlsinitparams)
      - [PluginListenerHandle](#pluginlistenerhandle)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize(...)

```typescript
initialize(options: { initParams: AliyunNlsInitParams; }) => any
```

初始化 SDK，传入完整的 JSON 格式初始化参数（包含鉴权信息、服务 URL 等）

| Param         | Type                                                                                 |
| ------------- | ------------------------------------------------------------------------------------ |
| **`options`** | <code>{ initParams: <a href="#aliyunnlsinitparams">AliyunNlsInitParams</a>; }</code> |

**Returns:** <code>any</code>

--------------------


### startRealTimeRecognition(...)

```typescript
startRealTimeRecognition(options: { nlsParams: string; dialogParams: string; }) => any
```

开始实时语音识别

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code>{ nlsParams: string; dialogParams: string; }</code> |

**Returns:** <code>any</code>

--------------------


### stopRealTimeRecognition()

```typescript
stopRealTimeRecognition() => any
```

停止实时语音识别

**Returns:** <code>any</code>

--------------------


### addListener('nlsEvent', ...)

```typescript
addListener(eventName: 'nlsEvent', listenerFunc: (data: { event: string; data: string; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

添加识别事件监听器，事件回调数据包含事件名称及对应的 JSON 字符串数据

| Param              | Type                                                             |
| ------------------ | ---------------------------------------------------------------- |
| **`eventName`**    | <code>'nlsEvent'</code>                                          |
| **`listenerFunc`** | <code>(data: { event: string; data: string; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener('nlsError', ...)

```typescript
addListener(eventName: 'nlsError', listenerFunc: (data: { error: string; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

添加错误事件监听器，错误数据包含错误信息

| Param              | Type                                               |
| ------------------ | -------------------------------------------------- |
| **`eventName`**    | <code>'nlsError'</code>                            |
| **`listenerFunc`** | <code>(data: { error: string; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => any
```

移除所有事件监听器

**Returns:** <code>any</code>

--------------------


### Interfaces


#### AliyunNlsInitParams

| Prop         | Type                | Description                                                      |
| ------------ | ------------------- | ---------------------------------------------------------------- |
| **`appKey`** | <code>string</code> | 申请的 appKey                                                       |
| **`token`**  | <code>string</code> | 鉴权 token                                                         |
| **`url`**    | <code>string</code> | 服务 URL，例如 "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1" |


#### PluginListenerHandle

| Prop         | Type                      |
| ------------ | ------------------------- |
| **`remove`** | <code>() =&gt; any</code> |

</docgen-api>
