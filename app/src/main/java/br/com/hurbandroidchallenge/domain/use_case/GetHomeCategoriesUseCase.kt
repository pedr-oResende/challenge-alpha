package br.com.hurbandroidchallenge.domain.use_case

import br.com.hurbandroidchallenge.domain.repository.StarWarsBookRepository

class GetHomeCategoriesUseCase(
    private val repository: StarWarsBookRepository,
) {
    operator fun invoke() = repository.getHomeCategories()
}