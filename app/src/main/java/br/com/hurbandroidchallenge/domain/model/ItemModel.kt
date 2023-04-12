package br.com.hurbandroidchallenge.domain.model

data class ItemModel(
    val image: String,
    val url: String,
    val fields: List<Pair<String, String>>
)