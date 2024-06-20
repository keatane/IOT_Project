package com.island.iot

import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div
import it.skrape.selects.html5.h3
import it.skrape.selects.html5.source

data class NewsArticle(
    val title: String,
    val content: String,
    val imageUrl: String,
    val url: String
)

interface NewsDataSource {
    suspend fun getNews(): List<NewsArticle>
}

const val BASE_URL = "https://www.unwater.org"
const val NEWS_URL = "https://www.unwater.org/news"

class NewsDataSourceImpl : NewsDataSource {
    override suspend fun getNews() = skrape(AsyncFetcher) {
        request {
            url = NEWS_URL
        }
        response {
            htmlDocument {
                div {
                    withClass = "views-row"
                    findAll {
                        map {
                            NewsArticle(
                                title = it.h3 { findFirst { text } },
                                content = it.div {
                                    withClass = "field"
                                    findLast { text }
                                },
                                imageUrl = it.source {
                                    findFirst {
                                        BASE_URL + attribute("data-srcset")
                                    }
                                },
                                url = it.a {
                                    findFirst { attribute("href") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}