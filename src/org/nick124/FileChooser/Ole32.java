package org.nick124.FileChooser;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Ole32 extends StdCallLibrary {

    Ole32 INSTANCE = Native.load("ole32", Ole32.class, W32APIOptions.DEFAULT_OPTIONS);

    int COINIT_APARTMENTTHREADED = 0x2;
    int CLSCTX_INPROC_SERVER     = 0x1;

    HRESULT CoInitializeEx(Pointer reserved, int dwCoInit);
    HRESULT CoInitialize(Pointer reserved);
    void CoUninitialize();
    void CoTaskMemFree(Pointer pv);

    HRESULT CoCreateInstance(
            Guid.CLSID rclsid,
            Pointer pUnkOuter,
            int dwClsContext,
            Guid.IID riid,
            PointerByReference ppv);
}