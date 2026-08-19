package com.example.model

enum class Platform(
    val displayName: String,
    val shortName: String,
    val defaultCategory: String
) {
    YOUTUBE("YouTube", "YouTube", "SEO & Discovery"),
    INSTAGRAM("Instagram", "Instagram", "Content"),
    FACEBOOK("Facebook", "Facebook", "Content")
}
