package com.mudash.musicapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.mudash.musicapp.ui.theme.MusicAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()
        val playlist = getPlayList()

        enableEdgeToEdge()
        setContent {
            MusicAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    SongScreen(playList = playlist)
                }
            }
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun SongScreen(playList: List<Music>) {
        val pagerState = rememberPagerState(pageCount = { playList.count() })
        val playingSongIndex =
            remember {
                mutableIntStateOf(0)
            }

        LaunchedEffect(pagerState.currentPage) {
            playingSongIndex.intValue = pagerState.currentPage
            player.seekTo(pagerState.currentPage, 0)
        }

        LaunchedEffect(player.currentMediaItemIndex) {
            playingSongIndex.intValue = player.currentMediaItemIndex
            pagerState.animateScrollToPage(
                playingSongIndex.intValue,
                animationSpec = tween(500),
            )
        }

        LaunchedEffect(Unit) {
            playList.forEach {
                val path = "android.resource://" + packageName + "/" + it.music
                val mediaItem = MediaItem.fromUri(Uri.parse(path))
                player.addMediaItem(mediaItem)
            }
        }
        player.prepare()

        // States
        val isPlaying =
            remember {
                mutableStateOf(false)
            }

        val currentPosition =
            remember {
                mutableLongStateOf(0)
            }

        val sliderPosition =
            remember {
                mutableLongStateOf(0)
            }

        val totalDuration =
            remember {
                mutableLongStateOf(0)
            }

        LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
            delay(1000)
            currentPosition.longValue = player.currentPosition
        }

        LaunchedEffect(currentPosition.longValue) {
            sliderPosition.longValue = currentPosition.longValue
        }

        LaunchedEffect(player.duration) {
            if (player.duration > 0) {
                totalDuration.longValue = player.duration
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val configuration = LocalConfiguration.current
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Animated description(song name and artist)
                AnimatedContent(
                    targetState = playingSongIndex.intValue,
                    transitionSpec = {
                        (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                    },
                    label = "",
                ) {
                    Text(
                        text = playList[it].name,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontWeight = FontWeight.ExtraBold),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedContent(targetState = playingSongIndex.intValue, transitionSpec = {
                    (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                }, label = "") {
                    Text(
                        text = playList[it].artist,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Album Cover for each song
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = pagerState,
                    pageSize = PageSize.Fixed((configuration.screenWidthDp / (1.7)).dp),
                    contentPadding = PaddingValues(horizontal = 85.dp),
                ) { page ->

                    val painter = painterResource(id = playList[page].cover)

                    if (page == pagerState.currentPage) {
                        VinylAlbumCoverAnimation(isSongPlaying = isPlaying.value, painter = painter)
                    } else {
                        VinylAlbumCoverAnimation(isSongPlaying = false, painter = painter)
                    }
                }
                Spacer(modifier = Modifier.height(54.dp))

                // Track Slider
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                ) {
                    TrackSlider(
                        value = sliderPosition.longValue.toFloat(),
                        onValueChange = {
                            sliderPosition.longValue = it.toLong()
                        },
                        onValueChangeFinished = {
                            currentPosition.longValue = sliderPosition.longValue
                            player.seekTo(sliderPosition.longValue)
                        },
                        songDuration = totalDuration.longValue.toFloat(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = (currentPosition.longValue).convertToText(),
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                        )

                        val remainTime = totalDuration.longValue - currentPosition.longValue
                        Text(
                            text = if (remainTime >= 0) remainTime.convertToText() else "",
                            modifier =
                                Modifier
                                    .padding(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Control buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ControlButton(icon = R.drawable.ic_previous, size = 40.dp, onClick = {
                        player.seekToPreviousMediaItem()
                    })
                    Spacer(modifier = Modifier.width(20.dp))
                    ControlButton(
                        icon = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play,
                        size = 100.dp,
                        onClick = {
                            if (isPlaying.value) {
                                player.pause()
                            } else {
                                player.play()
                            }
                            isPlaying.value = player.isPlaying
                        },
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    ControlButton(icon = R.drawable.ic_next, size = 40.dp, onClick = {
                        player.seekToNextMediaItem()
                    })
                }
            }
        }
    }

    // Track and visualize song playing actions
    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun TrackSlider(
        value: Float,
        onValueChange: (newValue: Float) -> Unit,
        onValueChangeFinished: () -> Unit,
        songDuration: Float,
    ) {
        Slider(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            onValueChangeFinished = {
                onValueChangeFinished()
            },
            valueRange = 0f..songDuration,
            colors =
                SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = Color.Gray,
                ),
        )
    }

    // Music player control button
    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun ControlButton(
        icon: Int,
        size: Dp,
        onClick: () -> Unit,
    ) {
        Box(
            modifier =
                Modifier
                    .size(size)
                    .clip(CircleShape)
                    .clickable {
                        onClick()
                    },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(size / 1.5f),
                painter = painterResource(id = icon),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun VinylAlbumCoverAnimation(
        isSongPlaying: Boolean = true,
        painter: Painter,
    ) {
        var currentRotation by remember {
            mutableFloatStateOf(0f)
        }

        val rotation =
            remember {
                Animatable(currentRotation)
            }

        LaunchedEffect(isSongPlaying) {
            if (isSongPlaying) {
                rotation.animateTo(
                    targetValue = currentRotation + 360f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(3000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart,
                        ),
                ) {
                    currentRotation = value
                }
            } else {
                if (currentRotation > 0f) {
                    rotation.animateTo(
                        targetValue = currentRotation + 50,
                        animationSpec =
                            tween(
                                1250,
                                easing = LinearOutSlowInEasing,
                            ),
                    ) {
                        currentRotation = value
                    }
                }
            }
        }
        VinylAlbumCover(
            painter = painter,
            rotationDegrees = rotation.value,
        )
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun VinylAlbumCover(
        modifier: Modifier = Modifier,
        painter: Painter,
        rotationDegrees: Float = 0f,
    ) {
        val roundedShape =
            object : Shape {
                override fun createOutline(
                    size: Size,
                    layoutDirection: LayoutDirection,
                    density: Density,
                ): Outline {
                    val p1 =
                        Path().apply {
                            addOval(Rect(4f, 3f, size.width - 1, size.height - 1))
                        }
                    val thickness = size.height / 2.10f
                    val p2 =
                        Path().apply {
                            addOval(
                                Rect(
                                    thickness,
                                    thickness,
                                    size.width - thickness,
                                    size.height - thickness,
                                ),
                            )
                        }
                    val p3 = Path()
                    p3.op(p1, p2, PathOperation.Difference)

                    return Outline.Generic(p3)
                }
            }

        Box(
            modifier =
                modifier
                    .aspectRatio(1.0f)
                    .clip(roundedShape),
        ) {
            Image(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .rotate(rotationDegrees),
                painter = painterResource(id = R.drawable.vinyl_background),
                contentDescription = "Vinyl Background",
            )

            // Overlaying song album cover over vinyl background
            Image(
                modifier =
                    Modifier
                        .fillMaxSize(0.5f)
                        .rotate(rotationDegrees)
                        .aspectRatio(1.0f)
                        .align(Alignment.Center)
                        .clip(roundedShape),
                painter = painter,
                contentDescription = "Song cover",
            )
        }
    }

    // Conversion of millisecond to string text
    private fun Long.convertToText(): String {
        val sec = this / 1000
        val minutes = sec / 60
        val seconds = sec % 60

        val minutesString =
            if (minutes < 10) {
                "0$minutes"
            } else {
                minutes.toString()
            }
        val secondsString =
            if (seconds < 10) {
                "0$seconds"
            } else {
                seconds.toString()
            }
        return "$minutesString:$secondsString"
    }
}
