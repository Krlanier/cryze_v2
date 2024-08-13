Hello!

This is a rough RTSP server for WYZE cameras of the GWELL variety

## preface
THANK YOU to Carson Loyal (carTloyal123) for the libraries to connect and get streams and pedroSG94 for RTSP related android libraries. I used the following repos:
- [cryze-android](https://github.com/carTloyal123/cryze-android) - the library for connecting to the cameras
- [cryze](https://github.com/carTloyal123/cryze) - scripts for getting tokens, capturing raw stream contents
- [RootEncoder](https://github.com/pedroSG94/RootEncoder) - library for streaming RTSP
- [RTSP-Server](https://github.com/pedroSG94/RTSP-Server) - library for serving RTSP streams

## Prereqs
- An x86 machine. I am using libhoudini in `redroid` to make the cryze android app work with the binaries for getting connections. This avoids the overhead of qemu or other android emulators.
- a kernel compatible with `redroid`. follow [this guide](https://github.com/remote-android/redroid-doc/blob/master/deploy/README.md), optionally starting a redroid container to confirm it works
- Wyze GWELL cameras. I've tested with `GW_GC1` (Wyze Cam OG) and `GW_BE1` (Wyze Cam Doorbell Pro	), 3 concurrent streams seems stable.
- Unknown: non-GWELL Wyze cameras. There are references to TOTK in some of the libraries, it might work, but then again, it might not. Let me know if it does work or if it doesn't so I can document it.

To use this, docker compose is easiest.
1) copy `sample.env` to `.env` - update your details. Wyze API keys can be shared with `wyze-bridge` NOTE: I messed up and didnt exactly use the same variable names as `wyze-bridge` and I need to fix this. as such, there are duplicates.
2) update the `docker-config.yml` with the redroid configs specific to your kernel and GPU. This could involve changing some props, it could involve volume mapping your GPU. I'm running on a Minisforum NAB6 with an intel i7-12650H on Arch with the Zen kernel.
3) Optional: Expose the ports you mapped your cameras to in `.env` from the android container - I personally just share a network between frigate and this project so that I don't need to.
4) build and start the thing:
```bash
docker compose build
docker compose up -d
```

you can view the android container over adb with something like scrcpy: `scrcpy -s localhost:5555` - that repo is [here](https://github.com/Genymobile/scrcpy)

## Support
I am not tech support, I am sorry, but I just do not have time. To debug the android half, you _will_ need to use logcat/a debugger/android studio. The `redroid` logs are _not_ flushed to the docker log, and debugging is difficult. 

## Development
I am using Android Studio for the android app, and just attaching to my remote docker-hosted `redroid` container (`adb connect [arch box ip address]:5555`). debugging/remote builds work, but container reboots will not persist your `/data` partition, so be sure to rebuild/restart with updated sources. (step 3 above)

## HELP NEEDED
- Top priotity: p2px, the native peer to peer library seems to not work, meaning this library is likely pulling down a bunch of traffic from wyze. I'd love to be independent of them to isolate us from API/firmware changes on their side. It seems like the native library tries to use p2px but crashes and falls back. There's some documentation (in chinese, google translate does cleanly translate it) on tencent's website and [github](https://github.com/tencentyun/) [IVideoPlayer (Chinese)](https://github.com/tencentyun/qcloud-documents/blob/ef02d912065fe452bba6a11c292cef2f306fe127/product/%E7%89%A9%E8%81%94%E7%BD%91/%E7%89%A9%E8%81%94%E7%BD%91%E6%99%BA%E8%83%BD%E8%A7%86%E9%A2%91%E6%9C%8D%E5%8A%A1/%E5%BA%94%E7%94%A8%E7%AB%AF%E6%8E%A5%E5%85%A5%E6%89%8B%E5%86%8C/%E5%AE%89%E5%8D%93%E5%BA%94%E7%94%A8%E7%AB%AF%E6%8E%A5%E5%85%A5/%E5%A4%9A%E5%AA%92%E4%BD%93.md) [p2p documentation](https://github.com/tencentyun/qcloud-documents/blob/ef02d912065fe452bba6a11c292cef2f306fe127/product/%E7%89%A9%E8%81%94%E7%BD%91/%E7%89%A9%E8%81%94%E7%BD%91%E6%99%BA%E8%83%BD%E8%A7%86%E9%A2%91%E6%9C%8D%E5%8A%A1/%E8%AE%BE%E5%A4%87%E6%8E%A5%E5%85%A5%E6%89%8B%E5%86%8CV2/P2P%20%E4%BC%A0%E8%BE%93%E6%A8%A1%E5%9D%97.md). 
- rewrite `RtspStream` to allow us to pass the raw packets from `AVData` on `IVideoDecoder.receive_frame` or `IVideoDecoder.send_packet` to remove _both_ `MediaCodec`s involved with _each_ camera's stream. This would significantly reduce the resource utilization.
- move the complete use of the python `wyze_sdk` library into the android app to allow the android app to run independent of the webservice.
- deal with camera events like disconnects/connects and stream death associated therein

## Use
The RTSP server has issues. All sockets are written on the same thread and there's multi-connection issues. Use go2rtc/frigate/something to keep the connection count down.

AGAIN: DO NOT DIRECTLY OPEN THESE RTSP CONNECTIONS.

My personal docker-compose.yml is combined with a frigate and wyze-bridge container to consolidate everything

```yaml
networks:
  frigate:
    driver: bridge

services:
  wyze-bridge:
    container_name: wyze-bridge
    restart: unless-stopped
    image: mrlt8/wyze-bridge:latest
    networks:
      - frigate
    ports:
      - 5004:5000 # WEB-UI
    environment:
      ON_DEMAND: True
      LLHLS: True
      WYZE_EMAIL: ${WYZE_EMAIL}
      WYZE_PASSWORD: ${WYZE_PASSWORD}
      API_ID: ${WYZE_API_ID}
      API_KEY: ${WYZE_API_KEY}
      WB_AUTH: False # Set to false to disable web and stream auth.
  frigate:
    container_name: frigate
    privileged: true # this may not be necessary for all setups
    restart: unless-stopped
    image: ghcr.io/blakeblackshear/frigate:stable
    shm_size: "256mb" # update for your cameras based on calculation above
    networks:
      - frigate
    devices:
      - /dev/bus/usb:/dev/bus/usb # Passes the USB Coral, needs to be modified for other versions
      - /dev/dri/renderD128:/dev/dri/renderD128 # For intel hwaccel, needs to be updated for your har>    volumes:
      - /etc/localtime:/etc/localtime:ro
      - ./config:/config
      - ./storage:/media/frigate
      - type: tmpfs # Optional: 1GB of memory, reduces SSD/SD Card wear
        target: /tmp/cache
        tmpfs:
          size: 1000000000
    ports:
      - "8971:8971"
      - "5005:5000" # Internal unauthenticated access. Expose carefully.
      - "8554:8554" # RTSP feeds
      - "8555:8555/tcp" # WebRTC over tcp
      - "8555:8555/udp" # WebRTC over udp
    environment:
      FRIGATE_RTSP_PASSWORD: "password"

  cryze_api:
    networks:
      - frigate
    build:
      context: ./cryze_api
      dockerfile: Dockerfile
    ports:
      - 8080:8080
    env_file:
      - .env

  cryze_android_app:
    networks:
      - frigate
    build:
      context: ./cryze_android_app
      dockerfile: Dockerfile
    privileged: true
    volumes:
      - /dev/dri/renderD128:/dev/dri/renderD128
    ports:
      - 5555:5555
    entrypoint: # need to override the entrypoint to run the app on arch, see the redroid docs
      - /init
      - qemu=1
      - androidboot.hardware=redroid
      - androidboot.use_memfd=1
      - androidboot.redroid_net_ndns=1
      - androidboot.redroid_net_dns1=127.0.0.11
      - androidboot.redroid_gpu_mode=host
      - androidboot.redroid_gpu_node=/dev/dri/renderD128
```

I am using frigate with a config like this:
```yaml
cameras:
  doorbell:
    enabled: true
    ffmpeg:
      retry_interval: 0
      inputs:
        - path: rtsp://cryze_android_app:8001
          input_args: preset-rtsp-restream
          roles:
            - detect
    detect:
      width: 1440
      height: 1440
      enabled: true
```

## License
- All files in `cryze_android_app/app/src/main/java/com/pedro` and copied works from `RootEncoder` and `RTSP-Server` remain licensed Apache v2 per the code's source repositories (linked above)
- Remaining files not from named repos above, or missing copyright headers are licenses GPL v3, see the copy of that license located [here](LICENSE)
