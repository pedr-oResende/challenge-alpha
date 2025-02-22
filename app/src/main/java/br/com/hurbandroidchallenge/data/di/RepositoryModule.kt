package br.com.hurbandroidchallenge.data.di

import br.com.hurbandroidchallenge.commom.mapper.NullableListMapperImpl
import br.com.hurbandroidchallenge.commom.mapper.PagedListMapperImpl
import br.com.hurbandroidchallenge.data.mapper.characters.PeopleDtoToEntityMapper
import br.com.hurbandroidchallenge.data.mapper.characters.PeopleEntityToPeopleMapper
import br.com.hurbandroidchallenge.data.mapper.films.FilmDtoToEntityMapper
import br.com.hurbandroidchallenge.data.mapper.films.FilmEntityToFilmMapper
import br.com.hurbandroidchallenge.data.mapper.planets.PlanetDtoToEntityMapper
import br.com.hurbandroidchallenge.data.mapper.planets.PlanetEntityToPlanetMapper
import br.com.hurbandroidchallenge.data.remote.util.NetworkManager
import br.com.hurbandroidchallenge.data.repository.CharactersRepository
import br.com.hurbandroidchallenge.data.repository.FilmsRepository
import br.com.hurbandroidchallenge.data.repository.PlanetsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single {
        NetworkManager(get())
    }

    single {
        CharactersRepository(
            peopleDtoToEntityMapper = PagedListMapperImpl(
                listMapper = NullableListMapperImpl(
                    mapper = get<PeopleDtoToEntityMapper>()
                )
            ),
            peopleEntityToPeopleMapper = NullableListMapperImpl(
                mapper = get<PeopleEntityToPeopleMapper>()
            ),
            remoteDataSource = get(),
            localDataSource = get(),
            networkManager = get()
        )
    }

    single {
        FilmsRepository(
            filmDtoToEntityMapper = PagedListMapperImpl(
                listMapper = NullableListMapperImpl(
                    mapper = get<FilmDtoToEntityMapper>()
                )
            ),
            filmEntityToPeopleMapper = NullableListMapperImpl(
                mapper = get<FilmEntityToFilmMapper>()
            ),
            remoteDataSource = get(),
            localDataSource = get(),
            networkManager = get()
        )
    }

    single {
        PlanetsRepository(
            planetDtoToEntityMapper = PagedListMapperImpl(
                listMapper = NullableListMapperImpl(
                    mapper = get<PlanetDtoToEntityMapper>()
                )
            ),
            planetEntityToPlanetMapper = NullableListMapperImpl(
                mapper = get<PlanetEntityToPlanetMapper>()
            ),
            networkManager = get(),
            localDataSource = get(),
            remoteDataSource = get()
        )
    }

}