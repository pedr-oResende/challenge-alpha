package br.com.hurbandroidchallenge.data.repository

import br.com.hurbandroidchallenge.commom.extension.idFromUrl
import br.com.hurbandroidchallenge.commom.extension.pagedListOf
import br.com.hurbandroidchallenge.commom.mapper.NullableListMapper
import br.com.hurbandroidchallenge.commom.mapper.PagedListMapper
import br.com.hurbandroidchallenge.data.local.data_source.CharactersLocalDataSource
import br.com.hurbandroidchallenge.data.local.model.PeopleEntity
import br.com.hurbandroidchallenge.data.local.preferences.PreferencesWrapper
import br.com.hurbandroidchallenge.data.mapper.characters.toEntity
import br.com.hurbandroidchallenge.data.mapper.characters.toPeople
import br.com.hurbandroidchallenge.data.remote.data_sources.StarWarsBookRemoteDataSource
import br.com.hurbandroidchallenge.data.remote.model.PeopleDto
import br.com.hurbandroidchallenge.data.remote.util.NetworkManager
import br.com.hurbandroidchallenge.domain.model.PagedList
import br.com.hurbandroidchallenge.domain.model.People
import br.com.hurbandroidchallenge.domain.repository.StarWarsBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CharactersRepository(
    private val remoteDataSource: StarWarsBookRemoteDataSource,
    private val localDataSource: CharactersLocalDataSource,
    private val peopleDtoToEntityMapper: PagedListMapper<PeopleDto, PeopleEntity>,
    private val peopleEntityToPeopleMapper: NullableListMapper<PeopleEntity, People>,
    private val networkManager: NetworkManager,
) : StarWarsBookRepository<People> {

    private val preferences = PreferencesWrapper.getInstance()

    override fun getItemList(url: String, clearLocalDatasource: Boolean): Flow<PagedList<People>> {
        return flow {
            if (clearLocalDatasource)
                localDataSource.clearEntities()
            if (preferences.isCharactersUpToDate()) {
                emit(pagedListOf(getLocalCharacters()))
            } else if (networkManager.hasInternetConnection()) {
                val remoteCharacters = remoteDataSource.getCharacters(url)
                localDataSource.insertEntities(peopleDtoToEntityMapper.map(remoteCharacters).results)
                if (remoteCharacters.next == null)
                    preferences.charactersIsUpToDate()
                emit(
                    PagedList(
                        next = remoteCharacters.next,
                        previous = remoteCharacters.previous,
                        results = getLocalCharacters()
                    )
                )
            } else {
                emit(pagedListOf(getLocalCharacters()))
            }
        }
    }

    override fun getItemByUrl(url: String): Flow<People> {
        return flow {
            if (localDataSource.containsEntity(url.idFromUrl())) {
                emit(getLocalCharacterByUrl(url))
            } else if (networkManager.hasInternetConnection()) {
                val remoteCharacter = remoteDataSource.getCharacterByUrl(url)
                localDataSource.insertEntities(listOf(remoteCharacter.toEntity()))
                val localCharacter = getLocalCharacterByUrl(remoteCharacter.url.orEmpty())
                emit(localCharacter)
            } else {
                getLocalCharacterByUrl(url)
            }
        }
    }

    override fun getFavoriteItems(): Flow<List<People>> {
        return flow { emit(peopleEntityToPeopleMapper.map(localDataSource.getFavoriteEntities())) }
    }

    override fun getLastSeenItems(): Flow<List<People>> {
        return flow { emit(peopleEntityToPeopleMapper.map(localDataSource.getLastSeenEntities())) }
    }

    override fun updateItem(item: People): Flow<People> {
        return flow { emit(localDataSource.updateEntity(item.toEntity()).toPeople()) }
    }

    private suspend fun getLocalCharacters() =
        peopleEntityToPeopleMapper.map(localDataSource.getEntities())

    private suspend fun getLocalCharacterByUrl(url: String) =
        localDataSource.getEntityById(url.idFromUrl()).toPeople()

}