package com.mudash.musicapp

fun getPlayList(): List<Music>  {
    return listOf(
        Music(
            name = "Forever",
            artist = "Chris Brown",
            cover = R.drawable.forever_album_cover,
            music = R.raw.forever,
        ),
        Music(
            name = "Surround",
            artist = "J.I.D ft 21 Savage ft Baby Tate",
            cover = R.drawable.jid_surround_album_cover,
            music = R.raw.jid_surround,
        ),
        Music(
            name = "Like That",
            artist = "Metro Boomin ft Future ft Kendrik Lamar",
            cover = R.drawable.like_that_official,
            music = R.raw.like_that,
        ),
        Music(
            name = "My Eyes",
            artist = "Travis Scott",
            cover = R.drawable.my_eyes_album_cover,
            music = R.raw.my_eyes,
        ),
        Music(
            name = "NightCrawler",
            artist = "Travis Scott",
            cover = R.drawable.night_crawler_album_cover,
            music = R.raw.night_crawler,
        ),
        Music(
            name = "Save Your Tears",
            artist = "The Weekend",
            cover = R.drawable.save_your_tears_album_cover,
            music = R.raw.save_your_tears,
        ),
        Music(
            name = "We still don't trust you",
            artist = "Metro Boomin ft Future ft Abel",
            cover = R.drawable.dont_trust_you_album_cover,
            music = R.raw.we_still_dont_trust_you,
        ),
    )
}
