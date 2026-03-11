# MogileFS Configuration Fix

## Problem

When running `./objectcache.sh`, the application fails to start with the following error:

```
2026-03-11 13:22:39,225 [main] WARN objectcache.ObjectCache - Unable to connect to tracker at /127.0.0.1:22122
java.net.ConnectException: Connection refused (Connection refused)
    at java.net.PlainSocketImpl.socketConnect(Native Method)
    at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
    at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)
    at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)
    at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
    at java.net.Socket.connect(Socket.java:607)
    at com.projectgoth.fusion.mogilefs.Backend.getSocket(Backend.java:140)
    at com.projectgoth.fusion.mogilefs.Backend.reload(Backend.java:103)
```

## Root Cause

The issue occurs because:

1. **Inconsistent Configuration**: The `ObjectCache1.cfg` and `GatewayTCP_9119.cfg` files were configured to use `MogileFSTrackers=127.0.0.1:22122` (localhost), while other configuration files use `MogileFSTrackers=db1:7001`.

2. **Missing MogileFS Service**: MogileFS tracker service is not running on the local machine at port 22122.

3. **Fatal Initialization**: The ObjectCache application treats MogileFS initialization as a required dependency. If it cannot connect to the tracker, the application exits with error code 1 instead of starting with degraded functionality.

## Solution

Updated the MogileFS configuration in the following files to use the standard `db1:7001` tracker address:

### Files Modified

1. **etc/ObjectCache1.cfg** (lines 50-52)
   - Changed `MogileFSDomain` from `migx` to `mig33`
   - Changed `MogileFSTrackers` from `127.0.0.1:22122` to `db1:7001`

2. **etc/GatewayTCP_9119.cfg** (lines 70-72)
   - Changed `MogileFSDomain` from `migxchat` to `mig33`
   - Changed `MogileFSTrackers` from `127.0.0.1:22122` to `db1:7001`

## MogileFS Setup Requirements

MogileFS is a distributed file storage system used by the application for storing user-generated content (images, files, etc.). For the application to work properly, you need:

### Option 1: Set up MogileFS Tracker (Production)

1. Install and configure MogileFS on the `db1` server (or ensure it resolves to the correct host)
2. Start the MogileFS tracker on port 7001
3. Configure the `mig33` domain in MogileFS

### Option 2: Use Docker (Development)

The application is designed to run in a Docker environment where:
- `db1` resolves to the Docker container running MogileFS (typically at `172.17.0.2`)
- The MogileFS tracker service is running on port 7001
- Network connectivity is established between containers

### Option 3: Update DNS/Hosts (Local Development)

If running locally without Docker:

1. Add `db1` to your `/etc/hosts` file:
   ```
   127.0.0.1   db1
   ```

2. Install and run MogileFS tracker on port 7001:
   ```bash
   # Install MogileFS (Ubuntu/Debian)
   sudo apt-get install mogilefs-server mogilefs-utils

   # Or use Docker
   docker run -d --name mogilefs -p 7001:7001 mogilefs/mogilefs-tracker
   ```

## Configuration Reference

All MogileFS-related configuration files should now consistently use:

```properties
MogileFSDomain=mig33
MogileFSTrackers=db1:7001
```

### Files Using MogileFS:
- `etc/ObjectCache1.cfg` (fixed)
- `etc/ObjectCache2.cfg` (already correct)
- `etc/ObjectCache3.cfg` (already correct)
- `etc/ObjectCache4.cfg` (already correct)
- `etc/GatewayHTTP.cfg` (already correct)
- `etc/GatewayHTTP_83.cfg` (already correct)
- `etc/GatewayHTTP_84.cfg` (already correct)
- `etc/GatewayHTTP_88.cfg` (already correct)
- `etc/GatewayTCP_9119.cfg` (fixed)
- `etc/GatewayWS_9129.cfg` (already correct)
- `etc/ImageServer.cfg` (already correct)

## Testing

After applying this fix, the application will:

1. ✅ Use a consistent MogileFS configuration across all services
2. ✅ Connect to the `db1` tracker when it's available
3. ⚠️  Still fail to start if `db1` is not reachable (MogileFS is required)

To test the fix:

```bash
cd unixbin
./objectcache.sh
```

If `db1` is properly configured and MogileFS is running, the error should no longer appear. If `db1` is not available, you'll see a different error about being unable to connect to `db1:7001` instead of `127.0.0.1:22122`.

## Future Improvements

To make MogileFS truly optional (for development environments), the application code would need to be modified to:
1. Catch `NoTrackersException` during initialization
2. Set `mogileFSManager` to `null` or a stub implementation
3. Handle file operations gracefully when MogileFS is unavailable
4. Log warnings instead of treating it as a fatal error

This would require source code changes to `com.projectgoth.fusion.objectcache.ObjectCache` class (lines 174-244 in the decompiled bytecode).
