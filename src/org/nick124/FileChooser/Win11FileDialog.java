package org.nick124.FileChooser;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.PointerByReference;

public class Win11FileDialog {

    // CLSID_FileOpenDialog
    private static final Guid.CLSID CLSID_FileOpenDialog =
            new Guid.CLSID("DC1C5A9C-E88A-4DDE-A5A1-60F82A20AEF7");

    // IID_IFileOpenDialog
    private static final Guid.IID IID_IFileOpenDialog =
            new Guid.IID("d57c7288-d4ad-4768-be02-9d969532d960");

    // FOS flags
    private static final int FOS_PICKFOLDERS = 0x00000020;
    private static final int FOS_FORCEFILESYSTEM = 0x00000040;

    public static Path openFile() {
        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        // 0x80010106 = RPC_E_CHANGED_MODE -> COM already initialized on this thread
        // with a different concurrency model; not fatal, just don't re-uninitialize wrongly.
        boolean weInitialized = COMUtils.SUCCEEDED(hr) || hr.intValue() != 0x80010106;

        try {
            PointerByReference pDialog = new PointerByReference();

            hr = Ole32.INSTANCE.CoCreateInstance(
                    CLSID_FileOpenDialog,
                    null,
                    WTypes.CLSCTX_INPROC_SERVER,
                    IID_IFileOpenDialog,
                    pDialog
            );

            if (COMUtils.FAILED(hr)) {
                throw new RuntimeException("Failed to create dialog: " + hr);
            }

            IFileOpenDialog dialog = new IFileOpenDialog(pDialog.getValue());

            try {
                // Folder-only picker
                hr = dialog.SetOptions(FOS_FORCEFILESYSTEM);
                if (COMUtils.FAILED(hr)) {
                    throw new RuntimeException("Failed to set dialog options: " + hr);
                }

                hr = dialog.Show(null);
                if (hr.intValue() == 0x800704C7) {
                    return null; // user cancelled
                }
                if (COMUtils.FAILED(hr)) {
                    throw new RuntimeException("Failed to show dialog: " + hr);
                }

                PointerByReference result = new PointerByReference();
                hr = dialog.GetResult(result);
                if (COMUtils.FAILED(hr)) {
                    throw new RuntimeException("Failed to get dialog result: " + hr);
                }

                IShellItem item = new IShellItem(result.getValue());
                try {
                    PointerByReference pszString = new PointerByReference();
                    // SIGDN_FILESYSPATH
                    item.GetDisplayName(0x80058000, pszString);

                    Pointer pathPtr = pszString.getValue();
                    String path = pathPtr.getWideString(0);
                    Ole32.INSTANCE.CoTaskMemFree(pathPtr);

                    return Paths.get(path);
                } finally {
                    item.Release();
                }
            } finally {
                dialog.Release();
            }

        } finally {
            if (weInitialized) {
                Ole32.INSTANCE.CoUninitialize();
            }
        }
    }

    public static Path openFolder() {
        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        // 0x80010106 = RPC_E_CHANGED_MODE -> COM already initialized on this thread
        // with a different concurrency model; not fatal, just don't re-uninitialize wrongly.
        boolean weInitialized = COMUtils.SUCCEEDED(hr) || hr.intValue() != 0x80010106;

        try {
            PointerByReference pDialog = new PointerByReference();

            hr = Ole32.INSTANCE.CoCreateInstance(
                    CLSID_FileOpenDialog,
                    null,
                    WTypes.CLSCTX_INPROC_SERVER,
                    IID_IFileOpenDialog,
                    pDialog
            );

            if (COMUtils.FAILED(hr)) {
                throw new RuntimeException("Failed to create dialog: " + hr);
            }

            IFileOpenDialog dialog = new IFileOpenDialog(pDialog.getValue());

            try {
                // Folder-only picker
                hr = dialog.SetOptions(FOS_PICKFOLDERS | FOS_FORCEFILESYSTEM);
                if (COMUtils.FAILED(hr)) {
                    throw new RuntimeException("Failed to set dialog options: " + hr);
                }

                hr = dialog.Show(null);
                if (hr.intValue() == 0x800704C7) {
                    return null; // user cancelled
                }
                if (COMUtils.FAILED(hr)) {
                    throw new RuntimeException("Failed to show dialog: " + hr);
                }

                PointerByReference result = new PointerByReference();
                hr = dialog.GetResult(result);
                if (COMUtils.FAILED(hr)) {
                    throw new RuntimeException("Failed to get dialog result: " + hr);
                }

                IShellItem item = new IShellItem(result.getValue());
                try {
                    PointerByReference pszString = new PointerByReference();
                    // SIGDN_FILESYSPATH
                    item.GetDisplayName(0x80058000, pszString);

                    Pointer pathPtr = pszString.getValue();
                    String path = pathPtr.getWideString(0);
                    Ole32.INSTANCE.CoTaskMemFree(pathPtr);

                    return Paths.get(path);
                } finally {
                    item.Release();
                }
            } finally {
                dialog.Release();
            }

        } finally {
            if (weInitialized) {
                Ole32.INSTANCE.CoUninitialize();
            }
        }
    }

    // ---------------- COM WRAPPERS ----------------

    public static class IFileOpenDialog extends Unknown {
        public IFileOpenDialog(Pointer pointer) {
            super(pointer);
        }

        // IModalWindow::Show
        public WinNT.HRESULT Show(WinDef.HWND hwnd) {
            return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), hwnd}, WinNT.HRESULT.class);
        }

        // IFileDialog::SetOptions
        public WinNT.HRESULT SetOptions(int options) {
            return (WinNT.HRESULT) _invokeNativeObject(9, new Object[]{getPointer(), options}, WinNT.HRESULT.class);
        }

        // IFileDialog::GetOptions
        public WinNT.HRESULT GetOptions(PointerByReference pOptions) {
            return (WinNT.HRESULT) _invokeNativeObject(10, new Object[]{getPointer(), pOptions}, WinNT.HRESULT.class);
        }

        // IFileDialog::GetResult
        public WinNT.HRESULT GetResult(PointerByReference ppsi) {
            return (WinNT.HRESULT) _invokeNativeObject(20, new Object[]{getPointer(), ppsi}, WinNT.HRESULT.class);
        }
    }

    public static class IShellItem extends Unknown {
        public IShellItem(Pointer pointer) {
            super(pointer);
        }

        // IShellItem::GetDisplayName
        public WinNT.HRESULT GetDisplayName(int sigdnName, PointerByReference ppszName) {
            return (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer(), sigdnName, ppszName}, WinNT.HRESULT.class);
        }
    }
}