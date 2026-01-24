package com.pipe.richpipi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
