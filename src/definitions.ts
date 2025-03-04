export interface AliyunNlsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
