package com.example.rickandmortyexplorer.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun CharacterAsyncImage(
    imageUrl: String,
    contentDescription: String,
    placeholderText: String,
    modifier: Modifier = Modifier,
    placeholderTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
    placeholderModifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    val placeholder: @Composable () -> Unit = {
        Text(
            text = placeholderText,
            style = placeholderTextStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = placeholderModifier
        )
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (isPreview || imageUrl.isBlank()) {
            placeholder()
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                },
                success = { SubcomposeAsyncImageContent() },
                error = { placeholder() }
            )
        }
    }
}

