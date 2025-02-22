package br.com.hurbandroidchallenge.data.mapper.films

import br.com.hurbandroidchallenge.commom.mapper.Mapper
import br.com.hurbandroidchallenge.data.local.model.FilmEntity
import br.com.hurbandroidchallenge.domain.model.Film

class FilmEntityToFilmMapper : Mapper<FilmEntity, Film> {
    override fun map(input: FilmEntity) = input.toFilm()
}