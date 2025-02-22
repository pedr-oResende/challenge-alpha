package br.com.hurbandroidchallenge.presentation.compose.navigation

sealed class Screens(route: String, argumentKey: String) : ScreenNavOperations(route, argumentKey) {

    object Home : Screens(
        route = "home",
        argumentKey = "home_key"
    )

    object Characters : Screens(
        route = "characters",
        argumentKey = "characters_key"
    )

    object CharacterDetail : Screens(
        route = "character_detail?character_detail_key={character_detail_key}",
        argumentKey = "character_detail_key"
    )

    object Films : Screens(
        route = "films",
        argumentKey = "films_key"
    )

    object FilmDetail : Screens(
        route = "film_detail?film_detail_key={film_detail_key}",
        argumentKey = "film_detail_key"
    )

    object Planets : Screens(
        route = "planets",
        argumentKey = "planets"
    )

    object PlanetDetail : Screens(
        route = "planet_detail?planet_detail={planet_detail}",
        argumentKey = "planet_detail"
    )

}