package org.nick124.FileChooser;

import java.io.File;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.PointerByReference;

public class Win11FolderChooser {

    private static final GUID CLSID_FileOpenDialog = new GUID("{DC1C5A9C-DE88-4D56-A587-C74246473955}");
    private static final GUID IID_IFileOpenDialog = new GUID("{d57c7288-d6ad-4737-9a4f-e250912944d8}");
    private static final int FOS_PICKFOLDERS = 0x00000020;

    private static class FileOpenDialogInstance extends Unknown {
        public FileOpenDialogInstance(Pointer p) {
            super(p);
        }

        public HRESULT getOptions(PointerByReference pOptions) {
            // MUST pass 'this.getPointer()' as the first array element
            return (HRESULT) this._invokeNativeObject(5, new Object[]{this.getPointer(), pOptions}, HRESULT.class);
        }

        public HRESULT setOptions(int options) {
            return (HRESULT) this._invokeNativeObject(4, new Object[]{this.getPointer(), options}, HRESULT.class);
        }

        public HRESULT show(Pointer parentHwnd) {
            return (HRESULT) this._invokeNativeObject(3, new Object[]{this.getPointer(), parentHwnd}, HRESULT.class);
        }

        public HRESULT getResult(PointerByReference pShellItem) {
            return (HRESULT) this._invokeNativeObject(27, new Object[]{this.getPointer(), pShellItem}, HRESULT.class);
        }
    }

    private static class ShellItemInstance extends Unknown {
        public ShellItemInstance(Pointer p) {
            super(p);
        }

        public HRESULT getDisplayName(int sigdnName, PointerByReference pPath) {
            return (HRESULT) this._invokeNativeObject(5, new Object[]{this.getPointer(), sigdnName, pPath}, HRESULT.class);
        }
    }

    public static File show() {
        HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED);
        if (!COMUtils.SUCCEEDED(hr) && hr.intValue() != 0x80010106) {
            return null;
        }

        PointerByReference ppv = new PointerByReference();
        hr = com.sun.jna.platform.win32.Ole32.INSTANCE.CoCreateInstance(
            CLSID_FileOpenDialog, null, Ole32.CLSCTX_INPROC_SERVER, IID_IFileOpenDialog, ppv
        );

        if (!COMUtils.SUCCEEDED(hr)) {
            Ole32.INSTANCE.CoUninitialize();
            return null;
        }

        FileOpenDialogInstance dialog = new FileOpenDialogInstance(ppv.getValue());
        File selectedFolder = null;

        try {
            PointerByReference pOptions = new PointerByReference();
            dialog.getOptions(pOptions);
            int options = pOptions.getValue().getInt(0);

            options |= FOS_PICKFOLDERS;
            dialog.setOptions(options);

            // Sets parent window context handle to NULL (Independent task window)
            HRESULT showHr = dialog.show(Pointer.NULL);

            if (COMUtils.SUCCEEDED(showHr)) {
                PointerByReference pShellItem = new PointerByReference();
                dialog.getResult(pShellItem);

                ShellItemInstance shellItem = new ShellItemInstance(pShellItem.getValue());
                try {
                    PointerByReference pPath = new PointerByReference();
                    shellItem.getDisplayName(0x80058000, pPath);

                    Pointer pathPtr = pPath.getValue();
                    String fullPath = pathPtr.getWideString(0);
                    selectedFolder = new File(fullPath);

                    Ole32.INSTANCE.CoTaskMemFree(pathPtr);
                } finally {
                    shellItem.Release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dialog.Release();
            Ole32.INSTANCE.CoUninitialize();
        }

        return selectedFolder;
    }
}