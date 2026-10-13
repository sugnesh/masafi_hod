package com.engear.vansales.printer;
/**
 * Created by Rakshit on 06-Feb-17.
 */

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonRpcUtil {
    public static final String ERROR_OBJ_CODE = "code";
    public static final String ERROR_OBJ_MESSAGE = "message";
    public static final String EVENT_NAME_PRINT_PROGRESS = "lp.printProgressEvent";
    public static final String EVENT_PARAM_PROGRESS_CANCEL = "cancel";
    public static final String EVENT_PARAM_PROGRESS_COMPLETE = "complete";
    public static final String EVENT_PARAM_PROGRESS_ENDDOC = "endDoc";
    public static final String EVENT_PARAM_PROGRESS_ENDPAGE = "endPage";
    public static final String EVENT_PARAM_PROGRESS_FINISHED = "finished";
    public static final String EVENT_PARAM_PROGRESS_NONE = "none";
    public static final String EVENT_PARAM_PROGRESS_STARTDOC = "startDoc";
    public static final String EVENT_PARAM_PROGRESS_STARTPAGE = "startPage";
    public static final String EVENT_PARAM_PROGRESS_USERCANCEL = "userCancel";
    public static final int JSON_NULL_METHOD_ID = -1;
    public static final String KEY_NAME_ERROR = "error";
    public static final String KEY_NAME_ID = "id";
    public static final String KEY_NAME_JSONRPC = "jsonrpc";
    public static final String KEY_NAME_METHOD = "method";
    public static final String KEY_NAME_PARAMS = "params";
    public static final String KEY_NAME_RESULT = "result";
    public static final String METHOD_CONNECT = "lp.connect";
    public static final String METHOD_DISCONNECT = "lp.disconnect";
    public static final String METHOD_FLUSH = "lp.flush";
    public static final String METHOD_GET_BYTES_WRITTEN = "lp.getBytesWritten";
    public static final String METHOD_NEW_LINE = "lp.newLine";
    public static final String METHOD_OBTAIN_PRINT_HANDLE = "lp.obtainPrintHandle";
    public static final String METHOD_RELEASE_PRINT_HANDLE = "lp.releasePrintHandle";
    public static final String METHOD_SEND_CUSTOM_CMD = "lp.sendCustomCommand";
    public static final String METHOD_SET_BOLD = "lp.setBold";
    public static final String METHOD_SET_COMPRESS = "lp.setCompress";
    public static final String METHOD_SET_DOUBLE_HIGH = "lp.setDoubleHigh";
    public static final String METHOD_SET_DOUBLE_WIDE = "lp.setDoubleWide";
    public static final String METHOD_SET_ITALIC = "lp.setItalic";
    public static final String METHOD_SET_SETTING_BOOL = "lp.setSettingBool";
    public static final String METHOD_SET_SETTING_BYTES = "lp.setSettingBytes";
    public static final String METHOD_SET_SETTING_NUM = "lp.setSettingNum";
    public static final String METHOD_SET_SETTING_STRING = "lp.setSettingString";
    public static final String METHOD_SET_STRIKEOUT = "lp.setStrikeout";
    public static final String METHOD_SET_UNDERLINE = "lp.setUnderline";
    public static final String METHOD_WRITE_BYTES = "lp.writeBytes";
    public static final String METHOD_WRITE_GRAPHIC = "lp.writeGraphic";
    public static final String METHOD_WRITE_STR = "lp.writeStr";
    public static final String PARAM_CMD_ID = "commandID";
    public static final String PARAM_CONFIG_FILE_PATH = "configFilePath";
    public static final String PARAM_DATA = "data";
    public static final String PARAM_ENABLED = "enabled";
    public static final String PARAM_GRAPHIC_FILE = "graphicFile";
    public static final String PARAM_HEIGHT = "height";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_NUM_OF_LINES = "numLines";
    public static final String PARAM_PRINTER_ENTRY = "printerEntry";
    public static final String PARAM_PRINTER_URI = "printerURI";
    public static final String PARAM_PRINT_HANDLE = "prtHandle";
    public static final String PARAM_PROGRESS = "progress";
    public static final String PARAM_ROTATION = "rotation";
    public static final String PARAM_VALUE = "value";
    public static final String PARAM_WIDTH = "width";
    public static final String PARAM_XOFFSET = "xOffset";
    public static final String RESULT_BYTES_WRITTEN = "bytesWritten";
    public static final String RESULT_PRINT_HANDLE = "prtHandle";
    private static int s_iMethodID;

    static class JsonRpcError {
        public int code;
        public String message;

        JsonRpcError() {
            this.code = 0;
            this.message = null;
        }
    }

    static class JsonRpcResponse {
        public JsonRpcError error;
        public String jsonRpcVersion;
        protected boolean m_fHasResult;
        public int methodID;
        public JSONObject result;

        public JsonRpcResponse(String aResponse) throws LinePrinterException {
            this.jsonRpcVersion = null;
            this.result = null;
            this.error = null;
            this.methodID = JsonRpcUtil.JSON_NULL_METHOD_ID;
            this.m_fHasResult = false;
            if (aResponse != null) {
                //Logger.m1561d("JsonRpcResponse", aResponse);
                try {
                    JSONObject jsObj = (JSONObject) new JSONTokener(aResponse).nextValue();
                    this.jsonRpcVersion = jsObj.getString(JsonRpcUtil.KEY_NAME_JSONRPC);
                    if (jsObj.has(JsonRpcUtil.KEY_NAME_RESULT)) {
                        this.m_fHasResult = true;
                        if (!jsObj.isNull(JsonRpcUtil.KEY_NAME_RESULT)) {
                            this.result = jsObj.getJSONObject(JsonRpcUtil.KEY_NAME_RESULT);
                        }
                    }
                    if (!jsObj.isNull(JsonRpcUtil.KEY_NAME_ERROR)) {
                        JSONObject jsErrorObj = jsObj.getJSONObject(JsonRpcUtil.KEY_NAME_ERROR);
                        this.error = new JsonRpcError();
                        this.error.code = jsErrorObj.getInt(JsonRpcUtil.ERROR_OBJ_CODE);
                        this.error.message = jsErrorObj.getString(JsonRpcUtil.ERROR_OBJ_MESSAGE);
                    }
                    if (!jsObj.isNull(JsonRpcUtil.KEY_NAME_ID)) {
                        this.methodID = jsObj.getInt(JsonRpcUtil.KEY_NAME_ID);
                    }
                    if (hasError()) {
                        throw new LinePrinterException(this.error.message, this.error.code);
                    } else if (!this.m_fHasResult) {
                        throw new LinePrinterException("JSON-RPC response does not contain result", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                    } else {
                        return;
                    }
                } catch (JSONException ex) {
                    throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                }
            }
            throw new LinePrinterException("Null JSON-RPC response", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }

        public boolean hasError() {
            if (this.error == null) {
                return false;
            }
            return true;
        }

        public boolean hasResult() {
            return this.m_fHasResult;
        }

        public boolean isMethodIDNull() {
            return this.methodID == JsonRpcUtil.JSON_NULL_METHOD_ID;
        }
    }

    static class PrintEvent {
        public String eventName;
        public String jsonRpcVersion;

        public PrintEvent(String aEventMsg) throws LinePrinterException {
            this.jsonRpcVersion = null;
            if (aEventMsg != null) {
                try {
                    JSONObject jsObj = (JSONObject) new JSONTokener(aEventMsg).nextValue();
                    this.jsonRpcVersion = jsObj.getString(JsonRpcUtil.KEY_NAME_JSONRPC);
                    if (jsObj.isNull(JsonRpcUtil.KEY_NAME_METHOD)) {
                        throw new LinePrinterException("JSON-RPC event does not contain method object", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                    }
                    this.eventName = jsObj.getString(JsonRpcUtil.KEY_NAME_METHOD);
                    return;
                } catch (JSONException ex) {
                    throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                }
            }
            throw new LinePrinterException("Null JSON-RPC event message", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }

        public boolean isPrintProgressEvent() {
            if (this.eventName == null || !this.eventName.equals(JsonRpcUtil.EVENT_NAME_PRINT_PROGRESS)) {
                return false;
            }
            return true;
        }
    }

    static class BytesWrittenResponse extends JsonRpcResponse {
        public int bytesWritten;

        public BytesWrittenResponse(String aResponse) throws LinePrinterException {
            super(aResponse);
            this.bytesWritten = 0;
            if (this.result != null) {
                try {
                    this.bytesWritten = this.result.getInt(JsonRpcUtil.RESULT_BYTES_WRITTEN);
                    return;
                } catch (JSONException ex) {
                    throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                }
            }
            throw new LinePrinterException("JSON-RPC lp.getBytesWritten response contains null result", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    static class PrintHandleResponse extends JsonRpcResponse {
        public int prtHandle;

        public PrintHandleResponse(String aResponse) throws LinePrinterException {
            super(aResponse);
            this.prtHandle = 0;
            if (this.result != null) {
                try {
                    this.prtHandle = this.result.getInt(JsonRpcUtil.RESULT_PRINT_HANDLE);
                    return;
                } catch (JSONException ex) {
                    throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                }
            }
            throw new LinePrinterException("JSON-RPC lp.obtainPrintHandle response contains null result", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    static class PrintProgressEvent extends PrintEvent {
        private String m_sProgress;
        public int progress;
        public int prtHandle;

        public PrintProgressEvent(String aEventMsg) throws LinePrinterException {
            super(aEventMsg);
            this.m_sProgress = null;
            try {
                JSONObject jsObj = (JSONObject) new JSONTokener(aEventMsg).nextValue();
                if (jsObj.isNull(JsonRpcUtil.KEY_NAME_PARAMS)) {
                    throw new LinePrinterException("JSON-RPC print progress event does not contain params object", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                }
                JSONObject jsParams = jsObj.getJSONObject(JsonRpcUtil.KEY_NAME_PARAMS);
                this.prtHandle = jsParams.getInt(JsonRpcUtil.RESULT_PRINT_HANDLE);
                this.m_sProgress = jsParams.getString(JsonRpcUtil.PARAM_PROGRESS);
                if (this.m_sProgress == null) {
                    throw new LinePrinterException("JSON-RPC print progress event null progress", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
                } else if (this.m_sProgress.equals(JsonRpcUtil.EVENT_PARAM_PROGRESS_CANCEL)) {
                    this.progress = 6;
                } else if (this.m_sProgress.equals(JsonRpcUtil.EVENT_PARAM_PROGRESS_COMPLETE)) {
                    this.progress = 8;
                } else if (this.m_sProgress.equals(JsonRpcUtil.EVENT_PARAM_PROGRESS_ENDDOC)) {
                    this.progress = 4;
                } else if (this.m_sProgress.equals(JsonRpcUtil.EVENT_PARAM_PROGRESS_FINISHED)) {
                    this.progress = 7;
                } else if (this.m_sProgress.equals(JsonRpcUtil.EVENT_PARAM_PROGRESS_STARTDOC)) {
                    this.progress = 1;
                } else if (this.m_sProgress.equals(JsonRpcUtil.EVENT_PARAM_PROGRESS_NONE)) {
                    this.progress = 0;
                } else {
                    //Logger.m1561d("AndroidLinePrinter", "JSON-RPC event unsupported progress: " + this.m_sProgress);
                    this.progress = 0;
                }
            } catch (JSONException ex) {
                throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
            }
        }
    }

    JsonRpcUtil() {
    }

    static {
        s_iMethodID = 0;
    }

    public static String getConnectJsonStr(int aHandle, String aPrinterURI) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_CONNECT);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aPrinterURI != null) {
                jsonParamsObj.put(PARAM_PRINTER_URI, aPrinterURI);
            } else {
                jsonParamsObj.put(PARAM_PRINTER_URI, JSONObject.NULL);
            }
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getDisconnectJsonStr(int aHandle) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_DISCONNECT);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getFlushJsonStr(int aHandle) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_FLUSH);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getBytesWrittenJsonStr(int aHandle) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_GET_BYTES_WRITTEN);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getNewLineJsonStr(int aHandle, int aNumOfLines) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_NEW_LINE);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            jsonParamsObj.put(PARAM_NUM_OF_LINES, aNumOfLines);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getObtainPrintHandleJsonStr(String aConfigFilePath, String aPrinterID) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParmsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_OBTAIN_PRINT_HANDLE);
            if (aConfigFilePath != null) {
                jsonParmsObj.put(PARAM_CONFIG_FILE_PATH, aConfigFilePath);
            } else {
                jsonParmsObj.put(PARAM_CONFIG_FILE_PATH, JSONObject.NULL);
            }
            if (aPrinterID != null) {
                jsonParmsObj.put(PARAM_PRINTER_ENTRY, aPrinterID);
            } else {
                jsonParmsObj.put(PARAM_PRINTER_ENTRY, JSONObject.NULL);
            }
            jsonObj.put(KEY_NAME_PARAMS, jsonParmsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getReleasePrintHandleJsonStr(int aHandle) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParmsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_RELEASE_PRINT_HANDLE);
            jsonParmsObj.put(RESULT_PRINT_HANDLE, aHandle);
            jsonObj.put(KEY_NAME_PARAMS, jsonParmsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getSendCustomCmdJsonStr(int aHandle, String aCommandID) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_SEND_CUSTOM_CMD);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aCommandID != null) {
                jsonParamsObj.put(PARAM_CMD_ID, aCommandID);
            } else {
                jsonParamsObj.put(PARAM_CMD_ID, JSONObject.NULL);
            }
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getSetFontJsonStr(int aHandle, String aSetFontMethodName, boolean enabled) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, aSetFontMethodName);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            jsonParamsObj.put(PARAM_ENABLED, enabled);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getSetSettingBoolJsonStr(int aHandle, String aSettingName, boolean aBoolValue) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_SET_SETTING_BOOL);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aSettingName != null) {
                jsonParamsObj.put(PARAM_NAME, aSettingName);
            } else {
                jsonParamsObj.put(PARAM_NAME, JSONObject.NULL);
            }
            jsonParamsObj.put(PARAM_VALUE, aBoolValue);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getSetSettingBytesJsonStr(int aHandle, String aSettingName, byte[] aBytesValue) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_SET_SETTING_BYTES);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aSettingName != null) {
                jsonParamsObj.put(PARAM_NAME, aSettingName);
            } else {
                jsonParamsObj.put(PARAM_NAME, JSONObject.NULL);
            }
            if (aBytesValue != null) {
                jsonParamsObj.put(PARAM_VALUE, new String(aBytesValue));
            } else {
                jsonParamsObj.put(PARAM_VALUE, JSONObject.NULL);
            }
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getSetSettingNumJsonStr(int aHandle, String aSettingName, int aIntValue) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_SET_SETTING_NUM);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aSettingName != null) {
                jsonParamsObj.put(PARAM_NAME, aSettingName);
            } else {
                jsonParamsObj.put(PARAM_NAME, JSONObject.NULL);
            }
            jsonParamsObj.put(PARAM_VALUE, aIntValue);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getSetSettingStringJsonStr(int aHandle, String aSettingName, String aStrValue) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_SET_SETTING_STRING);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aSettingName != null) {
                jsonParamsObj.put(PARAM_NAME, aSettingName);
            } else {
                jsonParamsObj.put(PARAM_NAME, JSONObject.NULL);
            }
            if (aStrValue != null) {
                jsonParamsObj.put(PARAM_VALUE, aStrValue);
            } else {
                jsonParamsObj.put(PARAM_VALUE, JSONObject.NULL);
            }
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getWriteBytesJsonStr(int aHandle, byte[] aByteArray) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_WRITE_BYTES);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aByteArray != null) {
                jsonParamsObj.put(PARAM_DATA, new String(aByteArray));
            } else {
                jsonParamsObj.put(PARAM_DATA, JSONObject.NULL);
            }
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getWriteGraphicJsonStr(int aHandle, String aGraphicFilePath, int aRotation, int aXOffset, int aWidth, int aHeight) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_WRITE_GRAPHIC);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aGraphicFilePath != null) {
                jsonParamsObj.put(PARAM_GRAPHIC_FILE, aGraphicFilePath);
            } else {
                jsonParamsObj.put(PARAM_GRAPHIC_FILE, JSONObject.NULL);
            }
            jsonParamsObj.put(PARAM_ROTATION, aRotation);
            jsonParamsObj.put(PARAM_XOFFSET, aXOffset);
            jsonParamsObj.put(PARAM_WIDTH, aWidth);
            jsonParamsObj.put(PARAM_HEIGHT, aHeight);
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static String getWriteStrJsonStr(int aHandle, String aDataStr) throws LinePrinterException {
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonParamsObj = new JSONObject();
        try {
            jsonObj.put(KEY_NAME_JSONRPC, "2.0");
            jsonObj.put(KEY_NAME_METHOD, METHOD_WRITE_STR);
            jsonParamsObj.put(RESULT_PRINT_HANDLE, aHandle);
            if (aDataStr != null) {
                jsonParamsObj.put(PARAM_DATA, aDataStr);
            } else {
                jsonParamsObj.put(PARAM_DATA, JSONObject.NULL);
            }
            jsonObj.put(KEY_NAME_PARAMS, jsonParamsObj);
            jsonObj.put(KEY_NAME_ID, getMethodID());
            return jsonObj.toString();
        } catch (JSONException ex) {
            throw new LinePrinterException(ex.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
    }

    public static int getMethodID() {
        if (s_iMethodID < Integer.MAX_VALUE) {
            s_iMethodID++;
        } else {
            s_iMethodID = 1;
        }
        return s_iMethodID;
    }
}
