/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.marsphotos.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.marsphotos.R
import com.example.marsphotos.network.MarsPhoto
import com.example.marsphotos.ui.theme.MarsPhotosTheme

@Composable
fun HomeScreen(
    marsUiState: MarsUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    var selectedPhoto by rememberSaveable { mutableStateOf<MarsPhoto?>(null) }

    if (selectedPhoto != null) {
        BackHandler {
            selectedPhoto = null
        }
        FullScreenPhoto(
            photo = selectedPhoto!!,
            onDismiss = { selectedPhoto = null }
        )
    } else {
        when (marsUiState) {
            is MarsUiState.Error -> ErrorScreen(retryAction = retryAction,modifier = modifier.fillMaxSize())
            is MarsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
            is MarsUiState.Success -> PhotosGridScreen(
                photos = marsUiState.photos,
                modifier = modifier.fillMaxWidth(),
                contentPadding = contentPadding,
                onPhotoClick = { photo -> selectedPhoto = photo }
            )
        }
    }
}

@Composable
fun FullScreenPhoto(
    photo: MarsPhoto,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.imgSrc)
                .crossfade(true)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .build(),
            contentDescription = stringResource(R.string.mars_photo),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    Image(
        modifier = modifier
            .size(200.dp)
            .rotate(rotation),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )

//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = modifier.fillMaxSize()
//    ) {
//        CircularProgressIndicator() // Стандартный индикатор загрузки Material Design
//    }
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun MarsPhotoCard(
    photo: MarsPhoto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(photo.imgSrc)
                .crossfade(true)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .build(),
            contentDescription = stringResource(R.string.mars_photo),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun PhotosGridScreen(
    photos: List<MarsPhoto>,
    onPhotoClick: (MarsPhoto) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(horizontal = 4.dp),
        contentPadding = contentPadding
    ) {
        items(items = photos, key = { photo -> photo.id }) { photo ->
            MarsPhotoCard(
                photo = photo,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .clickable { onPhotoClick(photo) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    MarsPhotosTheme {
        val mockData = List(10) { MarsPhoto("$it", "image_$it") }
        PhotosGridScreen(
            photos = mockData,
            onPhotoClick = {}
        )
    }
}
