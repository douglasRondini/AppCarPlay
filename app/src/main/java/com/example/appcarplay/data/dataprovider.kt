package com.example.appcarplay.data

import com.example.appcarplay.domain.model.AppItem
import com.example.appcarplay.ui.theme.hboAccent
import com.example.appcarplay.ui.theme.mapsAccent
import com.example.appcarplay.ui.theme.searchAccent
import com.example.appcarplay.ui.theme.spotifyAccent
import com.example.appcarplay.ui.theme.youtubeAccent
import com.example.appcarplay.ui.theme.youtubeMusicAccent
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Google
import compose.icons.simpleicons.Googlemaps
import compose.icons.simpleicons.Hbo
import compose.icons.simpleicons.Spotify
import compose.icons.simpleicons.Youtube
import compose.icons.simpleicons.Youtubemusic

data class DataProvider(
    val apps: List<AppItem> = listOf(
        AppItem("YouTube", SimpleIcons.Youtube, "https://www.youtube.com/","com.google.android.youtube", youtubeAccent),
        AppItem("YT Music", SimpleIcons.Youtubemusic, "https://music.youtube.com/","com.google.android.apps.youtube.music", youtubeMusicAccent),
        AppItem("Spotify", SimpleIcons.Spotify, "https://open.spotify.com/", "com.spotify.music", spotifyAccent),
        AppItem("Google Maps", SimpleIcons.Googlemaps, "https://maps.google.com/", "com.google.android.apps.maps", mapsAccent),
        AppItem("HBO Max", SimpleIcons.Hbo, "https://play.hbomax.com/", "com.hbo.hbonow", hboAccent),
        AppItem("Google Search", SimpleIcons.Google, "https://www.google.com/", "com.google.android.googlequicksearchbox", searchAccent)
    )

) 