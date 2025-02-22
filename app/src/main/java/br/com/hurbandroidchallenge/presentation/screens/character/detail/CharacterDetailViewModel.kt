package br.com.hurbandroidchallenge.presentation.screens.character.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.hurbandroidchallenge.data.mapper.films.toSmallModel
import br.com.hurbandroidchallenge.domain.model.People
import br.com.hurbandroidchallenge.domain.model.Planet
import br.com.hurbandroidchallenge.domain.use_case.characters.GetCharacterByUrlUseCase
import br.com.hurbandroidchallenge.domain.use_case.characters.SetCharacterLastSeenUseCase
import br.com.hurbandroidchallenge.domain.use_case.characters.SetFavoriteCharacterUseCase
import br.com.hurbandroidchallenge.domain.use_case.films.GetFilmByUrlUseCase
import br.com.hurbandroidchallenge.domain.use_case.planets.GetPlanetByUrlUseCase
import br.com.hurbandroidchallenge.presentation.model.SmallItemModel
import br.com.hurbandroidchallenge.presentation.model.StateUI
import br.com.hurbandroidchallenge.presentation.screens.character.detail.ui.CharacterUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val getCharacterByUrlUseCase: GetCharacterByUrlUseCase,
    private val getFilmByUrlUseCase: GetFilmByUrlUseCase,
    private val getPlanetByUrlUseCase: GetPlanetByUrlUseCase,
    private val setCharacterLastSeenUseCase: SetCharacterLastSeenUseCase,
    private val setFavoriteCharacterUseCase: SetFavoriteCharacterUseCase,
    url: String,
) : ViewModel() {

    private val _characterState = MutableStateFlow<StateUI<People>>(StateUI.Idle())
    val characterState = _characterState.asStateFlow()

    private val _filmsState = MutableStateFlow<StateUI<List<SmallItemModel>>>(StateUI.Idle())
    val filmsState = _filmsState.asStateFlow()

    private val _homeWorld = MutableStateFlow<StateUI<Planet>>(StateUI.Idle())

    private val _characterUI = mutableStateOf(CharacterUI())
    val characterUI: State<CharacterUI> = _characterUI

    init {
        loadCharacter(url)
    }

    private fun loadCharacter(url: String) {
        viewModelScope.launch {
            getCharacterByUrlUseCase(url).onStart {
                _characterState.emit(StateUI.Processing())
            }.catch {
                _characterState.emit(StateUI.Error(it.message.orEmpty()))
            }.collect { data ->
                setCharacterLastSeenUseCase(data).collect { character ->
                    _characterUI.value = characterUI.value.copy(character = character)
                    loadFilms(character.films)
                    loadHomeWorld(character.homeWorld)
                    _characterState.emit(StateUI.Processed(character))
                }
            }
        }
    }

    private fun loadFilms(urls: List<String>) {
        viewModelScope.launch {
            urls.forEach { urls ->
                getFilmByUrlUseCase(urls).onStart {
                    _filmsState.emit(StateUI.Processing())
                }.catch {
                    _filmsState.emit(StateUI.Error())
                }.collect { data ->
                    _characterUI.value = characterUI.value.copy(
                        films = characterUI.value.films.plus(data)
                    )
                }
            }
            if (_characterUI.value.films.isEmpty())
                _filmsState.emit(StateUI.Error("This character isn't related with any movie"))
            else
                _filmsState.emit(StateUI.Processed(_characterUI.value.films.map { it.toSmallModel() }))
        }
    }

    private fun loadHomeWorld(url: String) {
        viewModelScope.launch {
            getPlanetByUrlUseCase(url).onStart {
                _homeWorld.emit(StateUI.Processing())
            }.catch {
                _homeWorld.emit(StateUI.Error())
            }.collect { data ->
                _characterUI.value = characterUI.value.copy(homeWorld = data)
                _homeWorld.emit(StateUI.Processed(data = data))
            }
        }
    }

    fun favorite() {
        viewModelScope.launch {
            _characterUI.value.character?.let { character ->
                setFavoriteCharacterUseCase(character).collect { updatedCharacter ->
                    _characterUI.value = characterUI.value.copy(character = updatedCharacter)
                }
            }
        }
    }

}