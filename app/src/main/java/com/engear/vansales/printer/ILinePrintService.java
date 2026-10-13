package com.engear.vansales.printer;
/**
 * Created by Rakshit on 06-Feb-17.
 *
 *
 */

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILinePrintService extends IInterface {

    public static abstract class Stub extends Binder implements ILinePrintService {
        private static final String DESCRIPTOR = "com.intermec.print.service.ILinePrintService";
        static final int TRANSACTION_addLinePrintListener = 2;
        static final int TRANSACTION_execute = 1;
        static final int TRANSACTION_removeLinePrintListener = 3;

        private static class Proxy implements ILinePrintService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public String execute(String aInputStr) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(aInputStr);
                    this.mRemote.transact(Stub.TRANSACTION_execute, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addLinePrintListener(ILinePrintListener aListener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(aListener != null ? aListener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_addLinePrintListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeLinePrintListener(ILinePrintListener aListener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(aListener != null ? aListener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_removeLinePrintListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILinePrintService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ILinePrintService)) {
                return new Proxy(obj);
            }
            return (ILinePrintService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_execute /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    String _result = execute(data.readString());
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case TRANSACTION_addLinePrintListener /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    addLinePrintListener(ILinePrintListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_removeLinePrintListener /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeLinePrintListener(ILinePrintListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void addLinePrintListener(ILinePrintListener iLinePrintListener) throws RemoteException;

    String execute(String str) throws RemoteException;

    void removeLinePrintListener(ILinePrintListener iLinePrintListener) throws RemoteException;
}

