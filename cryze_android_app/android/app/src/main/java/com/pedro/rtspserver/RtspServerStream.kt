package com.pedro.rtspserver

import android.content.Context
import android.media.MediaCodec
import com.pedro.common.AudioCodec
import com.pedro.common.ConnectChecker
import com.pedro.common.VideoCodec
import com.pedro.library.base.StreamBase
import com.pedro.library.util.sources.audio.AudioSource
import com.pedro.library.util.sources.video.VideoSource
import com.pedro.rtspserver.server.RtspServer
import com.pedro.rtspserver.util.RtspServerStreamClient
import java.nio.ByteBuffer

/**
 * Created by pedro on 13/02/19.
 */
//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class RtspServerStream(
  context: Context, port: Int, connectChecker: ConnectChecker,
  videoSource: VideoSource, audioSource: AudioSource,
): StreamBase(context, videoSource, audioSource) {

  private val rtspServer = RtspServer(connectChecker, port)

  override fun onVideoInfoImp(sps: ByteBuffer, pps: ByteBuffer?, vps: ByteBuffer?) {
    val newSps = sps.duplicate()
    val newPps = pps?.duplicate()
    val newVps = vps?.duplicate()
    rtspServer.setVideoInfo(newSps, newPps, newVps)
  }

  override fun onAudioInfoImp(sampleRate: Int, isStereo: Boolean) {
    rtspServer.setAudioInfo(sampleRate, isStereo)
  }

  override fun startStreamImp(endPoint: String) {
    rtspServer.startServer()
  }

  override fun stopStreamImp() {
    rtspServer.stopServer()
  }

  override fun getVideoDataImp(videoBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
    rtspServer.sendVideo(videoBuffer, info)
  }

  override fun getAudioDataImp(audioBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
    rtspServer.sendAudio(audioBuffer, info)
  }

  override fun getStreamClient(): RtspServerStreamClient = RtspServerStreamClient(rtspServer)

  override fun setVideoCodecImp(codec: VideoCodec) {
    rtspServer.setVideoCodec(codec)
  }

  override fun setAudioCodecImp(codec: AudioCodec) {
    rtspServer.setAudioCodec(codec)
  }
}