package com.example.assetmanagement

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.assetmanagement.base.testutils.MainCoroutinesRule
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule

abstract class BaseUnit {

    @get:Rule
    var coroutinesTestRule =  MainCoroutinesRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()


    @Before
    open fun setup() {
        MockKAnnotations.init(this)
    }
}