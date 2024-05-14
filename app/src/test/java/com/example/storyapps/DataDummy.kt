package com.example.storyapps

import com.example.storyapps.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyItem = ListStoryItem(
                "https://bit.ly/4acjI2F",
                "2024-05-08T12:00:00",
                "Story $i",
                "Description for Story $i",
                0.0,
                i.toString(),
                0.0
            )
            items.add(storyItem)
        }
        return items
    }
}
