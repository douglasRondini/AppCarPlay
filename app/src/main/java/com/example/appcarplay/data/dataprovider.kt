package com.example.appcarplay.data

import com.example.appcarplay.domain.model.AppItem
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Google
import compose.icons.simpleicons.Googlemaps
import compose.icons.simpleicons.Hbo
import compose.icons.simpleicons.Spotify
import compose.icons.simpleicons.Youtube
import compose.icons.simpleicons.Youtubemusic

data class DataProvider(
    val apps: List<AppItem> = listOf(
        AppItem("YouTube", SimpleIcons.Youtube, "https://www.youtube.com/","com.google.android.youtube"),
        AppItem("YT Music", SimpleIcons.Youtubemusic, "https://music.youtube.com/","com.google.android.apps.youtube.music"),
        AppItem("Spotify", SimpleIcons.Spotify, "https://open.spotify.com/", "com.spotify.music"),
        AppItem("Google Maps", SimpleIcons.Googlemaps, "https://maps.google.com/", "com.google.android.apps.maps"),
        AppItem("HBO Max", SimpleIcons.Hbo, "https://play.hbomax.com/", "com.hbo.hbonow"),
        AppItem("Google Search", SimpleIcons.Google, "https://www.google.com/", "com.google.android.googlequicksearchbox")
    )
    
) 