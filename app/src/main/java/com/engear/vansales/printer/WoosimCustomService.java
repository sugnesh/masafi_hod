package com.engear.vansales.printer;

import android.os.Handler;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class WoosimCustomService {
    private static final String TAG = "WoosimCustomService";
    private static final boolean D = false;
    public static final String BLE_SERVICE = "a9fe5e12-de71-4020-b2cf-8bf764fb0a8d";
    public static final String BLE_RX_CHARACTERISTIC = "a9fe5e12-de71-4020-b2cf-8bf765fb0a8d";
    public static final String BLE_TX_CHARACTERISTIC = "a9fe5e12-de71-4020-b2cf-8bf766fb0a8d";
    public static final int MESSAGE_PRINTER = 100;
    public static final int UNPRESCRIBED = 0;
    public static final int MSR = 2;
    private final Handler mHandler;
    private Queue mRcvQueue;
    private ParsingThread mParsingThread;

    public WoosimCustomService(Handler var1) {
        this.mHandler = var1;
        this.mRcvQueue = null;
        this.mParsingThread = null;
    }

    public void processRcvData(byte[] var1, int var2) {
        if (this.mRcvQueue == null) {
            this.mRcvQueue = new Queue(1024);
        }

        this.mRcvQueue.add(var1, var2);
        if (this.mParsingThread == null) {
            this.mParsingThread = new ParsingThread();
            this.mParsingThread.start();
        }
    }

    public void clearRcvBuffer() {
        if (this.mRcvQueue != null) {
            this.mRcvQueue.reset();
        }
    }

    private class ParsingThread extends Thread {
        public ParsingThread() {
        }

        private void decodeMSRBlock(byte var1) {
            if (WoosimCustomService.this.mRcvQueue.remainData() >= 3) {
                WoosimCustomService.this.mRcvQueue.skipBytes(2);
                int var2 = 0;
                byte[] var3 = new byte[110];
                byte[][] var4 = new byte[3][];

                while (true) {
                    int var6;
                    while (true) {
                        if (!WoosimCustomService.this.mRcvQueue.isNotEmpty()) {
                            return;
                        }

                        byte var5;
                        if ((var5 = WoosimCustomService.this.mRcvQueue.getByte()) != 3) {
                            if (var5 == 10) {
                                return;
                            }

                            if (var5 == 13) {
                                continue;
                            }

                            if (var5 != 28) {
                                int var10000 = var6 = var2 + 1;
                                var3[var2] = var5;
                                if (var10000 > 104) {
                                    return;
                                }
                                break;
                            }

                            if (var2 == 0) {
                                continue;
                            }

                            if (var2 == 76) {
                                var4[0] = new byte[var2];
                                System.arraycopy(var3, 0, var4[0], 0, var2);
                            } else if (var2 == 37) {
                                var4[1] = new byte[var2];
                                System.arraycopy(var3, 0, var4[1], 0, var2);
                            } else {
                                if (var2 != 104) {
                                    continue;
                                }

                                var4[2] = new byte[var2];
                                System.arraycopy(var3, 0, var4[2], 0, var2);
                            }

                            var6 = 0;
                            break;
                        }

                        if (var2 == 37) {
                            var4[1] = new byte[var2];
                            System.arraycopy(var3, 0, var4[1], 0, var2);
                        } else if (var2 == 104) {
                            var4[2] = new byte[var2];
                            System.arraycopy(var3, 0, var4[2], 0, var2);
                        }

                        WoosimCustomService.this.mHandler.obtainMessage(100, 2, var1, var4).sendToTarget();
                        var6 = var2;
                        break;
                    }

                    var2 = var6;
                }
            }
        }

        public void run() {
            while (WoosimCustomService.this.mRcvQueue.isNotEmpty()) {
                byte var1;
                if ((var1 = WoosimCustomService.this.mRcvQueue.getByte()) != 0) {
                    int var2;
                    int var3;
                    int var4;
                    ByteBuffer var10002;
                    if (var1 != 2) {
                        if (var1 != 27) {
                            try {
                                Thread.sleep(500L);
                            } catch (InterruptedException var17) {
                                var17.printStackTrace();
                            }

                            ByteBuffer var24;
                            var10002 = var24 = ByteBuffer.allocate(var3 = (var2 = WoosimCustomService.this.mRcvQueue.remainData()) + 1);
                            var24.put(var1);
                            var10002.put(WoosimCustomService.this.mRcvQueue.getBytes(var2));
                            WoosimCustomService.this.mHandler.obtainMessage(100, 0, var3, var24).sendToTarget();
                            WoosimCustomService.this.mRcvQueue.reset();
                        } else {
                            try {
                                Thread.sleep(300L);
                            } catch (InterruptedException var18) {
                                var18.printStackTrace();
                            }

                            byte var10000 = (byte) (var2 = WoosimCustomService.this.mRcvQueue.getByte());
                            byte var23 = WoosimCustomService.this.mRcvQueue.getByte();
                            if ((var10000 == 77 && var23 == 49) || (var2 == 110 && var23 == 49)) {
                                WoosimCustomService.this.mHandler.obtainMessage(100, 2, 0).sendToTarget();
                            } else {
                                ByteBuffer var6;
                                int var25;
                                var10002 = var6 = ByteBuffer.allocate(var25 = (var4 = WoosimCustomService.this.mRcvQueue.remainData()) + 3);
                                var6.put(var1);
                                var6.put((byte) var2);
                                var6.put(var23);
                                var10002.put(WoosimCustomService.this.mRcvQueue.getBytes(var4));
                                WoosimCustomService.this.mHandler.obtainMessage(100, 0, var25, var6).sendToTarget();
                                WoosimCustomService.this.mRcvQueue.reset();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(700L);
                        } catch (InterruptedException var19) {
                            var19.printStackTrace();
                        }

                        if (((var2 = WoosimCustomService.this.mRcvQueue.getByte()) < 67 || var2 > 71) && var2 != 110) {
                            ByteBuffer var5;
                            var10002 = var5 = ByteBuffer.allocate(var4 = (var3 = WoosimCustomService.this.mRcvQueue.remainData()) + 2);
                            var5.put(var1);
                            var5.put((byte) var2);
                            var10002.put(WoosimCustomService.this.mRcvQueue.getBytes(var3));
                            WoosimCustomService.this.mHandler.obtainMessage(100, 0, var4, var5).sendToTarget();
                            WoosimCustomService.this.mRcvQueue.reset();
                        } else {
                            decodeMSRBlock((byte) var2);
                        }
                    }
                }
            }

            synchronized (WoosimCustomService.this) {
                WoosimCustomService.this.mParsingThread = null;
                WoosimCustomService.this.mRcvQueue.reset();
            }
        }
    }

    private class Queue {
        private byte[] mmBuffer;
        private int mmFront;
        private int mmRear;
        private final int mmQsize;

        public Queue(int var2) {
            this.mmBuffer = new byte[var2];
            Arrays.fill(this.mmBuffer, (byte) 0);
            this.mmFront = 0;
            this.mmRear = 0;
            this.mmQsize = var2;
        }

        private int remainSpace() {
            return this.mmQsize - this.remainData();
        }

        public boolean isNotEmpty() {
            return this.mmFront != this.mmRear;
        }

        public int remainData() {
            int var1;
            return (this.mmRear - this.mmFront + (var1 = this.mmQsize)) % var1;
        }

        public synchronized void add(byte[] var1, int var2) {
            if (var2 >= this.remainSpace()) {
                Log.e("WoosimCustomService", "Queue buffer is not enough");
            } else {
                int var3;
                int var4;
                if (var2 > (var3 = this.mmQsize) - (var4 = this.mmRear)) {
                    int var10001 = var3 -= var4;
                    System.arraycopy(var1, 0, this.mmBuffer, var4, var3);
                    System.arraycopy(var1, var10001, this.mmBuffer, 0, var2 - var3);
                } else {
                    System.arraycopy(var1, 0, this.mmBuffer, var4, var2);
                }

                this.mmRear = (this.mmRear + var2) % this.mmQsize;
            }
        }

        public synchronized byte getByte() {
            byte[] var1;
            int var2;
            byte var10000 = (var1 = this.mmBuffer)[var2 = this.mmFront];
            var1[var2] = 0;
            this.mmFront = var2 + 1;
            this.mmFront %= this.mmQsize;
            return var10000;
        }

        public synchronized byte[] getBytes(int var1) {
            if (this.remainData() < var1) {
                return null;
            } else {
                byte[] var2 = new byte[var1];
                int var3;
                int var4;
                if (var1 > (var3 = this.mmQsize) - (var4 = this.mmFront)) {
                    System.arraycopy(this.mmBuffer, var4, var2, 0, var3 - var4);
                    int var10004 = var3 = this.mmQsize;
                    int var10005 = this.mmFront;
                    var3 -= var10005;
                    var4 = var1 - (var10004 - var10005);
                    System.arraycopy(this.mmBuffer, 0, var2, var3, var4);
                    Arrays.fill(this.mmBuffer, this.mmFront, this.mmQsize, (byte) 0);
                    Arrays.fill(this.mmBuffer, 0, var1 - (this.mmQsize - this.mmFront), (byte) 0);
                } else {
                    System.arraycopy(this.mmBuffer, var4, var2, 0, var1);
                    int var10001 = this.mmFront;
                    Arrays.fill(this.mmBuffer, var10001, var10001 + var1, (byte) 0);
                }

                this.mmFront = (this.mmFront + var1) % this.mmQsize;
                return var2;
            }
        }

        public synchronized void skipBytes(int var1) {
            if (this.remainData() < var1) {
                Log.w("WoosimCustomService", "Not enough data to skip in Queue");
                this.reset();
            } else {
                int var2;
                int var3;
                if (var1 > (var2 = this.mmQsize) - (var3 = this.mmFront)) {
                    Arrays.fill(this.mmBuffer, var3, var2, (byte) 0);
                    Arrays.fill(this.mmBuffer, 0, var1 - (this.mmQsize - this.mmFront), (byte) 0);
                } else {
                    Arrays.fill(this.mmBuffer, var3, var3 + var1, (byte) 0);
                }

                this.mmFront = (this.mmFront + var1) % this.mmQsize;
            }

        }

        public synchronized void reset() {
            Arrays.fill(this.mmBuffer, (byte) 0);
            this.mmFront = 0;
            this.mmRear = 0;
        }
    }
}
