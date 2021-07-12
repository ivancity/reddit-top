package com.ivan.m.reddittimeline.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ivan.m.reddittimeline.model.data.KeyCountModel
import com.ivan.m.reddittimeline.model.data.db.PostDatabase
import com.ivan.m.reddittimeline.model.data.db.Posts
import com.ivan.m.reddittimeline.model.data.db.RemoteKeys
import com.ivan.m.reddittimeline.services.ApiService
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class PostRemoteMediator(
    private val service: ApiService,
    private val database: PostDatabase,
    private val token: String
) : RemoteMediator<Int, Posts>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Posts>): MediatorResult {
        val keyCountModel: KeyCountModel = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                KeyCountModel(
                    keyName = remoteKeys?.prevKey,
                    counter = remoteKeys?.counter
                )
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return  MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                KeyCountModel(
                    keyName = prevKey,
                    counter = remoteKeys.counter
                )
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                KeyCountModel(
                    keyName = nextKey,
                    counter = remoteKeys.counter
                )
            }
        }

        try {
            val response = service.fetchPosts(
                token = "Bearer $token",
                after = keyCountModel.keyName,
                count = keyCountModel.counter
            )

            val postsResponse = response.data?.children ?: emptyList()
            val endOfPaginationReached = postsResponse.isNullOrEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.postDao().clearPosts()
                }
                val beforeKey = response.data?.before
                val afterKey = response.data?.after

                val counterResult = postsResponse.count() + (keyCountModel.counter ?: 0)
                var counter: Int? = null
                if (counterResult > 0) {
                    counter = counterResult
                }
                val keys = postsResponse.map {
                    RemoteKeys(
                        keyName = it.data.name,
                        prevKey = beforeKey,
                        nextKey = afterKey,
                        counter = counter
                    )
                }

                val posts = postsResponse.map {
                    Posts(id = it.data.name,
                        author = it.data.author,
                        created = it.data.created,
                        title = it.data.title,
                        commentsCounter = it.data.numComments,
                        thumbnail = it.data.thumbnail)
                }

                database.remoteKeysDao().insertAll(keys)
                database.postDao().insertAll(posts)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Posts>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { posts ->
                database.remoteKeysDao().remoteKeysKeyName(posts.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Posts>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { post ->
                database.remoteKeysDao().remoteKeysKeyName(post.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Posts>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { keyName ->
                database.remoteKeysDao().remoteKeysKeyName(keyName)
            }
        }
    }
}