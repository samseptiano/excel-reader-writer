package com.example.assetmanagement.base.apihandler.exception

/**
 * HandleableException is exception which is able to be handled manually
 * Manual handling can be done in the presentation (activity/fragment)
 * On the ResultState.onError(error) { /* Do manual handling here */ }
 */
abstract class HandleableException: Exception()