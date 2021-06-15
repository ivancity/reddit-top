package com.ivan.m.reddittimeline.model.response
import com.google.gson.annotations.SerializedName

data class TopPostResponse (
    val kind: String? = null,
    val data: TopPostResponseData? = null
)

data class TopPostResponseData (
    val dist: Long? = null,
    val children: List<Child>? = null,
    val after: String? = null,
    val before: String? = null
)

data class Child (
    val kind: String? = null,
    val data: ChildData? = null
)

data class ChildData (
    @SerializedName("author_fullname")
    val authorFullname: String? = null,

    val clicked: Boolean? = null,
    val title: String? = null,
    val name: String? = null,
    val thumbnail: String? = null,

    @SerializedName("post_hint")
    val postHint: String? = null,

    val created: Long? = null,
    val preview: Preview? = null,
    val id: String? = null,
    val author: String? = null,

    @SerializedName("num_comments")
    val numComments: Long? = null,

    @SerializedName("send_replies")
    val sendReplies: Boolean? = null,

    val url: String? = null,

    @SerializedName("subreddit_subscribers")
    val subredditSubscribers: Long? = null,

    @SerializedName("created_utc")
    val createdUTC: Long? = null,

    @SerializedName("num_crossposts")
    val numCrossposts: Long? = null,

    @SerializedName("is_video")
    val isVideo: Boolean? = null
)

data class Preview (
    val images: List<Image>? = null,
    val enabled: Boolean? = null
)

data class Image (
    val source: Source? = null,
    val resolutions: List<Source>? = null,
    val id: String? = null
)

data class Source (
    val url: String? = null,
    val width: Long? = null,
    val height: Long? = null
)

