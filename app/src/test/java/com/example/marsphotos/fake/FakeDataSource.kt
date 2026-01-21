package com.example.marsphotos.fake

import com.example.marsphotos.network.MarsPhoto

object FakeDataSource {
    val fakeMarsPhotosList = List(24) { MarsPhoto("$it", "img_src_$it") }
}