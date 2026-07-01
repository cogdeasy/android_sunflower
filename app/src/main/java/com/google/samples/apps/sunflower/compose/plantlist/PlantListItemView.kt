/*
 * Copyright 2022 Google LLC
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

package com.google.samples.apps.sunflower.compose.plantlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.palette.graphics.Palette
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.UnsplashPhoto

@Composable
fun PlantListItem(plant: Plant, onClick: () -> Unit) {
    ImageListItem(name = plant.name, imageUrl = plant.imageUrl, onClick = onClick)
}

@Composable
fun PhotoListItem(photo: UnsplashPhoto, onClick: () -> Unit) {
    ImageListItem(name = photo.user.name, imageUrl = photo.urls.small, onClick = onClick)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageListItem(name: String, imageUrl: String, onClick: () -> Unit) {
    val defaultColor = MaterialTheme.colorScheme.secondaryContainer
    val dominantColor = remember(imageUrl) { mutableStateOf(defaultColor) }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = dominantColor.value),
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = R.dimen.card_bottom_margin))
    ) {
        Column(Modifier.fillMaxWidth()) {
            GlideImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.a11y_plant_item_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.plant_item_image_height)),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.ic_plant_placeholder),
                failure = placeholder(R.drawable.ic_plant_placeholder),
            ) {
                it.addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean = false

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        val bitmap = when (resource) {
                            is BitmapDrawable -> resource.bitmap
                            else -> null
                        }
                        bitmap?.let { bmp ->
                            val scaled = Bitmap.createScaledBitmap(bmp, 50, 50, false)
                            val palette = Palette.from(scaled).generate()
                            val swatch = palette.mutedSwatch
                                ?: palette.dominantSwatch
                            swatch?.let {
                                dominantColor.value = Color(it.rgb).copy(alpha = 0.25f)
                            }
                        }
                        return false
                    }
                })
            }
            Text(
                text = name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.margin_normal))
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}
