import { registerPlugin } from '@capacitor/core';

import type { AliyunNlsPlugin } from './definitions';

const AliyunNls = registerPlugin<AliyunNlsPlugin>('AliyunNls', {
  web: () => import('./web').then((m) => new m.AliyunNlsWeb()),
});

export * from './definitions';
export { AliyunNls };
