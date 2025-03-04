import { WebPlugin } from '@capacitor/core';

import type { AliyunNlsPlugin } from './definitions';

export class AliyunNlsWeb extends WebPlugin implements AliyunNlsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
