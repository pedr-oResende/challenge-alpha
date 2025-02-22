package br.com.hurbandroidchallenge.presentation.screens.film.detail.ui

import br.com.hurbandroidchallenge.domain.model.Film
import br.com.hurbandroidchallenge.domain.model.People
import br.com.hurbandroidchallenge.domain.model.Planet

data class FilmDetailUI(
    val film: Film? = null,
    val characters: List<People> = emptyList(),
    val planets: List<Planet> = emptyList()
)