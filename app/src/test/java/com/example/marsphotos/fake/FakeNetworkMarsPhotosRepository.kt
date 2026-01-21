package com.example.marsphotos.fake

import com.example.marsphotos.data.MarsPhotosRepository
import com.example.marsphotos.network.MarsPhoto

class FakeNetworkMarsPhotosRepository : MarsPhotosRepository {
    override suspend fun getMarsPhotos(): List<MarsPhoto> {
        return FakeDataSource.fakeMarsPhotosList
    }
}

class NetworkErrorMarsPhotosRepository : MarsPhotosRepository {
    override suspend fun getMarsPhotos(): List<MarsPhoto> {
        throw Exception("Сетевая ошибка")
    }
}
