/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Github\\LAParking\\openxc\\src\\main\\aidl\\com\\openxc\\remote\\VehicleServiceInterface.aidl
 */
package com.openxc.remote;
/**
 * The AIDL interface for a VehicleService running in a separate process.
 *
 * This interface is used to bind to an AIDL interface in order to interact with
 * a centralized VehicleService that is reading data from a vehicle, trace
 * file or other source.
 *
 * Applications should use the in-process VehicleManager, as that builds the
 * proper Measurement types before returning - the data at this level is very
 * loosely typed in order to slip through the limited AIDL interface.
 */
public interface VehicleServiceInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.openxc.remote.VehicleServiceInterface
{
private static final java.lang.String DESCRIPTOR = "com.openxc.remote.VehicleServiceInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.openxc.remote.VehicleServiceInterface interface,
 * generating a proxy if needed.
 */
public static com.openxc.remote.VehicleServiceInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.openxc.remote.VehicleServiceInterface))) {
return ((com.openxc.remote.VehicleServiceInterface)iin);
}
return new com.openxc.remote.VehicleServiceInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_get:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.openxc.remote.RawMeasurement _result = this.get(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_send:
{
data.enforceInterface(DESCRIPTOR);
com.openxc.remote.RawMeasurement _arg0;
if ((0!=data.readInt())) {
_arg0 = com.openxc.remote.RawMeasurement.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.send(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_register:
{
data.enforceInterface(DESCRIPTOR);
com.openxc.remote.VehicleServiceListener _arg0;
_arg0 = com.openxc.remote.VehicleServiceListener.Stub.asInterface(data.readStrongBinder());
this.register(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregister:
{
data.enforceInterface(DESCRIPTOR);
com.openxc.remote.VehicleServiceListener _arg0;
_arg0 = com.openxc.remote.VehicleServiceListener.Stub.asInterface(data.readStrongBinder());
this.unregister(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_receive:
{
data.enforceInterface(DESCRIPTOR);
com.openxc.remote.RawMeasurement _arg0;
if ((0!=data.readInt())) {
_arg0 = com.openxc.remote.RawMeasurement.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.receive(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getMessageCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getMessageCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_addVehicleInterface:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.addVehicleInterface(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_removeVehicleInterface:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.removeVehicleInterface(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setBluetoothPollingStatus:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setBluetoothPollingStatus(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getSourceSummaries:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _result = this.getSourceSummaries();
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_getSinkSummaries:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _result = this.getSinkSummaries();
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_getActiveSourceTypeStrings:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _result = this.getActiveSourceTypeStrings();
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_userPipelineActivated:
{
data.enforceInterface(DESCRIPTOR);
this.userPipelineActivated();
reply.writeNoException();
return true;
}
case TRANSACTION_userPipelineDeactivated:
{
data.enforceInterface(DESCRIPTOR);
this.userPipelineDeactivated();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.openxc.remote.VehicleServiceInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Retreive the most recent value for the measurement.
     *
     * @param measurementType must match the ID field of a known Measurement
     *                        subclass.
     * @return a RawMeasurement which may or may not have a value. This function
     *         will never return null, even if no value is available.
     */
@Override public com.openxc.remote.RawMeasurement get(java.lang.String measurementType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.openxc.remote.RawMeasurement _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(measurementType);
mRemote.transact(Stub.TRANSACTION_get, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.openxc.remote.RawMeasurement.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Set a new value for the measurement class on the vehicle.
     *
     * @param measurement The measurement to set on the vehicle.
     */
@Override public boolean send(com.openxc.remote.RawMeasurement measurement) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((measurement!=null)) {
_data.writeInt(1);
measurement.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_send, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Register to receive asynchronous updates when measurements are received.
     *
     * All instances of VehicleManager in application processes must register
     * themselves if they want to use the asynchronous interface.
     */
@Override public void register(com.openxc.remote.VehicleServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Stop sending asynchronous measurement updates to a remote listener.
     *
     * Instances of VehicleManager should unregister themselves if they no
     * longer require real-time updates.
     */
@Override public void unregister(com.openxc.remote.VehicleServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregister, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Receive a new measurement that originates from an application.
     *
     * Applications may have alternative data sources that cannot be
     * instantiated in the remote process (e.g. a trace file playback source).
     * As an application's source receive updates, it can pass them back into
     * the remote process using this method.
     */
@Override public void receive(com.openxc.remote.RawMeasurement measurement) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((measurement!=null)) {
_data.writeInt(1);
measurement.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_receive, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * @return number of messages received since instantiation.
     */
@Override public int getMessageCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMessageCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addVehicleInterface(java.lang.String interfaceName, java.lang.String resource) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(interfaceName);
_data.writeString(resource);
mRemote.transact(Stub.TRANSACTION_addVehicleInterface, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeVehicleInterface(java.lang.String interfaceName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(interfaceName);
mRemote.transact(Stub.TRANSACTION_removeVehicleInterface, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * The one vehicle interface specific function, control whether polling is
     * used to find Bluetooth vehicle interfaces.
     *
     * TODO The add/remove vehicle interface API should probably be refactored
     * since it doesn't well support extra interface-specific parameters such as
     * this one, and inter-process communication via shared preferences isn't
     * very reliable.
     */
@Override public void setBluetoothPollingStatus(boolean enabled) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enabled)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setBluetoothPollingStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List<java.lang.String> getSourceSummaries() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSourceSummaries, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<java.lang.String> getSinkSummaries() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSinkSummaries, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<java.lang.String> getActiveSourceTypeStrings() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getActiveSourceTypeStrings, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void userPipelineActivated() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_userPipelineActivated, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void userPipelineDeactivated() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_userPipelineDeactivated, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_get = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_send = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_register = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregister = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_receive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getMessageCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_addVehicleInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_removeVehicleInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_setBluetoothPollingStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getSourceSummaries = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getSinkSummaries = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getActiveSourceTypeStrings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_userPipelineActivated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_userPipelineDeactivated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
}
/**
     * Retreive the most recent value for the measurement.
     *
     * @param measurementType must match the ID field of a known Measurement
     *                        subclass.
     * @return a RawMeasurement which may or may not have a value. This function
     *         will never return null, even if no value is available.
     */
public com.openxc.remote.RawMeasurement get(java.lang.String measurementType) throws android.os.RemoteException;
/**
     * Set a new value for the measurement class on the vehicle.
     *
     * @param measurement The measurement to set on the vehicle.
     */
public boolean send(com.openxc.remote.RawMeasurement measurement) throws android.os.RemoteException;
/**
     * Register to receive asynchronous updates when measurements are received.
     *
     * All instances of VehicleManager in application processes must register
     * themselves if they want to use the asynchronous interface.
     */
public void register(com.openxc.remote.VehicleServiceListener listener) throws android.os.RemoteException;
/**
     * Stop sending asynchronous measurement updates to a remote listener.
     *
     * Instances of VehicleManager should unregister themselves if they no
     * longer require real-time updates.
     */
public void unregister(com.openxc.remote.VehicleServiceListener listener) throws android.os.RemoteException;
/**
     * Receive a new measurement that originates from an application.
     *
     * Applications may have alternative data sources that cannot be
     * instantiated in the remote process (e.g. a trace file playback source).
     * As an application's source receive updates, it can pass them back into
     * the remote process using this method.
     */
public void receive(com.openxc.remote.RawMeasurement measurement) throws android.os.RemoteException;
/**
     * @return number of messages received since instantiation.
     */
public int getMessageCount() throws android.os.RemoteException;
public void addVehicleInterface(java.lang.String interfaceName, java.lang.String resource) throws android.os.RemoteException;
public void removeVehicleInterface(java.lang.String interfaceName) throws android.os.RemoteException;
/**
     * The one vehicle interface specific function, control whether polling is
     * used to find Bluetooth vehicle interfaces.
     *
     * TODO The add/remove vehicle interface API should probably be refactored
     * since it doesn't well support extra interface-specific parameters such as
     * this one, and inter-process communication via shared preferences isn't
     * very reliable.
     */
public void setBluetoothPollingStatus(boolean enabled) throws android.os.RemoteException;
public java.util.List<java.lang.String> getSourceSummaries() throws android.os.RemoteException;
public java.util.List<java.lang.String> getSinkSummaries() throws android.os.RemoteException;
public java.util.List<java.lang.String> getActiveSourceTypeStrings() throws android.os.RemoteException;
public void userPipelineActivated() throws android.os.RemoteException;
public void userPipelineDeactivated() throws android.os.RemoteException;
}
